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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class LootingSkill extends Skill {
    public LootingSkill(PlayerSkills plugin) {
        super(plugin, "Looting", "looting", 20, 15);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
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

            int lootingLevel = getLevel(sPlayer);

            if (lootingLevel > 0) {
                double lootingIncrementValue = getIncrement().getValue();
                List<ItemStack> drops = event.getDrops();

                for (ItemStack drop : drops) {
                    // Apply looting bonus
                    int amount = (int) (drop.getAmount() * lootingLevel * lootingIncrementValue);
                    drop.setAmount(amount);
                }
            }
        }
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(getIncrement());
    }

    @Override
    public ItemBuilder<ItemStack> getDefaultItem() {
        return new BukkitItemBuilder()
                .addItemModifier(new NameModifier().setName("&cLooting Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.BONE))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skill-price} &7point(s).",
                        "&7This skill increases loot drop rates.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cLoot Bonus: ",
                        "   &e{prev}x &7 >>> &e{next}x"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        double lootBonus = getLevel(player) * getIncrement().getValue();
        return Utils.getPercentageFormat().format(lootBonus);
    }

    @Override
    public String getNextString(SPlayer player) {
        double lootBonus = (getLevel(player) + 1) * getIncrement().getValue();
        return Utils.getPercentageFormat().format(lootBonus);
    }
}