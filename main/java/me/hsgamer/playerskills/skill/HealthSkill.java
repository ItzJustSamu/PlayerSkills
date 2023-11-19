package me.hsgamer.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.bukkit.scheduler.Task;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.hsgamer.playerskills.PlayerSkills;
import me.hsgamer.playerskills.config.MainConfig;
import me.hsgamer.playerskills.player.SPlayer;
import me.hsgamer.playerskills.util.Utils;
import me.hsgamer.playerskills.util.modifier.XMaterialModifier;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class HealthSkill extends Skill {
    private final ConfigPath<Double> extraHealthPerLevel = Paths.doublePath("extra-health-per-level", 0.5);
    private final ConfigPath<Boolean> compatibilityMode = Paths.booleanPath("compatibility-mode", false);
    private final Map<UUID, Integer> knownMaxHealth = new IdentityHashMap<>();
    private Task task;

    public HealthSkill(PlayerSkills plugin) {
        super(plugin, "Health", "health", 20, 15);
    }

    @Override
    public void enable() {
        Runnable runnable = () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();
                SPlayer sPlayer = SPlayer.get(uuid);
                if (sPlayer == null) {
                    if (MainConfig.isVerboseLogging()) {
                        Utils.logError("Failed event. SPlayer for " + uuid + " is null.");
                    }
                    continue;
                }
                if (isWorldNotAllowed(player)) {
                    clearPlayer(player);
                    return;
                }
                int hpNeeded = (int) (getLevel(sPlayer) * (extraHealthPerLevel.getValue() * 2));
                if (hpNeeded != knownMaxHealth.getOrDefault(uuid, 0)) {
                    clearPlayer(player);
                    if (hpNeeded > 0) {
                        knownMaxHealth.put(player.getUniqueId(), hpNeeded);
                        clearModifier(player);
                        addNewHealth(player, hpNeeded);
                    }
                }
            }
        };
        long tick = compatibilityMode.getValue() ? 1L : 20L;
        task = Scheduler.CURRENT.runTaskTimer(getPlugin(), runnable, tick, tick, false);
    }

    @Override
    public void disable() {
        super.disable();
        if (task != null) {
            try {
                task.cancel();
            } catch (Exception ignored) {
                // IGNORED
            }
        }
    }

    private void addNewHealth(Player player, int amount) {
        Optional.ofNullable(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).ifPresent(instance -> {
            AttributeModifier modifier = new AttributeModifier("PlayerSkillsHealth", amount, AttributeModifier.Operation.ADD_NUMBER);
            instance.addModifier(modifier);
        });
    }

    private void clearPlayer(Player player) {
        knownMaxHealth.remove(player.getUniqueId());
        clearModifier(player);
    }

    private void clearModifier(Player player) {
        Optional.ofNullable(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).ifPresent(instance -> {
            for (AttributeModifier modifier : instance.getModifiers()) {
                if (modifier.getName().equals("PlayerSkillsHealth")) {
                    instance.removeModifier(modifier);
                }
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        clearPlayer(event.getPlayer());
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Arrays.asList(extraHealthPerLevel, compatibilityMode);
    }

    @Override
    public ItemBuilder getDefaultItem() {
        return new ItemBuilder()
                .addItemModifier(new NameModifier().setName("&cHealth Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.APPLE))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skillprice} &7point(s).",
                        "&7This skill increases the amount of health you have.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cExtra hearts: ",
                        "   &e{prev}❤ &7 >>> &e{next}❤"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int healthLevel = getLevel(player);
        int hp = (int) (healthLevel * extraHealthPerLevel.getValue());
        return Integer.toString(hp);
    }

    @Override
    public String getNextString(SPlayer player) {
        int healthLevel = getLevel(player) + 1;
        int hp = (int) (healthLevel * extraHealthPerLevel.getValue());
        return Integer.toString(hp);
    }
}
