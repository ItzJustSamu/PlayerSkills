package com.leonardobishop.playerskills2.menu;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.config.Config;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.skill.Skill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class SkillsMenu implements Menu {

    private final PlayerSkills plugin;
    private final Player player;
    private final SPlayer sPlayer;

    public SkillsMenu(PlayerSkills plugin, Player player, SPlayer sPlayer) {
        this.plugin = plugin;
        this.player = player;
        this.sPlayer = sPlayer;
    }

    @Override
    public Inventory getInventory() {
        String title = Config.get(plugin, "gui.title").getColoredString();
        int size = Config.get(plugin, "gui.size").getInt();

        Inventory inventory = Bukkit.createInventory(this, size, title);

        if (plugin.getConfig().getBoolean("gui.background.enabled")) {
            ItemStack background = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
            assert background != null;
            ItemMeta backgroundm = background.getItemMeta();
            backgroundm.setDisplayName(" ");
            background.setItemMeta(backgroundm);

            ItemStack config;
            if ((config = Config.get(plugin, "gui.background").getItemStack()) != null) {
                background = config;
            }
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, background);
            }
        }

        HashMap<String, String> placeholders = new HashMap<>();
        int price = sPlayer.getNextPointPrice();
        placeholders.put("{price}", Integer.toString(price));
        placeholders.put("{symbol}", plugin.getFundingSource().getSymbol(price));
        placeholders.put("{points}", String.valueOf(sPlayer.getPoints()));

        for (Skill skill : plugin.getSkillRegistrar().values()) {
            inventory.setItem(skill.getGuiSlot(), skill.getDisplayItem(player));
        }

        for (String s : Config.get(plugin, "gui.other").getKeys()) {
            int slot = Config.get(plugin, "gui.other." + s + ".slot").getInt();
            ItemStack is = Config.get(plugin, "gui.other." + s).getItemStack(placeholders);

            inventory.setItem(slot, is);
        }

        return inventory;
    }

    @Override
    public void onClick(int slot) {
        for (Skill skill : plugin.getSkillRegistrar().values()) {
            if (slot == skill.getGuiSlot() && (skill.getLevel(sPlayer) < skill.getMaxLevel())) {
                int price = skill.getPriceOverride(skill.getLevel(sPlayer) + 1);
                if ((sPlayer.getPoints() >= price)) {
                    Runnable callback = () -> {
                        sPlayer.setLevel(skill.getConfigName(), skill.getLevel(sPlayer) + 1);
                        sPlayer.setPoints(sPlayer.getPoints() - price);
                        XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player, 2, 2);
                        this.open(player);
                    };
                    if (Config.get(plugin, "gui-confirmation.enabled.purchase-skills").getBoolean()) {
                        ConfirmationMenu confirmationMenu = new ConfirmationMenu(plugin, player,
                                player.getOpenInventory().getTopInventory().getItem(slot), callback, SkillsMenu.this);
                        confirmationMenu.open(player);
                    } else {
                        callback.run();
                    }
                    return;
                } else {
                    XSound.ENTITY_ITEM_BREAK.play(player, 1, 0.6f);
                }
            }
        }


        if (slot == Config.get(plugin, "gui.other.points.slot").getInt()) {
            int price = sPlayer.getNextPointPrice();
            Runnable callback = () -> {
                if (plugin.getFundingSource().doTransaction(sPlayer, price, player)) {
                    sPlayer.setPoints(sPlayer.getPoints() + 1);
                    XSound.UI_BUTTON_CLICK.play(player, 1, 1);
                    this.open(player);
                } else {
                    XSound.ENTITY_ITEM_BREAK.play(player, 1, 0.6f);
                }
            };
            if (Config.get(plugin, "gui-confirmation.enabled.purchase-skill-points").getBoolean()) {
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(plugin, player,
                        player.getOpenInventory().getTopInventory().getItem(slot), callback, SkillsMenu.this);
                confirmationMenu.open(player);
            } else {
                callback.run();
            }
        } else if (slot == Config.get(plugin, "gui.other.reset.slot").getInt()) {
            Runnable callback = () -> {
                if (sPlayer.getPoints() >= Config.get(plugin, "points.reset-price").getInt()) {
                    sPlayer.setPoints(sPlayer.getPoints() - Config.get(plugin, "points.reset-price").getInt());
                    for (String s : sPlayer.getSkills().keySet()) {
                        for (int i = 1; i <= sPlayer.getLevel(s); i++) {
                            sPlayer.setPoints(sPlayer.getPoints() + plugin.getSkillRegistrar().get(s).getPriceOverride(i));
                        }
                    }
                    sPlayer.getSkills().clear();
                    XSound.ENTITY_GENERIC_EXPLODE.play(player, 1, 1);
                    this.open(player);
                } else {
                    XSound.ENTITY_ITEM_BREAK.play(player, 1, 0.6f);
                }
            };
            if (Config.get(plugin, "gui-confirmation.enabled.reset-skills").getBoolean()) {
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(plugin, player,
                        player.getOpenInventory().getTopInventory().getItem(slot), callback, SkillsMenu.this);
                confirmationMenu.open(player);
            } else {
                callback.run();
            }
        }
    }
}
