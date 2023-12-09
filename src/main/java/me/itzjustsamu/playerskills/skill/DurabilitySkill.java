package me.itzjustsamu.playerskills.skill;

import de.tr7zw.nbtapi.NBTItem;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;


import java.util.Collections;
import java.util.List;

public class DurabilitySkill extends Skill implements Listener {

    private final ConfigPath<Double> durabilityIncrease = Paths.doublePath("durability-increase", 2.0);

    public DurabilitySkill(PlayerSkills plugin) {
        super(plugin, "Durability", "durability", 20, 12);
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        SPlayer sPlayer = SPlayer.get(player.getUniqueId());

        if (sPlayer != null) {
            ItemStack itemStack = event.getItem();
            updateDurability(player, itemStack, getLevel(sPlayer));
        }
    }

    private void updateDurability(Player player, ItemStack itemStack, int currentLevel) {
        // Update NBT lore with new durability
        Durability(itemStack, currentLevel);

        player.getInventory().setItemInMainHand(itemStack);
    }

    private void Durability(ItemStack itemStack, int durabilityLevel) {
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            NBTItem nbtItem = new NBTItem(itemStack);
            int loreDurability = nbtItem.getInteger("Durability");
            int loreMaxDurability = itemStack.getType().getMaxDurability();

            double increaseDurability = durabilityIncrease.getValue();

            // Calculate new durability based on skill level
            int newLoreDurability = (int) Math.min(loreMaxDurability, loreDurability - increaseDurability);
            nbtItem.setInteger("Durability", newLoreDurability); // Set new durability

            // Adjust the damage to simulate increasing max durability
            int newDamage = loreMaxDurability - newLoreDurability;
            Damageable damageable = (Damageable) itemStack.getItemMeta();
            assert damageable != null;
            damageable.setDamage(newDamage);
            itemStack.setItemMeta((ItemMeta) damageable);

            itemStack = nbtItem.getItem();
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta instanceof Damageable) {
                damageable = (Damageable) itemMeta;
                int maxDurability = itemStack.getType().getMaxDurability();
                int currentDurability = maxDurability - damageable.getDamage();

                int newDurability = Math.min(maxDurability, currentDurability + (int) (durabilityIncrease.getValue() * durabilityLevel));

                damageable.setDamage(maxDurability - newDurability);
                itemStack.setItemMeta(itemMeta);
            }
        }
    }


    @Override
    public ItemBuilder getDefaultItem() {
        return new ItemBuilder()
                .addItemModifier(new NameModifier().setName("&cDurability Overview"))
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
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(durabilityIncrease);
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
