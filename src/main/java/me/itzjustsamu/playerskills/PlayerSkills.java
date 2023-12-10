package me.itzjustsamu.playerskills;


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
import me.itzjustsamu.playerskills.listener.ItemDamageEvent;
import me.itzjustsamu.playerskills.listener.ItemMendEvent;
import me.itzjustsamu.playerskills.listener.PlayerListener;
import me.itzjustsamu.playerskills.menu.MenuController;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.skill.Skill;
import me.itzjustsamu.playerskills.storage.FlatFileStorage;
import me.itzjustsamu.playerskills.storage.PlayerStorage;
import me.itzjustsamu.playerskills.util.Updater;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        registerListener(new ItemDamageEvent());
        registerListener(new ItemMendEvent());

        Updater updater = new Updater(this, 113626);
        updater.checkForUpdates();

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
                        String lowerCaseSkillName = skillName.toLowerCase();
                        registerSkill(lowerCaseSkillName, skillEntry.getString("class"));
                    } else {
                        try {
                            Class<?> skillClass = Class.forName(skillEntry.getString("class"));
                            Constructor<?> constructor = skillClass.getConstructor(PlayerSkills.class);
                            Skill skill = (Skill) constructor.newInstance(this);
                            String lowerCaseSkillName = skillName.toLowerCase();
                            disabledSkills.put(lowerCaseSkillName, skill);
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
        disabledSkills.clear();
        loadSkillsFromConfig();
    }


    private void registerSkill(String skillName, String skillClassName) {
        String lowerCaseSkillName = skillName.toLowerCase();

        if (disabledSkills.containsKey(lowerCaseSkillName)) {
            return;
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

    @Override
    public void disable() {
        for (SPlayer player : SPlayer.getPlayers().values()) {
            SPlayer.save(player);
        }

        for (Skill skill : skills.values()) {
            skill.disable();
        }

        HandlerList.unregisterAll(this);

        skills.clear();
    }



    public Map<String, Skill> getSkills() {
        return skills;
    }

    public Map<String, Skill> getDisabledSkills() {
        return disabledSkills;
    }
}