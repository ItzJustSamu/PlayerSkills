package me.itzjustsamu.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.Utils;
import me.itzjustsamu.playerskills.util.modifier.XMaterialModifier;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Collections;
import java.util.List;

import static me.itzjustsamu.playerskills.skill.SkillEffect.playSound;
import static me.itzjustsamu.playerskills.util.Utils.getPercentageFormat;

public class ArcherySkill extends Skill {
    private final ConfigPath<Double> DAMAGE_INCREMENT = Paths.doublePath("Damage-increment", 3D);

    public ArcherySkill(PlayerSkills plugin) {
        super(plugin, "Archery", "archery", 20, 9);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow)) {
            return;
        }

        Arrow arrow = (Arrow) event.getDamager();
        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) arrow.getShooter();

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

        if (getLevel(sPlayer) > 0) {
            playSound(player);

            double damage = event.getDamage() / 100;
            damage = damage * DAMAGE_INCREMENT.getValue();
            double finalDamage = getLevel(sPlayer) * damage;
            event.setDamage(event.getDamage() + finalDamage);
        }
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(DAMAGE_INCREMENT);
    }

    @Override
    public ItemBuilder getDefaultItem() {
        return new ItemBuilder()
                .addItemModifier(new NameModifier().setName("&cArchery Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.BOW))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skillprice} &7point(s).",
                        "&7This skill increases damage dealt using bows.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cBow Upgrade: ",
                        "   &e{prev}% &7 >>> &e{next}%"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int playerLevel = getLevel(player);
        double archery = playerLevel * DAMAGE_INCREMENT.getValue();
        return getPercentageFormat().format(archery);
    }

    @Override
    public String getNextString(SPlayer player) {
        int playerLevel = getLevel(player) + 1;
        double archery = playerLevel * DAMAGE_INCREMENT.getValue();
        return getPercentageFormat().format(archery);
    }
}
