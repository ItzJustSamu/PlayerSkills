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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class LootingSkill extends Skill implements Listener {
    public LootingSkill(PlayerSkills plugin) {
        super(plugin, "Looting", "looting", 10, 12);
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

            if (getLevel(sPlayer) > 0) {
                List<ItemStack> drops = event.getDrops();
                double reductionFactor = 0.5;

                for (ItemStack drop : drops) {
                    int amount = drop.getAmount() * getLevel(sPlayer) * getUpgrade().getValue();
                    int reducedAmount = (int) (amount * reductionFactor);
                    setItemAmount(drop, reducedAmount);
                }
            }
        }
    }

    private void setItemAmount(ItemStack itemStack, int amount) {
        try {
            Method setAmountMethod = itemStack.getClass().getMethod("setAmount", int.class);
            setAmountMethod.invoke(itemStack, amount);
        } catch (Exception e) {
            if (MainConfig.isVerboseLogging()) {
                Utils.logError("Failed to set item amount: " + e.getMessage());
            }
        }
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(getUpgrade());
    }

    @Override
    public ItemBuilder<ItemStack> getDefaultItem() {
        return new BukkitItemBuilder()
                .addItemModifier(new NameModifier().setName("&cLooting Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.BONE))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{price} &7point(s).",
                        "&7This skill increases loot drop rates.",
                        "&7Level: &e{level}&7/&e{limit}&7",
                        " ",
                        "&cLoot Bonus: ",
                        "   &e{prev}% &7 >>> &e{next}%"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        double lootBonus = getLevel(player) * getUpgrade().getValue();
        return Utils.getPercentageFormat().format(lootBonus);
    }

    @Override
    public String getNextString(SPlayer player) {
        double lootBonus = (getLevel(player) + 1) * getUpgrade().getValue();
        return Utils.getPercentageFormat().format(lootBonus);
    }
}
