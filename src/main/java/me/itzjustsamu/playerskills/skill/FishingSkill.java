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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FishingSkill extends Skill {

    private final ConfigPath<Double> catchChanceIncrease = Paths.doublePath(new PathString("catch-chance-increase"), 2.0);

    public FishingSkill(PlayerSkills plugin) {
        super(plugin, "Fishing", "fishing", 20, 8);
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        SPlayer sPlayer = SPlayer.get(player.getUniqueId());
        if (Worlds_Restriction(player)) {
            return;
        }

        if (sPlayer == null) {
            if (MainConfig.isVerboseLogging()) {
                Utils.logError("Failed event. SPlayer for " + player.getUniqueId() + " is null.");
            }
            return;
        }

        int fishingLevel = getLevel(sPlayer);
        double catchChanceMultiplier = catchChanceIncrease.getValue() * fishingLevel;

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            if (event.getCaught() instanceof org.bukkit.entity.Item) {
                ItemStack caughtFish = ((org.bukkit.entity.Item) event.getCaught()).getItemStack();
                increaseCatchChance(caughtFish, catchChanceMultiplier);
            }
        }
    }

    private void increaseCatchChance(ItemStack itemStack, double increase) {
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            // Reduce the increase based on the desired reduction (e.g., 0.8 reduces by 20%)
            double reducedIncrease = increase * 0.8; // Adjust this value based on your desired reduction

            if (Math.random() * 100 < reducedIncrease) {
                // Increase the chance for the item to drop
                itemStack.setAmount(itemStack.getAmount() + 1);
            }
        }
    }

    @Override
    public ItemBuilder<ItemStack> getDefaultItem() {
        return new BukkitItemBuilder()
                .addItemModifier(new NameModifier().setName("&cFishing Mastery"))
                .addItemModifier(new XMaterialModifier(XMaterial.TROPICAL_FISH))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{price} &7point(s).",
                        "&7This skill increases chances of obtaining rare items and overall catch while fishing.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cCatch Chance Increase: ",
                        "   &e{prev}% &7 >>> &e{next}%"
                ));
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return List.of(catchChanceIncrease);
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int fishingLevel = getLevel(player);
        double catchChanceMultiplier = catchChanceIncrease.getValue() * fishingLevel;
        return Utils.getPercentageFormat().format(catchChanceMultiplier);
    }

    @Override
    public String getNextString(SPlayer player) {
        int fishingLevel = getLevel(player) + 1;
        double catchChanceMultiplier = catchChanceIncrease.getValue() * fishingLevel;
        return Utils.getPercentageFormat().format(catchChanceMultiplier);
    }
}