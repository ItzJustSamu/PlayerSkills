package me.itzjustsamu.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.Utils;
import me.itzjustsamu.playerskills.util.modifier.XMaterialModifier;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ExtraJumpSkill extends Skill {
    private final ConfigPath<Long> COOLDOWN = Paths.longPath("cooldown", 30000L); // Default: 30 seconds
    private final ConfigPath<String> COOLDOWN_MESSAGE = Paths.stringPath("cooldown-message", "&cDoubleJump cooldown: &e{remaining_time} seconds.");

    private final HashMap<Player, Boolean> coolDown = new HashMap<>();
    private final HashMap<Player, Long> cooldownMap = new HashMap<>();
    private final HashMap<Player, Boolean> HasDoubleJumped = new HashMap<>();

    public ExtraJumpSkill(PlayerSkills plugin) {
        super(plugin, "DoubleJump", "doublejump", 5, 5,0);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && HasDoubleJumped.getOrDefault(player, false)) {
            event.setCancelled(true);
            HasDoubleJumped.put(player, false);
        }
    }

    @EventHandler
    public void onMove(final PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        player.setAllowFlight(!cooldownMap.containsKey(player) || System.currentTimeMillis() >= cooldownMap.get(player));
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (Worlds_Restriction(player)) {
            return;
        }

        if (cooldownMap.containsKey(player) && System.currentTimeMillis() < cooldownMap.get(player)) {
            if (!HasDoubleJumped.getOrDefault(player, false)) {
                event.setCancelled(true);
                player.setAllowFlight(false);

            }
        } else {
            SPlayer sPlayer = SPlayer.get(player.getUniqueId());

            if (sPlayer == null) {
                if (MainConfig.isVerboseLogging()) {
                    Utils.logError("Failed event. SPlayer for " + player.getUniqueId() + " is null.");
                }
                return;
            }

            int jumpLevel = getLevel(sPlayer);

            if (jumpLevel > 0) {
                double jumpHeight = jumpLevel * getIncrement().getValue();
                cooldownMap.put(player, System.currentTimeMillis() + COOLDOWN.getValue());
                Vector direction = player.getLocation().getDirection();
                player.setVelocity(direction.multiply(jumpHeight));
                player.setAllowFlight(false);

                long remainingTime = (cooldownMap.get(player) - System.currentTimeMillis()) / 1000L;
                sendActionBar(player, remainingTime);

                // Set HasDoubleJumped to true only if the player is in the air
                if (player.getLocation().getY() % 1 != 0) {
                    HasDoubleJumped.put(player, true);
                }
            } else {
                // Reset HasDoubleJumped to false if the player is on the ground
                HasDoubleJumped.put(player, false);
            }
        }
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
        return Arrays.asList(getIncrement(), COOLDOWN, COOLDOWN_MESSAGE);
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
        double jumpHeight = jumpLevel * getIncrement().getValue();
        return String.valueOf(jumpHeight);
    }

    @Override
    public String getNextString(SPlayer player) {
        int jumpLevel = getLevel(player) + 1;
        double jumpHeight = jumpLevel * getIncrement().getValue();
        return String.valueOf(jumpHeight);
    }

    private void sendActionBar(Player player, long remainingTime) {
        new BukkitRunnable() {
            long timeLeft = remainingTime;

            @Override
            public void run() {
                if (timeLeft > 0) {
                    String actionBarMessage = ChatColor.translateAlternateColorCodes('&', COOLDOWN_MESSAGE.getValue())
                            .replace("{remaining_time}", String.valueOf(timeLeft));
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionBarMessage));
                    timeLeft--;
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
                    cancel(); // Stop the task when the cooldown ends
                }
            }
        }.runTaskTimer(getPlugin(), 0L, 20L); // Update every second
    }
}