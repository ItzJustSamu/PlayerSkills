package me.itzjustsamu.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.messages.ActionBar;
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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class InstantBreakerSkill extends Skill {
    private final ConfigPath<Integer> COOLDOWN_MAX = Paths.integerPath(new PathString("cooldown-max"), 30); // Default: 30 seconds
    private final HashMap<Player, Long> cooldownMap = new HashMap<>();

    public InstantBreakerSkill(PlayerSkills plugin) {
        super(plugin, "InstantBreaker", "instantbreaker", 10, 11);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        SPlayer sPlayer = SPlayer.get(player.getUniqueId());
        if (Worlds_Restriction(player)) {
            return;
        }

        if (sPlayer == null) {
            if (MainConfig.isVerboseLogging()) {
                Utils.logError("Failed event. SPlayer for " + player.getUniqueId() + " is null.");
            }
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK && getLevel(sPlayer) > 0) {
            if (!hasCooldown(player, sPlayer)) {
                Block block = event.getClickedBlock();

                if (block != null && block.getType() != Material.AIR) {
                    block.breakNaturally();
                    setCooldown(player, sPlayer);
                }
            }
        }
    }

    private boolean hasCooldown(Player player, SPlayer sPlayer) {
        long currentTime = System.currentTimeMillis();
        long cooldown = Math.min((long) getLevel(sPlayer) * getUpgrade().getValue(), COOLDOWN_MAX.getValue());

        if (cooldownMap.containsKey(player) && currentTime < cooldownMap.get(player)) {
            sendActionBar(player, String.valueOf(cooldownMap.get(player) - currentTime));
            return true;
        }

        return false;
    }

    private void setCooldown(Player player, SPlayer sPlayer) {
        long cooldown = Math.min((long) getLevel(sPlayer) * getUpgrade().getValue(), COOLDOWN_MAX.getValue());
        cooldownMap.put(player, System.currentTimeMillis() + cooldown);
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(getUpgrade());
    }

    @Override
    public ItemBuilder<ItemStack> getDefaultItem() {
        return new BukkitItemBuilder()
                .addItemModifier(new NameModifier().setName("&cInstant Breaker Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.DIAMOND_PICKAXE))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{price} &7point(s).",
                        "&7This skill allows you to instantly break blocks with a cooldown based on skill level.",
                        "&7Level: &e{level}&7/&e{limit}&7",
                        " ",
                        "&cCooldown: ",
                        "   &e{prev}% &7 >>> &e{next}%"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        return Utils.getPercentageFormat().format(Math.min((long) getLevel(player) * getUpgrade().getValue(), COOLDOWN_MAX.getValue()));
    }

    @Override
    public String getNextString(SPlayer player) {
        return Utils.getPercentageFormat().format(Math.min((long) (getLevel(player) + 1) * getUpgrade().getValue(), COOLDOWN_MAX.getValue()));
    }

    private void sendActionBar(Player player, String message) {
        ActionBar.sendActionBar(getPlugin(), player, message);
    }
}
