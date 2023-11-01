package me.hsgamer.playerskills2.util.path;

import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.AmountModifier;
import me.hsgamer.hscore.bukkit.item.modifier.ItemFlagModifier;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.path.AdvancedConfigPath;
import me.hsgamer.playerskills2.util.CommonStringReplacer;
import me.hsgamer.playerskills2.util.modifier.XMaterialModifier;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ItemBuilderConfigPath extends AdvancedConfigPath<Map<String, Object>, ItemBuilder> {
    public ItemBuilderConfigPath(@NotNull String path, @Nullable ItemBuilder def) {
        super(path, def != null ? addDefault(def) : null);
    }

    public static ItemBuilder addDefault(ItemBuilder builder) {
        return builder
                .addItemModifier(new ItemFlagModifier().setFlag(ItemFlag.values()))
                .addStringReplacer("colorize", CommonStringReplacer.COLORIZE)
                .addStringReplacer("player-properties", CommonStringReplacer.PLAYER_PROPERTIES);
    }

    @Override
    public @Nullable Map<String, Object> getFromConfig(@NotNull Config config) {
        return Optional.of(config.getNormalizedValues(getPath(), false))
                .filter(map -> !map.isEmpty())
                .orElse(null);
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
        if (rawValue.containsKey("lore")) {
            itemBuilder.addItemModifier(new LoreModifier().setLore(CollectionUtils.createStringListFromObject(rawValue.get("lore"), false)));
        }
        return addDefault(itemBuilder);
    }

    @Override
    public @Nullable Map<String, Object> convertToRaw(@NotNull ItemBuilder value) {
        Map<String, Object> map = new LinkedHashMap<>();
        value.getItemModifiers().forEach(modifier -> map.put(modifier.getName(), modifier.toObject()));
        return map;
    }
}
