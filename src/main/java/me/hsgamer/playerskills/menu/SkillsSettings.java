package me.hsgamer.playerskills.menu;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.hsgamer.playerskills.PlayerSkills;
import me.hsgamer.playerskills.config.MainConfig;
import me.hsgamer.playerskills.player.SPlayer;
import me.hsgamer.playerskills.skill.Skill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
        int size = Math.max(9, 9 * (int) Math.ceil(plugin.getSkillRegistrar().size() / 9.0));

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
        for (Skill skill : plugin.getSkillRegistrar().values()) {
            if (index++ == slot) {
                return skill;
            }
        }
        return null;
    }

    private void toggleSkill(Skill skill) {
        String skillName = skill.getConfigName();
        if (plugin.getDisabledSkills().containsKey(skillName)) {
            // Enable the skill
            plugin.enableSkill(skillName);
            XSound.BLOCK_NOTE_BLOCK_PLING.play(player, 1, 2);
        } else {
            // Disable the skill
            plugin.disableSkill(skill);
            XSound.BLOCK_NOTE_BLOCK_BASS.play(player, 1, 0.5f);
        }
    }

    private Collection<Skill> getEnabledSkills() {
        List<Skill> enabledSkills = new ArrayList<>();
        for (Skill skill : plugin.getSkillRegistrar().values()) {
            if (!plugin.getDisabledSkills().containsKey(skill.getConfigName())) {
                enabledSkills.add(skill);
            }
        }
        return enabledSkills;
    }

}
