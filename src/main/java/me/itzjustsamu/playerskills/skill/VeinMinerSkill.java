package me.itzjustsamu.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.BukkitItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.minecraft.item.ItemBuilder;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.modifier.XMaterialModifier;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class VeinMinerSkill extends Skill implements Listener {

    public VeinMinerSkill(PlayerSkills plugin) {
        super(plugin, "VeinMiner", "veinminer", 1, 25);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();
        SPlayer sPlayer = SPlayer.get(uniqueId);

        if (sPlayer == null) {
            return;
        }

        if (getLevel(sPlayer) > 0) {
            ItemStack itemInHand = getItemInMainHand(player);

            if (itemInHand.getType() == Material.DIAMOND_PICKAXE) {
                Block block = event.getBlock();
                Material material = block.getType();

                if (isOre(material)) {
                    List<Block> veinBlocks = getVeinBlocks(block, material);

                    for (Block veinBlock : veinBlocks) {
                        veinBlock.breakNaturally(); // Break the block
                    }
                }
            }
        }
    }

    private ItemStack getItemInMainHand(Player player) {
        ItemStack itemInMainHand = null;
        try {
            itemInMainHand = player.getInventory().getItemInMainHand();
        } catch (NoSuchMethodError ignored) {
            // Version is likely pre-1.9, handle it accordingly
        }
        if (itemInMainHand == null || itemInMainHand.getType() == Material.AIR) {
            // If main hand is null or air (empty hand), try getting from the whole inventory
            itemInMainHand = player.getInventory().getItemInHand();
        }
        return itemInMainHand;
    }

    private boolean isOre(Material material) {
        switch (material) {
            case COAL_ORE:
            case IRON_ORE:
            case GOLD_ORE:
            case LAPIS_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case REDSTONE_ORE:
                return true;
            default:
                return false;
        }
    }

    private List<Block> getVeinBlocks(Block originBlock, Material material) {
        List<Block> veinBlocks = new ArrayList<>();
        List<Block> queue = new ArrayList<>();
        List<Block> visited = new ArrayList<>();
        queue.add(originBlock);

        while (!queue.isEmpty()) {
            Block currentBlock = queue.remove(0);

            if (!visited.contains(currentBlock) && currentBlock.getType() == material) {
                visited.add(currentBlock);
                veinBlocks.add(currentBlock);

                for (Block relativeBlock : getAdjacentBlocks(currentBlock)) {
                    if (!visited.contains(relativeBlock) && !queue.contains(relativeBlock)) {
                        queue.add(relativeBlock);
                    }
                }
            }
        }

        return veinBlocks;
    }

    private List<Block> getAdjacentBlocks(Block block) {
        List<Block> adjacentBlocks = new ArrayList<>();

        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int yOffset = -1; yOffset <= 1; yOffset++) {
                for (int zOffset = -1; zOffset <= 1; zOffset++) {
                    if (xOffset != 0 || yOffset != 0 || zOffset != 0) {
                        Block relativeBlock = block.getWorld().getBlockAt(
                                block.getX() + xOffset,
                                block.getY() + yOffset,
                                block.getZ() + zOffset
                        );
                        adjacentBlocks.add(relativeBlock);
                    }
                }
            }
        }

        return adjacentBlocks;
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(getUpgrade());
    }

    @Override
    public ItemBuilder<ItemStack> getDefaultItem() {
        return new BukkitItemBuilder()
                .addItemModifier(new NameModifier().setName("&cVeinMiner Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.DIAMOND_PICKAXE))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{price} &7point(s).",
                        "&7Break entire veins of ores with a single hit.",
                        "&7Level: &e{level}&7/&e{limit}&7",
                        " ",
                        "&cVein Upgrade: ",
                        "   &e{prev} &7 >>> &e{next}+"
                ));
    }
    @Override
    public String getPreviousString(SPlayer player) {
        double value = getLevel(player) * getUpgrade().getValue();
        return String.valueOf(value);
    }

    @Override
    public String getNextString(SPlayer player) {
        double value = (getLevel(player) + 1) * getUpgrade().getValue();
        return String.valueOf(value);
    }
}
