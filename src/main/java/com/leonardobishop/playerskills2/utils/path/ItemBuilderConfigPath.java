package com.leonardobishop.playerskills2.utils.path;

import com.leonardobishop.playerskills2.utils.modifier.HideAttributesModifier;
import com.leonardobishop.playerskills2.utils.modifier.XMaterialModifier;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.AmountModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.config.AdvancedConfigPath;
import me.hsgamer.hscore.config.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class ItemBuilderConfigPath extends AdvancedConfigPath<Map<String, Object>, ItemBuilder> {
    public ItemBuilderConfigPath(@NotNull String path, @Nullable ItemBuilder def) {
        super(path, def);
    }

    @Override
    public @Nullable Map<String, Object> getFromConfig(@NotNull Config config) {
        return config.getNormalizedValues(getPath(), false);
    }

    @Override
    public @Nullable ItemBuilder convert(@NotNull Map<String, Object> rawValue) {
        ItemBuilder itemBuilder = new ItemBuilder();
        XMaterialModifier materialModifier = new XMaterialModifier();
        if (rawValue.containsKey("material")) {
            materialModifier.loadFromObject(rawValue.get("material"));
        } else if (rawValue.containsKey("type")) {
            materialModifier.loadFromObject(rawValue.get("type"));
        } else if (rawValue.containsKey("item")) {
            materialModifier.loadFromObject(rawValue.get("item"));
        }
        if (materialModifier.toObject() != null) {
            itemBuilder.addItemModifier(materialModifier);
        }
        if (rawValue.containsKey("amount")) {
            itemBuilder.addItemModifier(new AmountModifier().setAmount(String.valueOf(rawValue.get("amount"))));
        }
        if (rawValue.containsKey("name")) {
            itemBuilder.addItemModifier(new NameModifier().setName(String.valueOf(rawValue.get("name"))));
        }
        itemBuilder.addItemModifier(new HideAttributesModifier());
        itemBuilder.addStringReplacer("colorize", (original, uuid) -> MessageUtils.colorize(original));
        return itemBuilder;
    }

    @Override
    public @Nullable Map<String, Object> convertToRaw(@NotNull ItemBuilder value) {
        Map<String, Object> map = new LinkedHashMap<>();
        value.getItemModifiers().forEach(modifier -> map.put(modifier.getName(), modifier.toObject()));
        return map;
    }
}
