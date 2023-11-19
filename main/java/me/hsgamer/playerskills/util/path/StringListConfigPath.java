package me.hsgamer.playerskills.util.path;

import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.path.BaseConfigPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StringListConfigPath extends BaseConfigPath<List<String>> {
    public StringListConfigPath(@NotNull String path, @Nullable List<String> def) {
        super(path, def, o -> CollectionUtils.createStringListFromObject(o, false));
    }
}
