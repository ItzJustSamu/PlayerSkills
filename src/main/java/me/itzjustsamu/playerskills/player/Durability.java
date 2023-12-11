package me.itzjustsamu.playerskills.player;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.function.Function;

public class Durability implements IDurabilityItem {

    private final ItemStack item;

    public Durability(ItemStack item) {
        this.item = item;
    }

    @Override
    public ItemStack getItem() {
        return this.item;
    }

    @Override
    public boolean hasDurability() {
        if (this.item.getType().getMaxDurability() == 0) {
            return false;  // Item has no durability
        }
        Object result = NBT.get(this.item, (Function<ReadableItemNBT, Object>) nbt -> nbt.hasTag(Constants.COMPOUND));

        return result instanceof Boolean && (Boolean) result;
    }

    @Override
    public int getDurability() {
        return NBT.get(this.item, (Function<ReadableItemNBT, Integer>) nbt ->
                Objects.requireNonNull(nbt.getCompound(Constants.COMPOUND)).getInteger(Constants.DURABILITY));
    }

    @Override
    public int getMaxDurability() {
        return NBT.get(this.item, (Function<ReadableItemNBT, Integer>) nbt ->
                Objects.requireNonNull(nbt.getCompound(Constants.COMPOUND)).getInteger(Constants.MAX_DURABILITY));
    }

    @Override
    public void setDurability(int durability) {
        if (this.hasDurability()) {
            int max, cur;
            max = this.getMaxDurability();
            cur = Math.min(durability, max);

            final int itemMaxDurability = this.item.getType().getMaxDurability();

            if (cur <= 0 && this.item.getType() == Material.TRIDENT && itemMaxDurability != 249) {
                NBT.modify(this.item, nbt -> {
                    Objects.requireNonNull(nbt.getCompound(Constants.COMPOUND)).setInteger(Constants.DURABILITY, 1);
                    nbt.setInteger(Constants.DAMAGE, itemMaxDurability - 1);
                });
                return;
            }

            NBT.modify(this.item, nbt -> {
                Objects.requireNonNull(nbt.getCompound(Constants.COMPOUND)).setInteger(Constants.DURABILITY, cur);
                nbt.setInteger(Constants.DAMAGE, itemMaxDurability - (itemMaxDurability * cur / max));
            });

            if (cur <= 0) this.item.setAmount(0);
        }
    }

    @Override
    public void createDurability(int maxDurability, int durability) {
        try {
            if (this.item.getType().getMaxDurability() == 0) {
                throw new IllegalAccessException("Durability cannot be applied to this item.");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        int max = Math.max(durability, maxDurability);
        int cur = Math.max(0, Math.min(durability, max));

        final int itemMaxDurability = this.item.getType().getMaxDurability();
        NBT.modify(this.item, nbt -> {
            ReadWriteNBT compound = nbt.getOrCreateCompound(Constants.COMPOUND);
            compound.setInteger(Constants.DURABILITY, cur);
            compound.setInteger(Constants.MAX_DURABILITY, max);
            nbt.setInteger(Constants.DAMAGE, itemMaxDurability - (itemMaxDurability * cur / max));
        });
    }

    @Override
    public void damageItem(int amount) {
        int damageAmount = this.getDurability();
        damageAmount -= amount;
        this.setDurability(damageAmount);
    }

    @Override
    public void repairItem(int amount) {
        int repairAmount = this.getDurability();
        repairAmount += amount;
        this.setDurability(repairAmount);
    }

    @Override
    public void updateLoreDurability(Player player) {
        if (item.getType() != Material.AIR) {
            new playerActionBar(player).sendActionBar(this.getDurability(), this.getMaxDurability());
        }
    }
}
