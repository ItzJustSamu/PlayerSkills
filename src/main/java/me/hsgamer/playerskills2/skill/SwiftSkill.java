package me.hsgamer.playerskills2.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.hsgamer.playerskills2.PlayerSkills;
import me.hsgamer.playerskills2.config.MainConfig;
import me.hsgamer.playerskills2.player.SPlayer;
import me.hsgamer.playerskills2.util.Utils;
import me.hsgamer.playerskills2.util.modifier.XMaterialModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static me.hsgamer.playerskills2.util.Utils.getPercentageFormat;

public class SwiftSkill extends Skill {

    private final ConfigPath<Double> swiftIncrement = Paths.doublePath("swift-increment", 1D);

    public SwiftSkill(PlayerSkills plugin) {
        super(plugin, "Speed", "speed", 10, 15);
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (isWorldNotAllowed(player)) {
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
        double speedMultiplier = 1.0f + (speedLevel * swiftIncrement.getValue());

        player.setWalkSpeed((float) speedMultiplier);
    }


    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(swiftIncrement);
    }

    @Override
    public ItemBuilder getDefaultItem() {
        return new ItemBuilder()
                .addItemModifier(new NameModifier().setName("&cSpeed Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.IRON_BOOTS))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skillprice} &7point(s).",
                        "&7This skill increases your movement speed.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cSpeed Increase: ",
                        "   &e{prev}x &7 >>> &e{next}x"
                ));
    }

        @Override
        public String getPreviousString(SPlayer player) {
            int swiftLevel = getLevel(player);
            double swift = 1.0 + (swiftLevel * swiftIncrement.getValue());
            return getPercentageFormat().format(swift);
        }

        @Override
        public String getNextString(SPlayer player) {
            int swiftLevel=getLevel(player)+1;
            double swift = 1.0 + (swiftLevel * swiftIncrement.getValue());
            return getPercentageFormat().format(swift);
        }
        }
