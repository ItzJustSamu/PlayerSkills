package me.hsgamer.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.bukkit.scheduler.Task;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.hsgamer.playerskills.PlayerSkills;
import me.hsgamer.playerskills.config.MainConfig;
import me.hsgamer.playerskills.player.SPlayer;
import me.hsgamer.playerskills.util.Utils;
import me.hsgamer.playerskills.util.modifier.XMaterialModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BooleanSupplier;

import static me.hsgamer.playerskills.util.Utils.getPercentageFormat;

public class LacerateSkill extends Skill {
    private final ConfigPath<Double> percentIncrease = Paths.doublePath("percent-increase", 4D);
    private final ConfigPath<Integer> bleedCycles = Paths.integerPath("bleed-cycles", 8);
    private final ConfigPath<Long> bleedInterval = Paths.longPath("bleed-interval", 50L);
    private final ConfigPath<Integer> bleedDamage = Paths.integerPath("bleed-damage", 2);
    private final ConfigPath<Boolean> applyToNonPlayers = Paths.booleanPath("apply-to-non-players", false);
    private final ConfigPath<String> bleedingEnemy = Paths.stringPath("bleeding-enemy-message", "&a*** ENEMY BLEEDING ***");
    private final ConfigPath<String> bleedingSelf = Paths.stringPath("bleeding-self-message", "&c*** YOU ARE BLEEDING ***");

    private final Map<LivingEntity, Task> cutEntities = new ConcurrentHashMap<>();

    public LacerateSkill(PlayerSkills plugin) {
        super(plugin, "Lacerate", "lacerate", 4, 23);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || (!(event.getEntity() instanceof Player) && (!applyToNonPlayers.getValue()))) {
            return;
        }

        Player player = (Player) event.getDamager();
        if (isWorldNotAllowed(player)) {
            return;
        }

        SPlayer sPlayer = SPlayer.get(player.getUniqueId());

        if (sPlayer == null) {
            if (MainConfig.isVerboseLogging()) {
                Utils.logError("Failed event. SPlayer for " + player.getUniqueId() + " is null.");
            }
            return;
        }

        int lacerateLevel = getLevel(sPlayer);

        double chance = lacerateLevel * percentIncrease.getValue();

        if (ThreadLocalRandom.current().nextInt(100) < chance) {
            LivingEntity victim = (LivingEntity) event.getEntity();

            bleed(victim);

            String message = bleedingEnemy.getValue();
            if (!message.equals("")) {
                MessageUtils.sendMessage(player, message, "");
            }
            String victimMesssage = bleedingSelf.getValue();
            if (!victimMesssage.equals("")) {
                MessageUtils.sendMessage(victim, victimMesssage, "");
            }
        }
    }

    private void bleed(LivingEntity player) {
        if (cutEntities.containsKey(player)) {
            return;
        }
        BooleanSupplier runnable = new BooleanSupplier() {
            int times = 0;

            @Override
            public boolean getAsBoolean() {
                player.damage(bleedDamage.getValue(), null);
                times++;
                if (times >= bleedCycles.getValue()) {
                    cutEntities.remove(player);
                    return false;
                }
                return true;
            }
        };
        Task task = Scheduler.CURRENT.runEntityTaskTimer(getPlugin(), player, runnable, bleedInterval.getValue(), bleedInterval.getValue(), false);
        cutEntities.put(player, task);
    }

    private void cancelTask(LivingEntity livingEntity) {
        Optional.ofNullable(cutEntities.get(livingEntity)).ifPresent(task -> {
            try {
                task.cancel();
            } catch (Exception ignored) {
                // IGNORED
            }
        });
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        cancelTask(event.getEntity());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cancelTask(event.getPlayer());
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Arrays.asList(percentIncrease, bleedCycles, bleedInterval, bleedDamage, applyToNonPlayers);
    }

    @Override
    public List<ConfigPath<?>> getMessageConfigPaths() {
        return Arrays.asList(bleedingEnemy, bleedingSelf);
    }

    @Override
    public ItemBuilder getDefaultItem() {
        return new ItemBuilder()
                .addItemModifier(new NameModifier().setName("&cLacerate Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.REDSTONE))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skillprice} &7point(s).",
                        "&7This skill increases the chance of making an enemy bleed.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cCut chance: ",
                        "   &e{prev}% &7 >>> &e{next}%"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int lacerateLevel = getLevel(player);
        double damage = lacerateLevel * percentIncrease.getValue();
        return getPercentageFormat().format(damage);
    }

    @Override
    public String getNextString(SPlayer player) {
        int lacerateLevel = getLevel(player) + 1;
        double damage = lacerateLevel * percentIncrease.getValue();
        return getPercentageFormat().format(damage);
    }
}
