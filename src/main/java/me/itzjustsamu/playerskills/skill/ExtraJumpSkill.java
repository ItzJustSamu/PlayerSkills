package me.itzjustsamu.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.util.Utils;
import me.itzjustsamu.playerskills.util.modifier.XMaterialModifier;
import me.itzjustsamu.playerskills.player.SPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class  ExtraJumpSkill extends Skill {
    private final ConfigPath<Double> VELOCITY = Paths.doublePath("skills.extrajump.config.Velocity-increment", 0.5);

    private final HashMap<Player, Boolean> coolDown = new HashMap<>();

    public ExtraJumpSkill(PlayerSkills plugin) {
        super(plugin, "DoubleJump", "doublejump", 5, 12);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(final PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        player.setAllowFlight(coolDown.get(player) != null && coolDown.get(player));

        if (player.getLocation().getY() % 1 == 0) {
            coolDown.put(player, true);
        }
    }


        @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
            Player player = event.getPlayer();
            if (player.getGameMode() == GameMode.CREATIVE) {
                return;
            }

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

            int jumpLevel = getLevel(sPlayer);

            double jumpHeight = 0;
            if (jumpLevel > 0) {
                jumpHeight = jumpLevel * VELOCITY.getValue();
            }

            if (jumpLevel > 0) {
                SkillEffect.playParticles(player, player.getLocation());
                SkillEffect.playSound(player);
            }
            // Perform double jump
            if (coolDown.get(player)) {
                event.setCancelled(true);
                coolDown.put(player, false);

                // Get the player's direction vector
                Vector direction = player.getLocation().getDirection();

                // Set the player's velocity using the direction vector and jump height
                player.setVelocity(direction.multiply(jumpHeight));
            }
            player.setAllowFlight(false);
    }

            @EventHandler
    public void onSneak(final PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (player.getLocation().getY() % 1 == 0 && coolDown.get(player) != null && !coolDown.get(player)) {
            coolDown.put(player, true);
            player.setVelocity(new Vector());
        }
    }

    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(VELOCITY);
    }

    @Override
    public ItemBuilder getDefaultItem() {
        return new ItemBuilder()
                .addItemModifier(new NameModifier().setName("&cDouble Jump Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.LEATHER_BOOTS))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skillprice} &7point(s).",
                        "&7This skill allows you to perform a double jump with cooldown based on skill level.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cVelocity increase: ",
                        "   &e{prev} &7 >>> &e{next}"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int jumpLevel = getLevel(player);
        double jumpHeight = jumpLevel * VELOCITY.getValue();
        return String.valueOf(jumpHeight);
    }

    @Override
    public String getNextString(SPlayer player) {
        int jumpLevel = getLevel(player) + 1;
        double jumpHeight = jumpLevel * VELOCITY.getValue();
        return String.valueOf(jumpHeight);
    }

}