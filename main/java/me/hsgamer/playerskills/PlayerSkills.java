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
import me.hsgamer.playerskills.util.Utils;
import org.bukkit.event.HandlerList;

import java.util.*;
import java.util.function.Supplier;

public class PlayerSkills extends BasePlugin {
    public static final Map<String, Supplier<FundingSource>> FUNDING_SOURCE_MAP = new CaseInsensitiveStringHashMap<>();
    public static final Map<String, Supplier<PlayerStorage>> PLAYER_STORAGE_MAP = new CaseInsensitiveStringHashMap<>();

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
        registerSkill(new GluttonySkill(this));
        registerSkill(new StrengthSkill(this));
        registerSkill(new ResistanceSkill(this));
        registerSkill(new DodgeSkill(this));
        registerSkill(new HealthSkill(this));
        registerSkill(new CriticalsSkill(this));
        registerSkill(new ArcherySkill(this));
        registerSkill(new LacerateSkill(this));
        registerSkill(new SwiftSkill(this));
        registerSkill(new ExtraShotSkill(this));
        registerSkill(new LootingSkill(this));
        registerSkill(new KnockBackSkill(this));
        registerSkill(new XPSkill(this));
        registerSkill(new LumberSkill(this));
        registerSkill(new ExtraJumpSkill(this));
        registerCommand(new SkillsCommand(this));
        registerCommand(new SkillsAdminCommand(this));
        registerListener(new MenuController());
        registerListener(new PlayerListener());
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

    public void registerSkill(Skill skill) {
        if (MainConfig.OPTIONS_DISABLED_SKILLS.getValue().contains(skill.getConfigName())) {
            return;
        }
        skillRegistrar.put(skill.getConfigName(), skill);
        skill.setup();
        skill.enable();
    }

    public Map<String, Skill> getSkillRegistrar() {
        return skillRegistrar;
    }
}