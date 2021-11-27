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
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Optional;

public class PlayerSkills extends BasePlugin {

    private final HashMap<String, Skill> skillRegistrar = new HashMap<>();
    private FundingSource fundingSource;
    private boolean verboseLogging;

    public FundingSource getFundingSource() {
        return fundingSource;
    }

    @Override
    public void enable() {
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

    public void registerSkill(Skill skill) {
        if (isSkillDisabled(skill)) {
            return;
        }
        skill.setup();
        skillRegistrar.put(skill.getConfigName(), skill);
        skill.enable();
        Bukkit.getPluginManager().registerEvents(skill, this);
    }

    public boolean isSkillDisabled(Skill skill) {
        return getConfig().contains("disabled-skills") && getConfig().getStringList("disabled-skills").contains(skill.getConfigName());
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
        saveResource("config.yml", false);
    }
}
