package me.hsgamer.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.hsgamer.playerskills.PlayerSkills;
import me.hsgamer.playerskills.config.MainConfig;
import me.hsgamer.playerskills.player.SPlayer;
import me.hsgamer.playerskills.util.Utils;
import me.hsgamer.playerskills.util.modifier.XMaterialModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Collections;
import java.util.List;

import static me.hsgamer.playerskills.util.Utils.getPercentageFormat;

public class ResistanceSkill extends Skill {
    private final ConfigPath<Double> damageDrop = Paths.doublePath("damage-drop", 3D);

    public ResistanceSkill(PlayerSkills plugin) {
        super(plugin, "Resistance", "resistance", 20, 20);
    }

    @EventHandler
    public void onHit(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (isWorldNotAllowed(player)) {
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
    public ItemBuilder getDefaultItem() {
        return new ItemBuilder()
                .addItemModifier(new NameModifier().setName("&cResistance Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.IRON_CHESTPLATE))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skillprice} &7point(s).",
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
        return getPercentageFormat().format(damage);
    }

    @Override
    public String getNextString(SPlayer player) {
        int resistanceLevel = getLevel(player) + 1;
        double damage = 100 - (resistanceLevel * damageDrop.getValue());
        return getPercentageFormat().format(damage);
    }
}
