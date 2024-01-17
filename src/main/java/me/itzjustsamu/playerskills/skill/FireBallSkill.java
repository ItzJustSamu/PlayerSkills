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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;

public class FireBallSkill extends Skill implements Listener {

    public FireBallSkill(PlayerSkills plugin) {
        super(plugin, "FireBall", "fireball", 5, 7);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

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

        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.FIRE_CHARGE) {
            Action action = event.getAction();

            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                return;
            }

            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                int skillLevel = getLevel(sPlayer);

                if (skillLevel > 0) {
                    event.setCancelled(true);
                    summonFireballAtLocation(player);

                    int itemAmount = item.getAmount();
                    if (itemAmount > 1) {
                        item.setAmount(itemAmount - 1);
                    } else {
                        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    }
                }
            }
        }
    }

    private void summonFireballAtLocation(Player player) {
        Location eyeLocation = player.getEyeLocation();
        Vector initialVelocity = new Vector(0, 0, 0);

        Location spawnLocation = eyeLocation.add(eyeLocation.getDirection());
        World world = spawnLocation.getWorld();

        if (world == null) {
            return;
        }

        Fireball fireball = (Fireball) world.spawnEntity(spawnLocation, org.bukkit.entity.EntityType.FIREBALL);
        fireball.setVelocity(initialVelocity);
        fireball.setIsIncendiary(false);
        fireball.setShooter(player);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Fireball) {
            Fireball fireball = (Fireball) event.getEntity();
            explodeFireball(fireball);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Fireball) {
            event.getDamager();
            Fireball fireball = (Fireball) event.getEntity();

            if (!(fireball.getShooter() instanceof LivingEntity)) {
                return;
            }

            LivingEntity shooter = (LivingEntity) fireball.getShooter();

            setFireballVelocity(fireball, shooter.getLocation().getDirection());
        }
    }

    public void setFireballVelocity(Fireball fireball, Vector velocity) {
        fireball.setVelocity(velocity.multiply(getIncrement().getValue()));
    }

    private void explodeFireball(Fireball fireball) {
        World world = fireball.getWorld();

        world.createExplosion(fireball.getLocation(), 1.5F);
        fireball.remove();
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(getIncrement());
    }

    @Override
    public ItemBuilder<ItemStack> getDefaultItem() {
        return new BukkitItemBuilder()
                .addItemModifier(new NameModifier().setName("&cFireBall Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.FIRE_CHARGE))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skill-points-price} &7point(s).",
                        "&7This skill allows you to summon a fireball at the location where the fire charge was used.",
                        "&7When the fireball hits a surface, it will explode.",
                        "&7Level: &e{level}&7/&e{max}&7"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        return String.valueOf(getIncrement().getValue());
    }

    @Override
    public String getNextString(SPlayer player) {
        return String.valueOf(getIncrement().getValue() + 1);
    }
}
