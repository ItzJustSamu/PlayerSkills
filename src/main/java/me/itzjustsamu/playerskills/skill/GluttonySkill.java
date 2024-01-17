package me.itzjustsamu.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.BukkitItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.PathString;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.hsgamer.hscore.minecraft.item.ItemBuilder;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.Utils;
import me.itzjustsamu.playerskills.util.modifier.XMaterialModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class GluttonySkill extends Skill {
    private final ConfigPath<Double> percentIncrease = Paths.doublePath(new PathString("percent-increase"), 3D);

    public GluttonySkill(PlayerSkills plugin) {
        super(plugin, "Gluttony", "gluttony", 20, 9);
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (Worlds_Restriction(player)) {
            return;
        }

        if (player.getFoodLevel() >= event.getFoodLevel()) {
            return;
        }

        event.setCancelled(true);
        SPlayer sPlayer = SPlayer.get(player.getUniqueId());

        if (sPlayer == null) {
            if (MainConfig.isVerboseLogging()) {
                Utils.logError("Failed event. SPlayer for " + player.getUniqueId() + " is null.");
            }
            return;
        }

        int diff = event.getFoodLevel() - player.getFoodLevel();
        int gluttonyLevel = getLevel(sPlayer);
        double multiplier = 1D + (gluttonyLevel * (percentIncrease.getValue() / 100D));

        double newLevel = diff * multiplier;
        player.setFoodLevel(player.getFoodLevel() + (int) newLevel);
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(percentIncrease);
    }

    @Override
    public ItemBuilder<ItemStack> getDefaultItem() {
        return new BukkitItemBuilder()
                .addItemModifier(new NameModifier().setName("&cGluttony Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.WHEAT))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skill-points-price} &7point(s).",
                        "&7This skill increases the amount of food ingested from a single item.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cFood heal amount: ",
                        "   &e{prev}% &7 >>> &e{next}%"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int gluttonyLevel = getLevel(player);
        double heal = gluttonyLevel * percentIncrease.getValue();
        return Utils.getPercentageFormat().format(heal);
    }

    @Override
    public String getNextString(SPlayer player) {
        int gluttonyLevel = getLevel(player) + 1;
        double heal = gluttonyLevel * percentIncrease.getValue();
        return Utils.getPercentageFormat().format(heal);
    }
}