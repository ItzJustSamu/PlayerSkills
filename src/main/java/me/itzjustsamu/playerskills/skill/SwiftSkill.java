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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SwiftSkill extends Skill {

    private final ConfigPath<Double> DEFAULT_SPEED = Paths.doublePath(new PathString("default-speed"), 0.2);

    public SwiftSkill(PlayerSkills plugin) {
        super(plugin, "Swift", "swift", 5, 20);
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (Worlds_Restriction(player)) {
            return;
        }

        UUID uniqueId = player.getUniqueId();
        SPlayer sPlayer = SPlayer.get(uniqueId);

        if (sPlayer == null) {
            if (MainConfig.isVerboseLogging()) {
                Utils.logError("Failed event. SPlayer for " + uniqueId + " is null.");
            }
            return;
        }

        int speedLevel = getLevel(sPlayer);

        if (speedLevel == 0) {
            double defaultSpeed = DEFAULT_SPEED.getValue();
            float defaultSpeedFloat = (float) defaultSpeed;
            player.setWalkSpeed(defaultSpeedFloat);
        } else {
            double speedMultiplier = 0.1 + (speedLevel * getIncrement().getValue());
            float validSpeed = (float) Math.max(-1.0, Math.min(1.0, speedMultiplier));
            player.setWalkSpeed(validSpeed);
        }
    }


    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(getIncrement());
    }

    @Override
    public ItemBuilder<ItemStack> getDefaultItem() {
        return new BukkitItemBuilder()
                .addItemModifier(new NameModifier().setName("&cSpeed Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.IRON_BOOTS))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{price} &7point(s).",
                        "&7This skill increases your movement speed.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cSpeed Increase: ",
                        "   &e{prev}x &7 >>> &e{next}x"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        double swift = getLevel(player) * getIncrement().getValue();
        return Utils.getPercentageFormat().format(swift);
    }

    @Override
    public String getNextString(SPlayer player) {
        double swift = (getLevel(player) + 1) * getIncrement().getValue();
        return Utils.getPercentageFormat().format(swift);
    }
}