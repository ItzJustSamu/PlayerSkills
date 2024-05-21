package me.itzjustsamu.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.BukkitItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.minecraft.item.ItemBuilder;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.Utils;
import me.itzjustsamu.playerskills.util.VersionControl;
import me.itzjustsamu.playerskills.util.modifier.XMaterialModifier;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

public class SpeedSkill extends Skill {
    private final Map<UUID, Double> knownSpeeds = new IdentityHashMap<>();

    public SpeedSkill(PlayerSkills plugin) {
        super(plugin, "Speed", "speed", 20, 18);
    }

    @Override
    public void enable() {
        for (Player player : getPlugin().getServer().getOnlinePlayers()) {
            applySpeedModifier(player);
        }
    }

    @Override
    public void disable() {
        super.disable();
        for (Player player : getPlugin().getServer().getOnlinePlayers()) {
            clearSpeedModifier(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        applySpeedModifier(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        clearSpeedModifier(event.getPlayer());
    }

    private void applySpeedModifier(Player player) {
        if (Worlds_Restriction(player)) {
            return;
        }

        SPlayer sPlayer = SPlayer.get(player.getUniqueId());
        if (sPlayer == null) {
            if (MainConfig.isVerboseLogging()) {
                Utils.logError("Failed to apply speed modifier. SPlayer for " + player.getUniqueId() + " is null.");
            }
            return;
        }

        double speedIncrease = Math.min(0.8, getLevel(sPlayer) * getUpgrade().getValue() * .001); // Limit the increase to 0.8
        if (knownSpeeds.getOrDefault(player.getUniqueId(), 0.0) != speedIncrease) {
            knownSpeeds.put(player.getUniqueId(), speedIncrease);
            clearSpeedModifier(player);
            try {
                updateSpeed(player, speedIncrease);
            } catch (Exception ex) {
                Utils.logError("Error applying speed modifier to " + player.getName(), ex);
            }
        }
    }

    private void clearSpeedModifier(Player player) {
        knownSpeeds.remove(player.getUniqueId());
        try {
            updateSpeed(player, 0.0);
        } catch (Exception ex) {
            Utils.logError("Error clearing speed modifier from " + player.getName(), ex);
        }
    }

    private void updateSpeed(Player player, double speedIncrease) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (VersionControl.isNewVersion()) {
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            if (attribute != null) {
                double newSpeed = Math.max(0.0, Math.min(1.0, 0.2 + speedIncrease)); // Ensure the speed is between 0.0 and 1.0
                attribute.setBaseValue(newSpeed);
                Utils.logInfo("Updated speed for " + player.getUniqueId() + " to " + newSpeed);
            } else {
                Utils.logError("Attribute GENERIC_MOVEMENT_SPEED is null for player " + player.getUniqueId());
            }
        } else {
            float newSpeed = (float) Math.max(-1.0, Math.min(1.0, 0.2 + speedIncrease)); // Ensure the speed is between -1.0 and 1.0
            player.getClass().getMethod("setWalkSpeed", float.class).invoke(player, newSpeed);
            Utils.logInfo("Updated walk speed for " + player.getUniqueId() + " to " + newSpeed);
        }
    }

    @Override
    public ItemBuilder<ItemStack> getDefaultItem() {
        return new BukkitItemBuilder()
                .addItemModifier(new NameModifier().setName("&bSpeed Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.IRON_BOOTS))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{price} &7point(s).",
                        "&7This skill increases your movement speed.",
                        "&7Level: &e{level}&7/&e{limit}&7",
                        " ",
                        "&bSpeed Increase: ",
                        "   &e{prev}% &7 >>> &e{next}%"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        double speed = getLevel(player) * getUpgrade().getValue();
        return Utils.getPercentageFormat().format(speed);
    }

    @Override
    public String getNextString(SPlayer player) {
        double speed = (getLevel(player) + 1) * getUpgrade().getValue();
        return Utils.getPercentageFormat().format(speed);
    }
}
