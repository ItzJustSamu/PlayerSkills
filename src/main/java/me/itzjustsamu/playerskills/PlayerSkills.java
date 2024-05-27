package me.itzjustsamu.playerskills;

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
import me.itzjustsamu.playerskills.skill.*;
import me.itzjustsamu.playerskills.storage.FlatFileStorage;
import me.itzjustsamu.playerskills.storage.PlayerStorage;
import me.itzjustsamu.playerskills.util.Updater;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class PlayerSkills extends BasePlugin {
    public static final Map<String, Supplier<FundingSource>> FUNDING_SOURCE_MAP = new CaseInsensitiveStringHashMap<>();
    public static final Map<String, Supplier<PlayerStorage>> PLAYER_STORAGE_MAP = new CaseInsensitiveStringHashMap<>();

    private final MessageConfig messageConfig = new MessageConfig(this);
    private final MainConfig mainConfig = new MainConfig(this);

    private final Map<String, Skill> skills = new ConcurrentHashMap<>();
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
        registerSkill(new ArcherySkill(this));
        registerSkill(new CriticalsSkill(this));
        registerSkill(new DodgeSkill(this));
        registerSkill(new DoubleJumpSkill(this));
        registerSkill(new RapidFireSkill(this));
        registerSkill(new FireBallSkill(this));
        registerSkill(new FishingSkill(this));
        registerSkill(new GluttonySkill(this));
        registerSkill(new GrapplingSkill(this));
        registerSkill(new HealthSkill(this));
        registerSkill(new KnockBackSkill(this));
        registerSkill(new LacerateSkill(this));
        registerSkill(new LootingSkill(this));
        registerSkill(new LumberSkill(this));
        registerSkill(new MultiBlockBreakSkill(this));
        registerSkill(new HasteSkill(this));
        registerSkill(new ResistanceSkill(this));
        registerSkill(new ShadowStepSkill(this));
        registerSkill(new StrengthSkill(this));
        registerSkill(new XPSkill(this));
        registerCommand(new SkillsCommand(this));
        registerCommand(new SkillsAdminCommand(this));
        registerListener(new MenuController());
        registerListener(new PlayerListener());

        Updater updater = new Updater(this, 113626);
        updater.checkForUpdates();
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
            Scheduler.current().runner(async).runTaskTimer(runnable, ticks, ticks);
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

    public void registerSkill(Skill skill) {
        if (skill.isSkillDisabled()) {
            logger.info("Skipping registration of disabled skill: " + skill.getSkillsConfigName());
            return;
        }

        if (skills.containsKey(skill.getSkillsConfigName())) {
            logger.warning("Attempted to register duplicate skill: " + skill.getSkillsConfigName());
            return;
        }

        skills.put(skill.getSkillsConfigName(), skill);
        skill.setup();
        skill.enable();
        logger.info("Registered skill: " + skill.getSkillsConfigName());
    }
}
