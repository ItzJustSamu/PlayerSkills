package me.itzjustsamu.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.BukkitItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.minecraft.item.ItemBuilder;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.Utils;
import me.itzjustsamu.playerskills.util.modifier.XMaterialModifier;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class WitherRewardSkill extends Skill {

    public WitherRewardSkill(PlayerSkills plugin) {
        super(plugin, "WitherReward", "witherreward", 20, 24);
    }

    @EventHandler
    public void onWitherDeath(EntityDeathEvent event) {
        if (event.getEntityType() != EntityType.WITHER) {
            return;
        }

        Wither wither = (Wither) event.getEntity();
        Player player = wither.getKiller();
        if (player == null) {
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

        int level = getLevel(sPlayer);
        double chance = level * getUpgrade().getValue();

        Random random = new Random();
        if (random.nextDouble() * 100 < chance) {
            List<ItemStack> rewards = getRewards(level);
            for (ItemStack reward : rewards) {
                wither.getWorld().dropItemNaturally(wither.getLocation(), reward);
            }
        }
    }

    private List<ItemStack> getRewards(int level) {
        ItemStack witherSkull = XMaterial.WITHER_SKELETON_SKULL.parseItem();
        ItemStack netherStar = new ItemStack(Material.NETHER_STAR);
        ItemStack[] possibleRewards = new ItemStack[]{witherSkull, netherStar};

        List<ItemStack> rewards = new ArrayList<>();
        Random random = new Random();
        int rewardCount = 1 + (level / 5);  // Increase reward count with skill level, e.g., +1 reward every 5 levels

        for (int i = 0; i < rewardCount; i++) {
            rewards.add(possibleRewards[random.nextInt(possibleRewards.length)]);
        }

        return rewards;
    }


    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(getUpgrade());
    }

    @Override
    public ItemBuilder<ItemStack> getDefaultItem() {
        return new BukkitItemBuilder()
                .addItemModifier(new NameModifier().setName("&eWither Reward Skill Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.WITHER_SKELETON_SKULL))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{price} &7point(s).",
                        "&7This skill gives you a chance to get additional rewards from Wither.",
                        "&7Level: &e{level}&7/&e{limit}&7",
                        " ",
                        "&eReward Chance: ",
                        "   &e{prev}%&7 >>> &e{next}%"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        double chance = getLevel(player) * getUpgrade().getValue();
        return Utils.getPercentageFormat().format(chance);
    }

    @Override
    public String getNextString(SPlayer player) {
        double chance = (getLevel(player) + 1) * getUpgrade().getValue();
        return Utils.getPercentageFormat().format(chance);
    }
}
