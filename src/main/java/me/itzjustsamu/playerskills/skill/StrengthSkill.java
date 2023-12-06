package me.itzjustsamu.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.itzjustsamu.playerskills.util.Utils;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.modifier.XMaterialModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Collections;
import java.util.List;

public class StrengthSkill extends Skill {
    private final ConfigPath<Double> damageIncrement = Paths.doublePath("damage-increment", 5D);

    public StrengthSkill(PlayerSkills plugin) {
        super(plugin, "Strength", "strength", 20, 22);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
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

        int strengthLevel = getLevel(sPlayer);

        double percentile = event.getDamage() / 100;
        percentile = percentile * damageIncrement.getValue();
        double weightedDamage = strengthLevel * percentile;
        event.setDamage(event.getDamage() + weightedDamage);
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(damageIncrement);
    }

    @Override
    public ItemBuilder getDefaultItem() {
        return new ItemBuilder()
                .addItemModifier(new NameModifier().setName("&cStrength Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.IRON_SWORD))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skillprice} &7point(s).",
                        "&7This skill increases damage dealt to other players.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cDamage dealt: ",
                        "   &e{prev}% &7 >>> &e{next}%"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int strengthLevel = getLevel(player);
        double damage = strengthLevel * damageIncrement.getValue();
        return Utils.getPercentageFormat().format(damage);
    }

    @Override
    public String getNextString(SPlayer player) {
        int strengthLevel = getLevel(player) + 1;
        double damage = strengthLevel * damageIncrement.getValue();
        return Utils.getPercentageFormat().format(damage);
    }
}
