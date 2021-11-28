package com.leonardobishop.playerskills2;

import com.leonardobishop.playerskills2.command.SkillsAdminCommand;
import com.leonardobishop.playerskills2.command.SkillsCommand;
import com.leonardobishop.playerskills2.config.MainConfig;
import com.leonardobishop.playerskills2.config.MessageConfig;
import com.leonardobishop.playerskills2.fundingsource.FundingSource;
import com.leonardobishop.playerskills2.fundingsource.VaultFundingSource;
import com.leonardobishop.playerskills2.fundingsource.XPFundingSource;
import com.leonardobishop.playerskills2.listener.PlayerListener;
import com.leonardobishop.playerskills2.menu.MenuController;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.skill.*;
import com.leonardobishop.playerskills2.util.Utils;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PlayerSkills extends BasePlugin {
    public static final Map<String, Supplier<FundingSource>> FUNDING_SOURCE_MAP = new CaseInsensitiveStringHashMap<>();

    private final MessageConfig messageConfig = new MessageConfig(this);
    private final MainConfig mainConfig = new MainConfig(this);
    private final Map<String, Skill> skillRegistrar = new HashMap<>();

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
    }

    @Override
    public void load() {
        MessageUtils.setPrefix(MessageConfig.PREFIX::getValue);
        messageConfig.setup();
        mainConfig.setup();
    }

    @Override
    public void enable() {
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
    }

    @Override
    public void postEnable() {
        Utils.logInfo("Use " + MainConfig.POINTS_FUNDING_SOURCE.getValue().getName() + " as funding source.");
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
        skillRegistrar.put(skill.getConfigName(), skill);
        skill.setup();
        skill.enable();
    }

    public boolean isSkillDisabled(Skill skill) {
        return MainConfig.OPTIONS_DISABLED_SKILLS.getValue().contains(skill.getConfigName());
    }

    public Map<String, Skill> getSkillRegistrar() {
        return skillRegistrar;
    }
}
