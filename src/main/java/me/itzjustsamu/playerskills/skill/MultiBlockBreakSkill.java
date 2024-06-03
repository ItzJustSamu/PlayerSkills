package me.itzjustsamu.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.BukkitItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.minecraft.item.ItemBuilder;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.Utils;
import me.itzjustsamu.playerskills.util.VersionControl;
import me.itzjustsamu.playerskills.util.modifier.XMaterialModifier;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class MultiBlockBreakSkill extends Skill implements Listener {

    public MultiBlockBreakSkill(PlayerSkills plugin) {
        super(plugin, "Multibreak", "multiblockbreak", 10, 14);
    }

    private ItemStack getItemInHand(Player player) {
        if (VersionControl.isOldVersion()) {
            return player.getInventory().getItemInHand();
        } else {
            return player.getInventory().getItemInMainHand();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();
        SPlayer sPlayer = SPlayer.get(uniqueId);

        if (sPlayer == null) {
            if (MainConfig.isVerboseLogging()) {
                Utils.logError("Failed event. SPlayer for " + uniqueId + " is null.");
            }
            return;
        }

        // Check if player has the skill and if the tool is a hammer (replace with your hammer check)
        if (getLevel(sPlayer) > 0 && isHammer(getItemInHand(player))) {
            int baseRadius = getLevel(sPlayer) * getUpgrade().getValue();
            Block block = event.getBlock();
            Location eyeLocation = player.getEyeLocation();
            Vector direction = eyeLocation.getDirection().normalize();

            // Get break locations based on direction
            List<Location> breakLocations = getBreakLocations(block, direction, baseRadius);

            // Break blocks within the radius (except air and bedrock)
            for (Location location : breakLocations) {
                Block breakBlock = location.getBlock();
                if (breakBlock.getType() != Material.AIR && breakBlock.getType() != Material.BEDROCK) {
                    breakBlock.breakNaturally(); // Break the block
                }
            }
        }
    }

    // Method to check if the item is a hammer (replace with your logic)
    private boolean isHammer(ItemStack item) {
        if (item == null) return false;
        Material itemType = item.getType();

        if (VersionControl.isOldVersion()) {
            return itemType == Material.valueOf("WOOD_PICKAXE") ||
                    itemType == Material.STONE_PICKAXE ||
                    itemType == Material.IRON_PICKAXE ||
                    itemType == Material.valueOf("GOLD_PICKAXE") ||
                    itemType == Material.DIAMOND_PICKAXE;
        } else {
            return itemType == Material.WOODEN_PICKAXE ||
                    itemType == Material.STONE_PICKAXE ||
                    itemType == Material.IRON_PICKAXE ||
                    itemType == Material.GOLDEN_PICKAXE ||
                    itemType == Material.DIAMOND_PICKAXE;
        }
    }

    private List<Location> getBreakLocations(Block block, Vector direction, int radius) {
        List<Location> locations = new ArrayList<>();
        // Precompute relative positions for efficiency
        List<Vector> relativePositions = new ArrayList<>();
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                for (int k = -radius; k <= radius; k++) {
                    if (i * i + j * j + k * k <= radius * radius) {
                        relativePositions.add(new Vector(i + 0.5, j + 0.5, k + 0.5));
                    }
                }
            }
        }

        for (Vector relativePos : relativePositions) {
            Location loc = block.getLocation().add(relativePos).toVector().add(direction.multiply(0.1)).toLocation(block.getWorld());
            locations.add(loc);
        }

        return locations;
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(getUpgrade());
    }

    @Override
    public ItemBuilder<ItemStack> getDefaultItem() {
        return new BukkitItemBuilder()
                .addItemModifier(new NameModifier().setName("&cMulti Block Break Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.GOLDEN_PICKAXE))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{price} &7point(s).",
                        "&7This skill increases and allows multiple block breaks.",
                        "&7Level: &e{level}&7/&e{limit}&7",
                        " ",
                        "&cBreak amount: ",
                        "   &e{prev}x&7 >>> &e{next}x"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        double blockbreak = getLevel(player) * getUpgrade().getValue();
        return Utils.getPercentageFormat().format(blockbreak);
    }

    @Override
    public String getNextString(SPlayer player) {
        double blockbreak = (getLevel(player) + 1) * getUpgrade().getValue();
        return Utils.getPercentageFormat().format(blockbreak);
    }
}
