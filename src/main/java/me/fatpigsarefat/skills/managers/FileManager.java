package me.fatpigsarefat.skills.managers;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class FileManager {
    private JavaPlugin plugin;

    private HashMap<String, Config> configs;

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
        private String name;

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

        public Config reload() {
            if (this.file == null)
                this.file = new File(FileManager.this.plugin.getDataFolder(), this.name);
            this.config = YamlConfiguration.loadConfiguration(this.file);
            try {
                Reader defConfigStream = new InputStreamReader(FileManager.this.plugin.getResource(this.name), "UTF8");
                if (defConfigStream != null) {
                    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                    this.config.setDefaults((Configuration)defConfig);
                }
            } catch (UnsupportedEncodingException unsupportedEncodingException) {

            } catch (NullPointerException nullPointerException) {}
            return this;
        }

        public Config copyDefaults(boolean force) {
            get().options().copyDefaults(force);
            return this;
        }

        public Config set(String key, Object value) {
            get().set(key, value);
            return this;
        }

        public Object get(String key) {
            return get().get(key);
        }
    }
}
