package me.itzjustsamu.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.util.Utils;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.modifier.XMaterialModifier;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Collections;
import java.util.List;

public class LumberSkill extends Skill {
    private final ConfigPath<Double> lumberUpgrade = Paths.doublePath("lumber-upgrade", 1.0);

    public LumberSkill(PlayerSkills plugin) {
        super(plugin, "Lumber", "lumber", 1, 16,0);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (Worlds_Restriction(player)) {
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
            breakBlock(player, block);

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

    private void breakBlock(Player player, Block block) {
            block.breakNaturally();
        }

    private boolean isLog(Material material) {
        return material.toString().endsWith("_LOG");
    }

    private boolean isAxe(Player player) {
        Material mainHand = player.getInventory().getItemInMainHand().getType();
        Material offHand = player.getInventory().getItemInOffHand().getType();
        return mainHand.toString().endsWith("_AXE") || offHand.toString().endsWith("_AXE");
    }


    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(lumberUpgrade);
    }

    @Override
    public ItemBuilder getDefaultItem() {
        return new ItemBuilder()
                .addItemModifier(new NameModifier().setName("&cLumber Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.DIAMOND_AXE))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skillprice} &7point(s).",
                        "&7This skill lets you chop trees down.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cLumber Upgrade: ",
                        "   &e{prev} &7 >>> &e{next}+"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int lumberLevel = getLevel(player);
        double lumber = lumberLevel * lumberUpgrade.getValue();
        return Utils.getPercentageFormat().format(lumber);
    }

    @Override
    public String getNextString(SPlayer player) {
        int lumberLevel = getLevel(player) + 1;
        double lumber = lumberLevel * lumberUpgrade.getValue();
        return Utils.getPercentageFormat().format(lumber);
    }
}
