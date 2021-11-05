package com.leonardobishop.playerskills2;

import com.leonardobishop.playerskills2.command.SkillsAdminCommand;
import com.leonardobishop.playerskills2.command.SkillsCommand;
import com.leonardobishop.playerskills2.config.Config;
import com.leonardobishop.playerskills2.fundingsource.FundingSource;
import com.leonardobishop.playerskills2.fundingsource.VaultFundingSource;
import com.leonardobishop.playerskills2.fundingsource.XPFundingSource;
import com.leonardobishop.playerskills2.listener.PlayerListener;
import com.leonardobishop.playerskills2.menu.MenuController;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.skill.*;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Optional;

public class PlayerSkills extends BasePlugin {

    private final HashMap<String, Skill> skillRegistrar = new HashMap<>();
    private final DecimalFormat percentageFormat = new DecimalFormat("#.#");
    private FundingSource fundingSource;
    private boolean verboseLogging;

    public FundingSource getFundingSource() {
        return fundingSource;
    }

    @Override
    public void enable() {
        percentageFormat.setRoundingMode(RoundingMode.CEILING);

        createConfig();

        registerSkill(new GluttonySkill(this));
        registerSkill(new StrengthSkill(this));
        registerSkill(new ResistanceSkill(this));
        registerSkill(new DodgeSkill(this));
        registerSkill(new HealthSkill(this));
        registerSkill(new CriticalsSkill(this));
        registerSkill(new ArcherySkill(this));
        registerSkill(new LacerateSkill(this));

        registerCommand(new SkillsCommand(this));
        registerCommand(new SkillsAdminCommand(this));
        registerListener(new MenuController());
        registerListener(new PlayerListener(this));

        verboseLogging = Config.get(this, "options.logging.verbose", false).getBoolean();
        if (verboseLogging) {
            logInfo("Verbose logging is enabled. If there is too much spam in the console from PlayerSkills2, you can disable this in the config.");
        }

        this.fundingSource = Optional.of(Config.get(this, "points.funding-source"))
                .filter(value -> !value.isNull())
                .map(Config.ConfigObject::getString)
                .map(value -> {
                    if (value.equalsIgnoreCase("VAULT")) {
                        logInfo("Initialised with Vault as the skill point funding source.");
                        return new VaultFundingSource(this);
                    } else {
                        logInfo("Initialised with the players XP as the skill point funding source.");
                        return new XPFundingSource(this);
                    }
                })
                .orElseGet(() -> new XPFundingSource(this));
    }

    @Override
    public void disable() {
        for (SPlayer player : SPlayer.getPlayers().values()) {
            SPlayer.save(this, player);
        }
        skillRegistrar.values().forEach(skill -> {
            skill.disable();
            HandlerList.unregisterAll(skill);
        });
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
        ConfigurationSection section = getConfig().getConfigurationSection("skills." + skill.getConfigName() + ".config");
        if (section != null) {
            section.getValues(false).forEach(skill.getConfig()::put);
        }
        ConfigurationSection priceOverride = getConfig().getConfigurationSection("skills." + skill.getConfigName() + ".price-override");
        if (priceOverride != null) {
            priceOverride.getValues(false).forEach((key, value) -> {
                int level;
                try {
                    level = Integer.parseInt(key);
                } catch (Exception e) {
                    return;
                }
                if (!(value instanceof Number)) {
                    return;
                }
                int price = ((Number) value).intValue();
                skill.getPointPriceOverrides().put(level, price);
            });
        }
        skill.setItemLocation("skills." + skill.getConfigName() + ".display");
        skill.enable();
        Bukkit.getPluginManager().registerEvents(skill, this);
        return true;
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
    }

}
