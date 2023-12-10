package me.itzjustsamu.playerskills.skill;

import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.player.Durability;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.Utils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;


import java.util.Collections;
import java.util.List;

public class DurabilitySkill extends Skill implements Listener {

    private final ConfigPath<Double> durabilityIncrease = Paths.doublePath("durability-increase", 2.0);
    private final Durability durability;  // Create a field for the Durability object

    public DurabilitySkill(PlayerSkills plugin) {
        super(plugin, "Durability", "durability", 20, 12);
        this.durability = new Durability();  // Initialize the Durability object with an item stack

    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        SPlayer sPlayer = SPlayer.get(event.getPlayer().getUniqueId());
        int durabilityLevel = getLevel(sPlayer);
        double increase = durabilityIncrease.getValue() * durabilityLevel;

        // Iterate through the player's equipped items
        for (ItemStack itemStack : event.getPlayer().getEquipment().getArmorContents()) {
            applyDurabilityIncrease(itemStack, increase);
        }

        // Apply to main hand and off hand
        applyDurabilityIncrease(event.getPlayer().getEquipment().getItemInMainHand(), increase);
        applyDurabilityIncrease(event.getPlayer().getEquipment().getItemInOffHand(), increase);
    }

    private void applyDurabilityIncrease(ItemStack itemStack, double increase) {
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            // Adjust the item's durability based on the increase using the Durability object
            int currentDurability = this.durability.getDurability(itemStack);
            int maxDurability = this.durability.getMaxDurability(itemStack);

            // Calculate the new durability based on the increase
            int newDurability = (int) (currentDurability * (1 + increase / 100));

            // Set the new durability for the item using the Durability object
            this.durability.setDurability(itemStack, newDurability);
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
