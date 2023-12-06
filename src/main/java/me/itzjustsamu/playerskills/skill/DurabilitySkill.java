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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class DurabilitySkill extends Skill {
    private final ConfigPath<Double> durabilityIncrease = Paths.doublePath("durability-increase", 2.0D);

    public DurabilitySkill(PlayerSkills plugin) {
        super(plugin, "Durability", "durability", 20, 12);
    }

    @EventHandler
    public void onHit(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
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

        int durabilityLevel = getLevel(sPlayer);

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL || event.getCause().name().contains("CUSTOM")) {
            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                increaseDurability(itemStack, durabilityLevel);
            }
            increaseDurability(player.getInventory().getItemInMainHand(), durabilityLevel);
            increaseDurability(player.getInventory().getItemInOffHand(), durabilityLevel);
        }
    }

    private void increaseDurability(ItemStack itemStack, int durabilityLevel) {
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta instanceof Damageable) {
                Damageable damageable = (Damageable) itemMeta;
                int maxDurability = itemStack.getType().getMaxDurability();
                int currentDurability = maxDurability - damageable.getDamage();

                int newDurability = Math.min(maxDurability, currentDurability + (int) (durabilityIncrease.getValue() * durabilityLevel));

                damageable.setDamage(maxDurability - newDurability);
                itemStack.setItemMeta(itemMeta);
            }
        }
    }





    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(durabilityIncrease);
    }

    @Override
    public ItemBuilder getDefaultItem() {
        return new ItemBuilder()
                .addItemModifier(new NameModifier().setName("&cDurability Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.STRING))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skillprice} &7point(s).",
                        "&7This skill increases durability of equipped items.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cDurability Increase: ",
                        "   &e{prev}% &7 >>> &e{next}%"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int durabilityLevel = getLevel(player);
        double increase = durabilityIncrease.getValue() * durabilityLevel;
        return Utils.getPercentageFormat().format(increase);
    }

    @Override
    public String getNextString(SPlayer player) {
        int durabilityLevel = getLevel(player) + 1;
        double increase = durabilityIncrease.getValue() * durabilityLevel;
        return Utils.getPercentageFormat().format(increase);
    }
}
