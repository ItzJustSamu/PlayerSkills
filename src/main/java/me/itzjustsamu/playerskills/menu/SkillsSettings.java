package me.itzjustsamu.playerskills.menu;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.itzjustsamu.playerskills.skill.Skill;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.player.SPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import static me.itzjustsamu.playerskills.util.Utils.logger;

public class SkillsSettings implements Menu {

    private final PlayerSkills plugin;
    private final Player player;
    private final SPlayer sPlayer;

    public SkillsSettings(PlayerSkills plugin, Player player, SPlayer sPlayer) {
        this.plugin = plugin;
        this.player = player;
        this.sPlayer = sPlayer;
    }

    @Override
    public @NotNull Inventory getInventory() {
        String title = ColorUtils.colorize("&6Skills Settings");
        int size = Math.max(9, 9 * (int) Math.ceil(plugin.getSkills().size() / 9.0));

        Inventory inventory = Bukkit.createInventory(this, size, title);

        int index = 0;
        for (Skill skill : getEnabledSkills()) {
            ItemStack skillItem = createSkillItem(skill);
            inventory.setItem(index++, skillItem);
        }

        return inventory;
    }


    @Override
    public void onClick(int slot) {
        Skill skill = getSkillFromSlot(slot);
        if (skill != null) {
            toggleSkill(skill);
            this.open(player);
        }
    }

    private ItemStack createSkillItem(Skill skill) {
        XMaterial material = plugin.getDisabledSkills().containsKey(skill.getConfigName())
                ? XMaterial.RED_STAINED_GLASS_PANE
                : XMaterial.PAPER;

        ItemStack itemStack = Objects.requireNonNull(material.parseItem());
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ColorUtils.colorize("&a" + skill.getName()));
        itemStack.setItemMeta(meta);

        return itemStack;
    }


    private Skill getSkillFromSlot(int slot) {
        int index = 0;
        for (Skill skill : plugin.getSkills().values()) {
            if (index++ == slot) {
                return skill;
            }
        }
        return null;
    }

    public void toggleSkill(Skill skill) {
        String skillName = skill.getConfigName();
        boolean currentStatus = !plugin.getDisabledSkills().containsKey(skillName);

        // Update the in-memory state of disabled skills
        if (currentStatus) {
            plugin.getDisabledSkills().remove(skillName);
        } else {
            plugin.getDisabledSkills().put(skillName, skill);
        }

        // Persist the changes to the configuration file
        saveChangesToConfig();

        // Play the sound
        XSound.BLOCK_NOTE_BLOCK_PLING.play(player, 1, 2);
    }

    private void saveChangesToConfig() {
        BukkitConfig skillsConfig = new BukkitConfig(plugin, "SkillsSettings.yml");
        skillsConfig.setup();

        ConfigurationSection skillsSection = skillsConfig.getOriginal().getConfigurationSection("skills");
        if (skillsSection != null) {
            for (Map.Entry<String, Skill> entry : plugin.getDisabledSkills().entrySet()) {
                String skillName = entry.getKey();
                boolean isDisabled = skillsSection.getBoolean(skillName + ".enable", false);

                // Save the modified configuration with the new value
                skillsSection.set(skillName + ".enable", isDisabled);
            }

            // Save the changes to the configuration file
            skillsConfig.save();
        }
    }


    private Collection<Skill> getEnabledSkills() {
        List<Skill> enabledSkills = new ArrayList<>();
        for (Skill skill : plugin.getSkills().values()) {
            if (!plugin.getDisabledSkills().containsKey(skill.getConfigName())) {
                enabledSkills.add(skill);
            }
        }
        return enabledSkills;
    }

}
