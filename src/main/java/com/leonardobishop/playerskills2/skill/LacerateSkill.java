package com.leonardobishop.playerskills2.skill;

import com.cryptomorin.xseries.XMaterial;
import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.config.Config;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.utils.modifier.XMaterialModifier;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.ConfigPath;
import me.hsgamer.hscore.config.path.BooleanConfigPath;
import me.hsgamer.hscore.config.path.DoubleConfigPath;
import me.hsgamer.hscore.config.path.IntegerConfigPath;
import me.hsgamer.hscore.config.path.LongConfigPath;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.leonardobishop.playerskills2.utils.Utils.getPercentageFormat;

public class LacerateSkill extends Skill {
    private final DoubleConfigPath percentIncrease = new DoubleConfigPath("percent-increase", 4D);
    private final IntegerConfigPath bleedCycles = new IntegerConfigPath("bleed-cycles", 8);
    private final LongConfigPath bleedInterval = new LongConfigPath("bleed-interval", 50L);
    private final IntegerConfigPath bleedDamage = new IntegerConfigPath("bleed-damage", 2);
    private final BooleanConfigPath applyToNonPlayers = new BooleanConfigPath("apply-to-non-players", false);

    private final HashMap<LivingEntity, BukkitTask> cutEntities = new HashMap<>();

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
            if (super.getPlugin().isVerboseLogging()) {
                super.getPlugin().logError("Failed event. SPlayer for " + player.getUniqueId() + " is null.");
            }
            return;
        }

        int lacerateLevel = getLevel(sPlayer);

        double chance = lacerateLevel * percentIncrease.getValue();

        if (ThreadLocalRandom.current().nextInt(100) < chance) {
            LivingEntity victim = (LivingEntity) event.getEntity();

            bleed(victim);

            if (!Config.get(super.getPlugin(), "messages.bleeding-enemy").getColoredString().equals("")) {
                player.sendMessage(Config.get(super.getPlugin(), "messages.bleeding-enemy").getColoredString());
            }
            if (!Config.get(super.getPlugin(), "messages.bleeding-self").getColoredString().equals("")) {
                victim.sendMessage(Config.get(super.getPlugin(), "messages.bleeding-self").getColoredString());
            }
        }
    }

    private void bleed(LivingEntity player) {
        if (cutEntities.containsKey(player)) {
            return;
        }
        BukkitTask bt = new BukkitRunnable() {
            int times = 0;

            @Override
            public void run() {
                player.damage(bleedDamage.getValue(), null);
                times++;
                if (times >= bleedCycles.getValue()) {
                    cutEntities.remove(player);
                    try {
                        this.cancel();
                    } catch (Exception ignored) {
                        // cancelled check throws error in 1.8
                    }
                }
            }
        }.runTaskTimer(getPlugin(), bleedInterval.getValue(), bleedInterval.getValue());
        cutEntities.put(player, bt);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (cutEntities.containsKey(event.getEntity())) {
            BukkitTask bt = cutEntities.get(event.getEntity());
            try {
                bt.cancel();
            } catch (Exception ignored) {
                // cancelled check throws error in 1.8
            }
            cutEntities.remove(event.getEntity());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (cutEntities.containsKey(event.getPlayer())) {
            BukkitTask bt = cutEntities.get(event.getPlayer());
            try {
                bt.cancel();
            } catch (Exception ignored) {
                // cancelled check throws error in 1.8
            }
            cutEntities.remove(event.getPlayer());
        }
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Arrays.asList(percentIncrease, bleedCycles, bleedInterval, bleedDamage, applyToNonPlayers);
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
