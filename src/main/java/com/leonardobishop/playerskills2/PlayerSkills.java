package com.leonardobishop.playerskills2;

import com.leonardobishop.playerskills2.commands.SkillsCommand;
import com.leonardobishop.playerskills2.commands.SkillsadminCommand;
import com.leonardobishop.playerskills2.events.ChatEvent;
import com.leonardobishop.playerskills2.events.JoinEvent;
import com.leonardobishop.playerskills2.events.LeaveEvent;
import com.leonardobishop.playerskills2.fundingsource.FundingSource;
import com.leonardobishop.playerskills2.fundingsource.VaultFundingSource;
import com.leonardobishop.playerskills2.fundingsource.XPFundingSource;
import com.leonardobishop.playerskills2.menu.MenuController;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.skills.*;
import com.leonardobishop.playerskills2.utils.Config;
import com.leonardobishop.playerskills2.utils.ConfigEditWrapper;
import com.leonardobishop.playerskills2.utils.CreatorConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;

public class PlayerSkills extends JavaPlugin {

    private final HashMap<String, Skill> skillRegistrar = new HashMap<>();
    private final DecimalFormat percentageFormat = new DecimalFormat("#.#");
    private ChatEvent chatEvent;
    private FundingSource fundingSource;
    private boolean verboseLogging;

    public FundingSource getFundingSource() {
        return fundingSource;
    }

    @Override
    public void onEnable() {
        super.getLogger().info("Thank you for purchasing PlayerSkills2.");
        super.getLogger().info("If this is a leaked version, then shame on you :(");

        percentageFormat.setRoundingMode(RoundingMode.CEILING);

        createConfig();

        GluttonySkill gluttonySkill = new GluttonySkill(this);
        StrengthSkill strengthSkill = new StrengthSkill(this);
        ResistanceSkill resistanceSkill = new ResistanceSkill(this);
        DodgeSkill dodgeSkill = new DodgeSkill(this);
        HealthSkill healthSkill = new HealthSkill(this);
        CriticalsSkill criticalsSkill = new CriticalsSkill(this);
        ArcherySkill archerySkill = new ArcherySkill(this);
        LacerateSkill lacerateSkill = new LacerateSkill(this);

        registerSkill(gluttonySkill);
        registerSkill(strengthSkill);
        registerSkill(resistanceSkill);
        registerSkill(dodgeSkill);
        registerSkill(healthSkill);
        registerSkill(criticalsSkill);
        registerSkill(archerySkill);
        registerSkill(lacerateSkill);

        this.chatEvent = new ChatEvent(this);
        getCommand("skills").setExecutor(new SkillsCommand(this));
        getCommand("skillsadmin").setExecutor(new SkillsadminCommand(this));
        Bukkit.getPluginManager().registerEvents(new MenuController(), this);
        Bukkit.getPluginManager().registerEvents(new JoinEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new LeaveEvent(this), this);
        Bukkit.getPluginManager().registerEvents(chatEvent, this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!SPlayer.getPlayers().containsKey(player.getUniqueId())) {
                SPlayer.load(this, player.getUniqueId());
            }
        }

        verboseLogging = Config.get(this, "options.logging.verbose", false).getBoolean();
        if (verboseLogging) {
            logInfo("Verbose logging is enabled. If there is too much spam in the console from PlayerSkills2, you can disable this in the config.");
        }

        Config.ConfigObject fundingSource = Config.get(this, "points.funding-source");
        if (!fundingSource.isNull()) {
            if (fundingSource.getString().equalsIgnoreCase("VAULT")) {
                logInfo("Initialised with Vault as the skill point funding source.");
                this.fundingSource = new VaultFundingSource(this);
            } else {
                logInfo("Initialised with the players XP as the skill point funding source.");
                this.fundingSource = new XPFundingSource();
            }
        } else {
            this.fundingSource = new XPFundingSource();
        }
    }

    @Override
    public void onDisable() {
        for (SPlayer player : SPlayer.getPlayers().values()) {
            SPlayer.save(this, player);
        }
        skillRegistrar.clear();
    }

    public DecimalFormat getPercentageFormat() {
        return percentageFormat;
    }

    public boolean registerSkill(Skill skill) {
        if (getConfig().contains("disabled-skills") && getConfig().getStringList("disabled-skills").contains(skill.getConfigName())) {
            return false;
        }
        skillRegistrar.put(skill.getConfigName(), skill);
        for (String key : super.getConfig().getConfigurationSection("skills." + skill.getConfigName() + ".config").getKeys(false)) {
            Object value = super.getConfig().get("skills." + skill.getConfigName() + ".config." + key);
            skill.getConfig().put(key, value);
            for (CreatorConfigValue conf : skill.getCreatorConfigValues()) {
                if (conf.getKey().equals(key)) {
                    conf.setValue(value);
                }
            }
        }
        if (super.getConfig().contains("skills." + skill.getConfigName() + ".price-override")) {
            for (String key : super.getConfig().getConfigurationSection("skills." + skill.getConfigName() + ".price-override").getKeys(false)) {
                int price = super.getConfig().getInt("skills." + skill.getConfigName() + ".price-override." + key);
                skill.getPointPriceOverrides().put(Integer.valueOf(key), price);
            }
        }
        skill.setItemLocation("skills." + skill.getConfigName() + ".display");
        skill.enable(this);
        Bukkit.getPluginManager().registerEvents(skill, this);
        return true;
    }

    public void writeSkillConfigCreatorValuesToFile() {
        for (Skill skill : skillRegistrar.values()) {
            for (CreatorConfigValue conf : skill.getCreatorConfigValues()) {
                super.getConfig().set("skills." + skill.getConfigName() + ".config." + conf.getKey(), conf.getValue());
            }
        }
        super.saveConfig();
    }

    public void reloadSkillConfigs() {
        for (Skill skill : skillRegistrar.values()) {
            skill.getConfig().clear();
            for (String key : super.getConfig().getConfigurationSection("skills." + skill.getConfigName() + ".config").getKeys(false)) {
                Object value = super.getConfig().get("skills." + skill.getConfigName() + ".config." + key);
                skill.getConfig().put(key, value);
                for (CreatorConfigValue conf : skill.getCreatorConfigValues()) {
                    if (conf.getKey().equals(key)) {
                        conf.setValue(value);
                    }
                }
            }
            if (super.getConfig().contains("skills." + skill.getConfigName() + ".price-override")) {
                for (String key : super.getConfig().getConfigurationSection("skills." + skill.getConfigName() + ".price-override").getKeys(false)) {
                    int price = super.getConfig().getInt("skills." + skill.getConfigName() + ".price-override." + key);
                    skill.getPointPriceOverrides().put(Integer.valueOf(key), price);
                }
            }
        }
        super.saveConfig();
    }

    public boolean isVerboseLogging() {
        return verboseLogging;
    }

    public HashMap<String, Skill> getSkillRegistrar() {
        return skillRegistrar;
    }

    public void logInfo(String message) {
        super.getLogger().info(message);
    }

    public void logError(String message) {
        super.getLogger().severe(message);
    }

    public void lockPlayerEditor(Player player, ConfigEditWrapper wrapper) {
        chatEvent.getCreatorConfigValue().put(player, wrapper);
    }

    private void createConfig() {
        File directory = this.getDataFolder();
        if (!directory.exists() && !directory.isDirectory()) {
            directory.mkdir();
        }

        File config = new File(directory + File.separator + "config.yml");
        if (!config.exists()) {
            saveResource("config.yml", false);
        } else {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(config);
            InputStream packed = this.getResource("config.yml");
            assert packed != null;
            YamlConfiguration packedFile = YamlConfiguration.loadConfiguration(new InputStreamReader(packed));

            boolean changed = false;
            for (String s : packedFile.getConfigurationSection("skills").getKeys(false)) {
                if (!yaml.contains("skills." + s)) {
                    logInfo("Writing new skill to config: " + s);
                    changed = true;
                    yaml.set("skills." + s, packedFile.getConfigurationSection("skills." + s));
                }
            }

            if (changed) {
                try {
                    yaml.save(config);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        saveResource("readme.txt", false);
    }

}
