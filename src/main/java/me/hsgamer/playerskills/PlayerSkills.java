package me.hsgamer.playerskills;

import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.playerskills.command.SkillsAdminCommand;
import me.hsgamer.playerskills.command.SkillsCommand;
import me.hsgamer.playerskills.config.MainConfig;
import me.hsgamer.playerskills.config.MessageConfig;
import me.hsgamer.playerskills.fundingsource.FundingSource;
import me.hsgamer.playerskills.fundingsource.VaultFundingSource;
import me.hsgamer.playerskills.fundingsource.XPFundingSource;
import me.hsgamer.playerskills.listener.PlayerListener;
import me.hsgamer.playerskills.menu.MenuController;
import me.hsgamer.playerskills.player.SPlayer;
import me.hsgamer.playerskills.skill.*;
import me.hsgamer.playerskills.storage.FlatFileStorage;
import me.hsgamer.playerskills.storage.PlayerStorage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Supplier;

public class PlayerSkills extends BasePlugin {
    public static final Map<String, Supplier<FundingSource>> FUNDING_SOURCE_MAP = new CaseInsensitiveStringHashMap<>();
    public static final Map<String, Supplier<PlayerStorage>> PLAYER_STORAGE_MAP = new CaseInsensitiveStringHashMap<>();

    private final MessageConfig messageConfig = new MessageConfig(this);
    private final MainConfig mainConfig = new MainConfig(this);
    private final Map<String, Skill> skillRegistrar = new HashMap<>();
    private final Map<String, Skill> disabledSkills = new HashMap<>();
    private final File disabledSkillsFile = new File(getDataFolder(), "disabledSkills.yml");

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
        loadDisabledSkills();
        registerSkills();
        registerCommand(new SkillsCommand(this));
        registerCommand(new SkillsAdminCommand(this));
        registerListener(new MenuController());
        registerListener(new PlayerListener());
    }

    private void registerSkills() {
        Map<String, String> skillClassMap = new HashMap<>();
        skillClassMap.put("Gluttony", "me.hsgamer.playerskills.skill.GluttonySkill");
        skillClassMap.put("Strength", "me.hsgamer.playerskills.skill.StrengthSkill");
        skillClassMap.put("Resistance", "me.hsgamer.playerskills.skill.ResistanceSkill");
        skillClassMap.put("Dodge", "me.hsgamer.playerskills.skill.DodgeSkill");
        skillClassMap.put("Health", "me.hsgamer.playerskills.skill.HealthSkill");
        skillClassMap.put("Criticals", "me.hsgamer.playerskills.skill.CriticalsSkill");
        skillClassMap.put("Archery", "me.hsgamer.playerskills.skill.ArcherySkill");
        skillClassMap.put("Lacerate", "me.hsgamer.playerskills.skill.LacerateSkill");
        skillClassMap.put("Swift", "me.hsgamer.playerskills.skill.SwiftSkill");
        skillClassMap.put("ExtraShot", "me.hsgamer.playerskills.skill.ExtraShotSkill");
        skillClassMap.put("Looting", "me.hsgamer.playerskills.skill.LootingSkill");
        skillClassMap.put("KnockBack", "me.hsgamer.playerskills.skill.KnockBackSkill");
        skillClassMap.put("XP", "me.hsgamer.playerskills.skill.XPSkill");
        skillClassMap.put("Lumber", "me.hsgamer.playerskills.skill.LumberSkill");
        skillClassMap.put("ExtraJump", "me.hsgamer.playerskills.skill.ExtraJumpSkill");

        for (Map.Entry<String, String> entry : skillClassMap.entrySet()) {
            registerSkill(entry.getKey(), entry.getValue());
        }
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
            e.printStackTrace(); // Handle the exception as needed
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
        saveDisabledSkills();
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

    private void loadDisabledSkills() {
        if (disabledSkillsFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(disabledSkillsFile);
            for (String skillName : config.getStringList("disabledSkills")) {
                Skill skill = skillRegistrar.get(skillName);
                if (skill != null) {
                    skill.disable();
                    disabledSkills.put(skillName, skill);
                }
            }
        }
    }


    private void saveDisabledSkills() {
        List<String> disabledSkillNames = new ArrayList<>();
        for (Skill skill : disabledSkills.values()) {
            disabledSkillNames.add(skill.getConfigName());
        }
        YamlConfiguration config = new YamlConfiguration();
        config.set("disabledSkills", disabledSkillNames);
        try {
            config.save(disabledSkillsFile);
        } catch (Exception e) {
            e.printStackTrace(); // Print the exception for debugging purposes
        }
    }

    public Map<String, Skill> getSkillRegistrar() {
        return skillRegistrar;
    }

    public Map<String, Skill> getDisabledSkills() {
        return disabledSkills;
    }
}
