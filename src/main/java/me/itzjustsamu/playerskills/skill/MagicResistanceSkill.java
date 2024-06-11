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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class MagicResistanceSkill extends Skill implements Listener {

    public MagicResistanceSkill(PlayerSkills plugin) {
        super(plugin, "MagicResistance", "magicresistance", 10, 26);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            SPlayer sPlayer = SPlayer.get(player.getUniqueId());

            if (sPlayer != null && getLevel(sPlayer) > 0) {
                if (event.getCause() == EntityDamageEvent.DamageCause.MAGIC ||
                        event.getCause() == EntityDamageEvent.DamageCause.MAGIC) {
                    double resistanceLevel = getLevel(sPlayer) * getUpgrade().getValue();
                    double reduction = resistanceLevel / 100.0; // Convert resistance level to percentage

                    // Reduce the damage by the calculated percentage
                    event.setDamage(event.getDamage() * (1 - reduction));
                }
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
                .addItemModifier(new NameModifier().setName("&cMagic Resistance Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.ENCHANTED_BOOK))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{price} &7point(s).",
                        "&7This skill provides resistance against magical damage.",
                        "&7Level: &e{level}&7/&e{limit}&7",
                        " ",
                        "&cResistance Increase: ",
                        "   &e{prev}% &7 >>> &e{next}%"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        double resistanceLevel = getLevel(player) * getUpgrade().getValue();
        return String.valueOf(resistanceLevel);
    }

    @Override
    public String getNextString(SPlayer player) {
        double resistanceLevel = (getLevel(player) + 1) * getUpgrade().getValue();
        return String.valueOf(resistanceLevel);
    }
}
