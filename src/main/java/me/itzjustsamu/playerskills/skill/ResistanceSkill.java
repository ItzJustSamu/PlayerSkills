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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class ResistanceSkill extends Skill {
    private final ConfigPath<Double> damageDrop = Paths.doublePath(new PathString("damage-drop"), 3D);

    public ResistanceSkill(PlayerSkills plugin) {
        super(plugin, "Resistance", "resistance", 20, 17);
    }

    @EventHandler
    public void onHit(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
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

        int resistanceLevel = getLevel(sPlayer);

        double percentile = event.getDamage() / 100;
        percentile = percentile * damageDrop.getValue();
        double weightedDamage = resistanceLevel * percentile;
        event.setDamage(event.getDamage() - weightedDamage);
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(damageDrop);
    }

    @Override
    public ItemBuilder<ItemStack> getDefaultItem() {
        return new BukkitItemBuilder()
                .addItemModifier(new NameModifier().setName("&cResistance Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.IRON_CHESTPLATE))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skill-price} &7point(s).",
                        "&7This skill decreases damage received.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cIncoming damage: ",
                        "   &e{prev}% &7 >>> &e{next}%"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int resistanceLevel = getLevel(player);
        double damage = 100 - (resistanceLevel * damageDrop.getValue());
        return Utils.getPercentageFormat().format(damage);
    }

    @Override
    public String getNextString(SPlayer player) {
        int resistanceLevel = getLevel(player) + 1;
        double damage = 100 - (resistanceLevel * damageDrop.getValue());
        return Utils.getPercentageFormat().format(damage);
    }
}
