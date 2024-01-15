package me.itzjustsamu.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.BukkitItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.minecraft.item.ItemBuilder;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.Utils;
import me.itzjustsamu.playerskills.util.modifier.XMaterialModifier;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import static me.itzjustsamu.playerskills.util.Utils.getPercentageFormat;

public class ArcherySkill extends Skill {


    public ArcherySkill(PlayerSkills plugin) {
        super(plugin, "Archery", "archery", 20, 0, 0);
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
            int increment = getIncrement().getValue();
            double damage = event.getDamage() / 100;
            damage = damage * increment;
            double finalDamage = getLevel(sPlayer) * damage;
            event.setDamage(event.getDamage() + finalDamage);
        }
    }

    @Override
    public ItemBuilder<ItemStack> getDefaultItem() {
        return new BukkitItemBuilder()
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
        int increment = getIncrement().getValue();
        double archery = playerLevel * increment;
        return getPercentageFormat().format(archery);
    }

    @Override
    public String getNextString(SPlayer player) {
        int playerLevel = getLevel(player) + 1;
        int increment = getIncrement().getValue();
        double archery = playerLevel * increment;
        return getPercentageFormat().format(archery);
    }

}
