package me.itzjustsamu.playerskills;

import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import me.itzjustsamu.playerskills.command.SkillsAdminCommand;
import me.itzjustsamu.playerskills.command.SkillsCommand;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.config.MessageConfig;
import me.itzjustsamu.playerskills.fundingsource.FundingSource;
import me.itzjustsamu.playerskills.fundingsource.VaultFundingSource;
import me.itzjustsamu.playerskills.fundingsource.XPFundingSource;
import me.itzjustsamu.playerskills.listener.PlayerListener;
import me.itzjustsamu.playerskills.menu.MenuController;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.storage.FlatFileStorage;
import me.itzjustsamu.playerskills.storage.PlayerStorage;
import me.itzjustsamu.playerskills.skill.Skill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Supplier;

public class PlayerSkills extends BasePlugin {
    public static final Map<String, Supplier<FundingSource>> FUNDING_SOURCE_MAP = new CaseInsensitiveStringHashMap<>();
    public static final Map<String, Supplier<PlayerStorage>> PLAYER_STORAGE_MAP = new CaseInsensitiveStringHashMap<>();

    private final MessageConfig messageConfig = new MessageConfig(this);
    private final MainConfig mainConfig = new MainConfig(this);
    private final Map<String, Skill> skills = new ConcurrentHashMap<>();
    private final Map<String, Skill> disabledSkills = new ConcurrentHashMap<>();
    private final Logger logger = getLogger();

    public MessageConfig getMessageConfig() {
        return messageConfig;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    @Override
    public void preLoad() {
        FUNDING_SOURCE_MAP.put("XP", XPFundingSource::new);
        FUNDING_SOURCE_MAP.put("VAULT", VaultFundingSource::new);
        PLAYER_STORAGE_MAP.put("FLAT_FILE", FlatFileStorage::new);
    }

    @Override
    public void load() {
        MessageUtils.setPrefix(MessageConfig.PREFIX::getValue);
        messageConfig.setup();
        mainConfig.setup();
    }

    @Override
    public void enable() {
        registerSkills();
        registerCommand(new SkillsCommand(this));
        registerCommand(new SkillsAdminCommand(this));
        registerListener(new MenuController());
        registerListener(new PlayerListener());
    }

    private void loadSkillsFromConfig() {
        BukkitConfig skillsConfig = new BukkitConfig(this, "SkillsSettings.yml");
        skillsConfig.setup();

        ConfigurationSection skillSection = skillsConfig.getOriginal();
        if (skillSection != null && skillSection.contains("skills")) {
            ConfigurationSection skills = skillSection.getConfigurationSection("skills");

            if (skills != null) {
                for (String skillName : skills.getKeys(false)) {
                    ConfigurationSection skillEntry = skills.getConfigurationSection(skillName);
                    assert skillEntry != null;

                    boolean isEnabled = skillEntry.getBoolean("enable", true);

                    if (isEnabled) {
                        // Continue with the existing code for enabled skills
                        registerSkill(skillName, skillEntry.getString("class"));
                    } else {
                        // Skill is disabled, add it to disabledSkills map
                        try {
                            Class<?> skillClass = Class.forName(skillEntry.getString("class"));
                            Constructor<?> constructor = skillClass.getConstructor(PlayerSkills.class);
                            Skill skill = (Skill) constructor.newInstance(this);

                            disabledSkills.put(skill.getConfigName(), skill);
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Error registering disabled skill: " + skillName, e);
                        }
                    }
                }
            }
        }
    }


    private Skill createSkillInstance(String skillClassName) throws Exception {
        Class<?> skillClass = Class.forName(skillClassName);
        if (!Skill.class.isAssignableFrom(skillClass)) {
            throw new IllegalArgumentException("Invalid skill class: " + skillClassName);
        }
        Constructor<?> constructor = skillClass.getConstructor(PlayerSkills.class);
        return (Skill) constructor.newInstance(this);
    }

    private void registerSkills() {
        loadSkillsFromConfig();
    }

    private void registerSkill(String skillName, String skillClassName) {
        if (disabledSkills.containsKey(skillName)) {
            return;  // Skill is disabled
        }

        try {
            Skill skill = createSkillInstance(skillClassName);
            skills.put(skill.getConfigName(), skill);
            skill.setup();
            skill.enable();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error registering skill: " + skillName, e);
        }
    }

    @Override
    public void postEnable() {
        this.startAutoSaveTask();
    }

    @Override
    protected List<Class<?>> getPermissionClasses() {
        return Collections.singletonList(Permissions.class);
    }

    private void startAutoSaveTask() {
        long ticks = MainConfig.OPTIONS_AUTO_SAVE_TICKS.getValue();
        if (ticks >= 0) {
            boolean async = MainConfig.OPTIONS_AUTO_SAVE_ASYNC.getValue();
            Runnable runnable = () -> {
                List<SPlayer> list = new ArrayList<>(SPlayer.getPlayers().values());
                for (SPlayer player : list) {
                    SPlayer.save(player);
                }
            };
            Scheduler.CURRENT.runTaskTimer(this, runnable, ticks, ticks, async);
        }
    }

    // Inside the PlayerSkills class
    @Override
    public void disable() {
        for (SPlayer player : SPlayer.getPlayers().values()) {
            SPlayer.save(player);
        }

        // Save the current state of skill enable/disable to the SkillsSettings.yml file
        saveSkillSettings();

        skills.values().forEach(skill -> {
            skill.disable();
            HandlerList.unregisterAll(skill);
        });
        skills.clear();
    }

    public void saveSkillSettings() {
        BukkitConfig skillsConfig = new BukkitConfig(this, "SkillsSettings.yml");
        skillsConfig.setup();

        ConfigurationSection skillsSection = skillsConfig.getOriginal().getConfigurationSection("skills");
        if (skillsSection != null) {
            for (Map.Entry<String, Skill> entry : disabledSkills.entrySet()) {
                String skillName = entry.getKey();
                boolean isDisabled = skillsSection.getBoolean(skillName + ".enable", false);

                // Save the modified configuration with the new value
                skillsSection.set(skillName + ".enable", isDisabled);

                // Update the in-memory state of disabled skills
                if (isDisabled) {
                    entry.getValue().disable(); // Optional: Disable the skill if it was enabled
                }
            }

            skillsConfig.save();
        }
    }

    public Map<String, Skill> getSkills() {
        return skills;
    }

    public Map<String, Skill> getDisabledSkills() {
        return disabledSkills;
    }
}