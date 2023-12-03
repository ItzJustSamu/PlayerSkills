package me.itzjustsamu.playerskills;

import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
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
import org.bukkit.event.HandlerList;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Supplier;

public class PlayerSkills extends BasePlugin {
    public static final Map<String, Supplier<FundingSource>> FUNDING_SOURCE_MAP = new CaseInsensitiveStringHashMap<>();
    public static final Map<String, Supplier<PlayerStorage>> PLAYER_STORAGE_MAP = new CaseInsensitiveStringHashMap<>();

    private final MessageConfig messageConfig = new MessageConfig(this);
    private final MainConfig mainConfig = new MainConfig(this);
    private final Map<String, Skill> skillRegistrar = new ConcurrentHashMap<>();
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
        try (InputStream input = getResource("SkillsSettings.yml")) {
            Yaml yaml = new Yaml();
            Map<String, List<Map<String, String>>> skillMap = yaml.load(input);

            if (skillMap != null && skillMap.containsKey("skills")) {
                List<Map<String, String>> skills = skillMap.get("skills");

                for (Map<String, String> skillEntry : skills) {
                    String skillName = skillEntry.get("name");
                    String skillClassName = skillEntry.get("class");
                    registerSkill(skillName, skillClassName);
                }
            }
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error loading skills from config", e);
        }
    }


    private void registerSkills() {
        loadSkillsFromConfig();
    }

    private void registerSkill(String skillName, String skillClassName) {
        if (disabledSkills.containsKey(skillName)) {
            return;  // Skill is disabled
        }

        try {
            Class<?> skillClass = Class.forName(skillClassName);
            Constructor<?> constructor = skillClass.getConstructor(PlayerSkills.class);
            Skill skill = (Skill) constructor.newInstance(this);

            skillRegistrar.put(skill.getConfigName(), skill);
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
        skillRegistrar.values().forEach(skill -> {
            skill.disable();
            HandlerList.unregisterAll(skill);
        });
        skillRegistrar.clear();
    }


    public void disableSkill(Skill skill) {
        // Save players
        for (SPlayer player : SPlayer.getPlayers().values()) {
            SPlayer.save(player);
        }

        // Disable the specific skill
        skill.disable();
        disabledSkills.put(skill.getConfigName(), skill);

        // Reload or perform any other necessary cleanup for the skill
        skill.reload();
    }

    public void enableSkill(String skillName) {
        Skill skill = disabledSkills.remove(skillName);
        if (skill != null) {
            skillRegistrar.put(skill.getConfigName(), skill);
            skill.enable();
        }
    }

    public Map<String, Skill> getSkillRegistrar() {
        return skillRegistrar;
    }

    public Map<String, Skill> getDisabledSkills() {
        return disabledSkills;
    }
}