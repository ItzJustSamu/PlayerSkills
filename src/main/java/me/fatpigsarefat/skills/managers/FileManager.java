package me.fatpigsarefat.skills.managers;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class FileManager {
    private final JavaPlugin plugin;

    private final HashMap<String, Config> configs;

    public FileManager(JavaPlugin plugin) {
        this.configs = new HashMap<>();
        this.plugin = plugin;
    }

    public void AddConfig(String name) {
        if (!this.configs.containsKey(name))
            this.configs.put(name, (new Config(name)).copyDefaults(true).save());
    }

    public Config getConfig(String name) {
        if (this.configs.containsKey(name))
            return this.configs.get(name);
        return null;
    }

    public Config saveConfig(String name, boolean copyDefaults) {
        return getConfig(name).copyDefaults(copyDefaults).save();
    }

    public boolean reloadConfig(String name) {
        if (getConfig(name) != null) {
            getConfig(name).reload();
            return true;
        }
        return false;
    }

    public class Config {
        private final String name;

        private File file;

        private YamlConfiguration config;

        public Config(String name) {
            this.name = name + ".yml";
        }

        public Config save() {
            if (this.config == null || this.file == null)
                return this;
            try {
                if (this.config.getConfigurationSection("").getKeys(true).size() != 0)
                    this.config.save(this.file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return this;
        }

        public YamlConfiguration get() {
            if (this.config == null)
                reload();
            return this.config;
        }

        public Config saveDefaultConfig() {
            this.file = new File(FileManager.this.plugin.getDataFolder(), this.name);
            FileManager.this.plugin.saveResource(this.name, false);
            return this;
        }

        public void reload() {
            if (this.file == null)
                this.file = new File(FileManager.this.plugin.getDataFolder(), this.name);
            this.config = YamlConfiguration.loadConfiguration(this.file);
            try {
                Reader defConfigStream = new InputStreamReader(FileManager.this.plugin.getResource(this.name), StandardCharsets.UTF_8);
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                this.config.setDefaults(defConfig);
            } catch (NullPointerException ignored) {

            }
        }

        public Config copyDefaults(boolean force) {
            get().options().copyDefaults(force);
            return this;
        }

        public void set(String key, Object value) {
            get().set(key, value);
        }

        public Object get(String key) {
            return get().get(key);
        }
    }
}
