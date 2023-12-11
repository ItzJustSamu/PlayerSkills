package me.itzjustsamu.playerskills.skill;

import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.player.Durability;
import me.itzjustsamu.playerskills.player.IDurabilityItem;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DurabilitySkill extends Skill implements Listener {

    private final ConfigPath<Double> durabilityIncrease = Paths.doublePath("durability-increase", 2.0);


    public DurabilitySkill(PlayerSkills plugin) {
        super(plugin, "Durability", "durability", 20, -1);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event, ItemStack item, int durability) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            SPlayer sPlayer = SPlayer.get(player.getUniqueId());
            int durabilityLevel = getLevel(sPlayer);
            double increase = durabilityIncrease.getValue() * durabilityLevel;

            // Loop through player's inventory
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    // Check if the item has durability
                    IDurabilityItem durabilityItem = new Durability(itemStack);
                    if (durabilityItem.hasDurability()) {
                        // Increment durability based on the increase value
                        int maxDurability = durabilityItem.getMaxDurability();
                        int currentDurability = durabilityItem.getDurability();
                        int newMaxDurability = (int) (maxDurability * (1 + increase / 100.0));
                        int newDurability = (int) (currentDurability * (1 + increase / 100.0));

                        // Update item durability
                        new Durability(item).createDurability(newMaxDurability, newDurability);

                    }
                }
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
