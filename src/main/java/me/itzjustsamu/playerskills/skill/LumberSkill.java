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
import me.itzjustsamu.playerskills.util.modifier.XMaterialModifier;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class LumberSkill extends Skill {

    public LumberSkill(PlayerSkills plugin) {
        super(plugin, "Lumber", "lumber", 1, 13);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (isWorldRestricted(player)) {
            return;
        }

        SPlayer sPlayer = SPlayer.get(player.getUniqueId());

        if (sPlayer == null) {
            if (MainConfig.isVerboseLogging()) {
                Utils.logError("Failed event. SPlayer for " + player.getUniqueId() + " is null.");
            }
            return;
        }

        int lumberLevel = getLevel(sPlayer);

        if (lumberLevel > 0) {
            Block block = event.getBlock();
            breakTree(player, block);
        }
    }

    private void breakTree(Player player, Block block) {
        Material blockMaterial = block.getType();
        if (isLog(blockMaterial) && isAxe(player)) {
            breakBlock(block);

            // Recursively break connected logs
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        Block relativeBlock = block.getRelative(x, y, z);
                        Material relativeBlockMaterial = relativeBlock.getType();

                        if (isLog(relativeBlockMaterial)) {
                            breakTree(player, relativeBlock);
                        }
                    }
                }
            }
        }
    }

    private void breakBlock(Block block) {
        block.breakNaturally();
    }

    private boolean isLog(Material material) {
        return material.toString().endsWith("_LOG");
    }

    private boolean isAxe(Player player) {
        Material mainHand = player.getInventory().getItemInMainHand().getType();
        if (mainHand == null || mainHand == Material.AIR) {
            mainHand = player.getInventory().getItemInHand().getType();
        }
        Material offHand = player.getInventory().getItemInOffHand().getType();
        return mainHand.toString().endsWith("_AXE") || offHand.toString().endsWith("_AXE");
    }



    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(getUpgrade());
    }

    @Override
    public ItemBuilder<ItemStack> getDefaultItem() {
        return new BukkitItemBuilder()
                .addItemModifier(new NameModifier().setName("&cLumber Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.DIAMOND_AXE))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{price} &7point(s).",
                        "&7This skill lets you chop trees down.",
                        "&7Level: &e{level}&7/&e{limit}&7",
                        " ",
                        "&cLumber Upgrade: ",
                        "   &e{prev} &7 >>> &e{next}+"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        double lumber = getLevel(player) * getUpgrade().getValue();
        return Utils.getPercentageFormat().format(lumber);
    }

    @Override
    public String getNextString(SPlayer player) {
        double lumber = (getLevel(player) + 1) * getUpgrade().getValue();
        return Utils.getPercentageFormat().format(lumber);
    }
}
