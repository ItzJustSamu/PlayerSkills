/*     */ package me.fatpigsarefat.skills.managers;
/*     */

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
/*     */ 
/*     */ public class FileManager {
/*     */   private JavaPlugin plugin;
/*     */   
/*     */   public FileManager(JavaPlugin plugin) {
/*  15 */     this.configs = new HashMap<>();
/*  16 */     this.plugin = plugin;
/*     */   }
/*     */   private HashMap<String, Config> configs;
/*     */   public void AddConfig(String name) {
/*  20 */     if (!this.configs.containsKey(name))
/*  21 */       this.configs.put(name, (new Config(name)).copyDefaults(true).save()); 
/*     */   }
/*     */   
/*     */   public Config getConfig(String name) {
/*  25 */     if (this.configs.containsKey(name)) {
/*  26 */       return this.configs.get(name);
/*     */     }
/*  28 */     return null;
/*     */   }
/*     */   
/*     */   public Config saveConfig(String name, boolean copyDefaults) {
/*  32 */     return getConfig(name).copyDefaults(copyDefaults).save();
/*     */   }
/*     */   
/*     */   public boolean reloadConfig(String name) {
/*  36 */     if (getConfig(name) != null) {
/*  37 */       getConfig(name).reload();
/*  38 */       return true;
/*     */     } 
/*  40 */     return false;
/*     */   }
/*     */   
/*     */   public class Config
/*     */   {
/*     */     private String name;
/*     */     private File file;
/*     */     private YamlConfiguration config;
/*     */     
/*     */     public Config(String name) {
/*  50 */       this.name = name + ".yml";
/*     */     }
/*     */     
/*     */     public Config save() {
/*  54 */       if (this.config == null || this.file == null) {
/*  55 */         return this;
/*     */       }
/*     */       try {
/*  58 */         if (this.config.getConfigurationSection("").getKeys(true).size() != 0) {
/*  59 */           this.config.save(this.file);
/*     */         }
/*     */       }
/*  62 */       catch (IOException ex) {
/*  63 */         ex.printStackTrace();
/*     */       } 
/*  65 */       return this;
/*     */     }
/*     */     
/*     */     public YamlConfiguration get() {
/*  69 */       if (this.config == null) {
/*  70 */         reload();
/*     */       }
/*  72 */       return this.config;
/*     */     }
/*     */     
/*     */     public Config saveDefaultConfig() {
/*  76 */       this.file = new File(FileManager.this.plugin.getDataFolder(), this.name);
/*  77 */       FileManager.this.plugin.saveResource(this.name, false);
/*  78 */       return this;
/*     */     }
/*     */     
/*     */     public Config reload() {
/*  82 */       if (this.file == null) {
/*  83 */         this.file = new File(FileManager.this.plugin.getDataFolder(), this.name);
/*     */       }
/*  85 */       this.config = YamlConfiguration.loadConfiguration(this.file);
/*     */       
/*  87 */       try { Reader defConfigStream = new InputStreamReader(FileManager.this.plugin.getResource(this.name), "UTF8");
/*  88 */         if (defConfigStream != null) {
/*  89 */           YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
/*  90 */           this.config.setDefaults((Configuration)defConfig);
/*     */         }
/*     */          }
/*  93 */       catch (UnsupportedEncodingException unsupportedEncodingException) {  }
/*  94 */       catch (NullPointerException nullPointerException) {}
/*  95 */       return this;
/*     */     }
/*     */     
/*     */     public Config copyDefaults(boolean force) {
/*  99 */       get().options().copyDefaults(force);
/* 100 */       return this;
/*     */     }
/*     */     
/*     */     public Config set(String key, Object value) {
/* 104 */       get().set(key, value);
/* 105 */       return this;
/*     */     }
/*     */     
/*     */     public Object get(String key) {
/* 109 */       return get().get(key);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\jessl\Downloads\PlayerSkills.jar!\me\fatpigsarefat\skills\managers\FileManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */