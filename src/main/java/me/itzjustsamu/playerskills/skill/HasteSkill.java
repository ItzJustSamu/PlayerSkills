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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class HasteSkill extends Skill implements Listener {

    public HasteSkill(PlayerSkills plugin) {
        super(plugin, "Haste", "haste", 10, 8);
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

        // Check if the player has the haste skill
        if (getLevel(sPlayer) > 0) {
            int hasteAmount = getLevel(sPlayer) * getUpgrade().getValue(); // Set the initial haste amount based on skill level

            // Apply haste effect to the player
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, hasteAmount - 1, false, false));
        }
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(getUpgrade());
    }

    @Override
    public ItemBuilder<ItemStack> getDefaultItem() {
        return new BukkitItemBuilder()
                .addItemModifier(new NameModifier().setName("&cHaste Skill Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.DIAMOND_PICKAXE))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{price} &7point(s).",
                        "&7This skill increases block break speed.",
                        "&7Level: &e{level}&7/&e{limit}&7",
                        " ",
                        "&cHaste amount: ",
                        "   &e{prev}x&7 >>> &e{next}x"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        double haste = getLevel(player) * getUpgrade().getValue();
        return Utils.getPercentageFormat().format(haste);
    }

    @Override
    public String getNextString(SPlayer player) {
        double haste = (getLevel(player) + 1) * getUpgrade().getValue();
        return Utils.getPercentageFormat().format(haste);
    }
}
