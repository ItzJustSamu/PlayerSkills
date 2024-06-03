package me.itzjustsamu.playerskills.skill;

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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import com.cryptomorin.xseries.XMaterial;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ExcavatorSkill extends Skill {

    public ExcavatorSkill(PlayerSkills plugin) {
        super(plugin, "Excavator", "excavator", 20, 22);
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

        Material blockType = event.getBlock().getType();
        if (isDiggable(blockType)) {
            int level = getLevel(sPlayer);
            double chance = level * getUpgrade().getValue();

            if (new Random().nextDouble() * 100 < chance) {
                ItemStack treasure = getRandomTreasure();
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), treasure);
            }
        }
    }

    private boolean isDiggable(Material material) {
        return material == Material.DIRT || material == Material.SAND || material == Material.GRAVEL || material == Material.CLAY;
    }

    private ItemStack getRandomTreasure() {
        Material[] treasures;

        if (VersionControl.isOldVersion()) {
            treasures = new Material[]{
                    Material.DIAMOND,
                    Material.EMERALD,
                    Material.GOLD_INGOT,
                    Material.IRON_INGOT,
                    Material.REDSTONE,
                    Material.QUARTZ,
                    Material.COAL,
                    Material.CARROT,
                    Material.STICK,
                    Material.BOWL,
                    Material.GLASS_BOTTLE,
                    Material.ROTTEN_FLESH,
                    Material.BONE,
                    Material.STRING,
                    Material.SPIDER_EYE,
                    Material.EGG
            };
        } else {
            treasures = new Material[]{
                    Material.DIAMOND,
                    Material.EMERALD,
                    Material.GOLD_INGOT,
                    Material.IRON_INGOT,
                    Material.LAPIS_LAZULI,
                    Material.REDSTONE,
                    Material.QUARTZ,
                    Material.COAL,
                    Material.CARROT,
                    Material.STICK,
                    Material.BOWL,
                    Material.GLASS_BOTTLE,
                    Material.ROTTEN_FLESH,
                    Material.BONE,
                    Material.STRING,
                    Material.SPIDER_EYE,
                    Material.EGG
            };
        }


        return new ItemStack(treasures[new Random().nextInt(treasures.length)]);
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(getUpgrade());
    }

    @Override
    public ItemBuilder<ItemStack> getDefaultItem() {
        return new BukkitItemBuilder()
                .addItemModifier(new NameModifier().setName("&eExcavator Skill Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.CHEST))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{price} &7point(s).",
                        "&7This skill gives you a chance to find treasures when digging.",
                        "&7Level: &e{level}&7/&e{limit}&7",
                        " ",
                        "&eTreasure Chance: ",
                        "   &e{prev}%&7 >>> &e{next}%"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        double chance = getLevel(player) * getUpgrade().getValue();
        return Utils.getPercentageFormat().format(chance);
    }

    @Override
    public String getNextString(SPlayer player) {
        double chance = (getLevel(player) + 1) * getUpgrade().getValue();
        return Utils.getPercentageFormat().format(chance);
    }
}
