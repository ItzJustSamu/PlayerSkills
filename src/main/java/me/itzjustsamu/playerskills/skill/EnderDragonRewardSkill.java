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
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EnderDragonRewardSkill extends Skill {

    public EnderDragonRewardSkill(PlayerSkills plugin) {
        super(plugin, "EnderDragonReward", "enderdragonreward", 20, 23);
    }

    @EventHandler
    public void onEnderDragonDeath(EntityDeathEvent event) {
        if (event.getEntityType() != EntityType.ENDER_DRAGON) {
            return;
        }

        EnderDragon dragon = (EnderDragon) event.getEntity();
        Player player = dragon.getKiller();
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

        if (new Random().nextDouble() * 100 < chance) {
            List<ItemStack> rewards = getRewards(level);
            for (ItemStack reward : rewards) {
                dragon.getWorld().dropItemNaturally(dragon.getLocation(), reward);
            }
        }
    }

    private List<ItemStack> getRewards(int level) {
        ItemStack enderPearl = new ItemStack(Material.ENDER_PEARL);
        ItemStack expBottle = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemStack dragonBreath = new ItemStack(Material.DRAGON_BREATH);
        ItemStack[] possibleRewards = new ItemStack[]{enderPearl, expBottle, dragonBreath};

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
                .addItemModifier(new NameModifier().setName("&eEnder Dragon Reward Skill Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.DRAGON_EGG))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{price} &7point(s).",
                        "&7This skill gives you a chance to get additional rewards from Ender Dragon.",
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
