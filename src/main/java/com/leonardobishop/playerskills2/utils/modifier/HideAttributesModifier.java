package com.leonardobishop.playerskills2.utils.modifier;

import me.hsgamer.hscore.bukkit.item.ItemMetaModifier;
import me.hsgamer.hscore.common.interfaces.StringReplacer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.UUID;

public class HideAttributesModifier extends ItemMetaModifier {
    private boolean hideAttributes = true;

    @Override
    public ItemMeta modifyMeta(ItemMeta meta, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
        if (hideAttributes) {
            meta.addItemFlags(ItemFlag.values());
        }
        return meta;
    }

    @Override
    public void loadFromItemMeta(ItemMeta meta) {
        hideAttributes = meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES);
    }

    @Override
    public boolean canLoadFromItemMeta(ItemMeta meta) {
        return true;
    }

    @Override
    public boolean compareWithItemMeta(ItemMeta meta, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
        return false;
    }

    @Override
    public String getName() {
        return "hide";
    }

    @Override
    public Object toObject() {
        return hideAttributes;
    }

    @Override
    public void loadFromObject(Object object) {
        this.hideAttributes = Boolean.parseBoolean(String.valueOf(object));
    }
}
