package com.leonardobishop.playerskills2.skill;

import com.cryptomorin.xseries.XMaterial;
import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.util.modifier.XMaterialModifier;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.config.ConfigPath;
import me.hsgamer.hscore.config.path.Paths;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.leonardobishop.playerskills2.util.Utils.getPercentageFormat;

public class DodgeSkill extends Skill {
    private final ConfigPath<Double> percentIncrease = Paths.doublePath("percent-increase", 2D);
    private final ConfigPath<String> dodgeMessage = Paths.stringPath("dodge-message", "&a*** ATTACK DODGED ***");

    public DodgeSkill(PlayerSkills plugin) {
        super(plugin, "Dodge", "dodge", 6, 13);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (isWorldNotAllowed(player)) {
            return;
        }

        SPlayer sPlayer = SPlayer.get(player.getUniqueId());

        if (sPlayer == null) {
            if (super.getPlugin().isVerboseLogging()) {
                super.getPlugin().logError("Failed event. SPlayer for " + player.getUniqueId() + " is null.");
            }
            return;
        }

        int dodgeLevel = getLevel(sPlayer);

        double chance = dodgeLevel * percentIncrease.getValue();

        if (ThreadLocalRandom.current().nextInt(100) < chance) {
            String message = dodgeMessage.getValue();
            if (!message.equals("")) {
                MessageUtils.sendMessage(player, message, "");
            }
            event.setCancelled(true);
        }
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(percentIncrease);
    }

    @Override
    public List<ConfigPath<?>> getMessageConfigPaths() {
        return Collections.singletonList(dodgeMessage);
    }

    @Override
    public ItemBuilder getDefaultItem() {
        return new ItemBuilder()
                .addItemModifier(new NameModifier().setName("&cDodge Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.SUGAR))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skillprice} &7point(s).",
                        "&7This skill gives a chance to completely dodge attacks.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cDodge chance: ",
                        "   &e{prev}% &7 >>> &e{next}%"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int dodgeLevel = getLevel(player);
        double damage = dodgeLevel * percentIncrease.getValue();
        return getPercentageFormat().format(damage);
    }

    @Override
    public String getNextString(SPlayer player) {
        int dodgeLevel = getLevel(player) + 1;
        double damage = dodgeLevel * percentIncrease.getValue();
        return getPercentageFormat().format(damage);
    }
}
