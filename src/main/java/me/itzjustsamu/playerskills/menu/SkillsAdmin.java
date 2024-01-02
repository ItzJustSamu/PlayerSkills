package me.itzjustsamu.playerskills.menu;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.skill.Skill;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class SkillsAdmin implements Menu {

    private final PlayerSkills plugin;
    private final Player player;
    private final Skill skill;
    private final Menu superMenu;
    private final SPlayer sPlayer;

    private final String titleEditPermission = "playerskills.edit.title";
    private final Map<UUID, Consumer<String>> textInputCallbacks = new HashMap<>();

    public SkillsAdmin(PlayerSkills plugin, Player player, Skill skill, Menu superMenu, SPlayer sPlayer) {
        this.plugin = plugin;
        this.player = player;
        this.skill = skill;
        this.superMenu = superMenu;
        this.sPlayer = sPlayer;
    }

    @Override
    public @NotNull Inventory getInventory() {
        String title = ColorUtils.colorize(MainConfig.GUI_TITLE.getValue());
        int size = MainConfig.GUI_SIZE.getValue();
        Inventory inventory = Bukkit.createInventory(this, size, title);

        if (MainConfig.GUI_BACKGROUND_ENABLED.getValue()) {
            ItemStack background = MainConfig.GUI_BACKGROUND_DISPLAY.getValue().build(player);
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, background);
            }
        }

        if (skill != null) {
            inventory.setItem(MainConfig.GUI_EDIT_TITLE_SLOT.getValue(), MainConfig.GUI_EDIT_TITLE_DISPLAY.getValue().build(this.player));
        }

        return inventory;
    }

    @Override
    public void onClick(int slot, ClickType event) {
        if (slot == MainConfig.GUI_BACK_SLOT.getValue()) {
            // Open SkillsMenu when clicking the GUI_BACK_SLOT
            SkillsMenu skillsMenu = new SkillsMenu(this.plugin, this.player, this.sPlayer);
            skillsMenu.open(this.player);
        } else if (slot == MainConfig.GUI_EDIT_TITLE_SLOT.getValue()) {
            // Handle title edit event
            if (event == ClickType.LEFT && player.hasPermission(titleEditPermission)) {
                // Send a message to the player to enter a new title
                player.sendMessage(ChatColor.YELLOW + "Please enter a new title:");

                // Open a menu for title editing
                openTextInputMenu((newTitle) -> {
                    if (newTitle != null && !newTitle.isEmpty()) {
                        // Modify the skill title
                        ItemMeta itemMeta = skill.getDisplayItem(player).getItemMeta();
                        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', newTitle));
                        skill.getDisplayItem(player).setItemMeta(itemMeta);

                        // Save the modified skill to your data structure or configuration
                        // You might want to update your data structure or configuration here
                        // Display a message to the player indicating that the title has been updated
                        player.sendMessage(ChatColor.GREEN + "Skill title updated to: " + ChatColor.YELLOW + newTitle);
                    } else {
                        player.sendMessage(ChatColor.RED + "Invalid title. Please enter a valid title.");
                    }

                    // Open the SkillsSettings menu again
                    open(player);
                });
            }
        }
    }

    private void openTextInputMenu(Consumer<String> callback) {
        // You can create a simple text input menu directly here or use your existing library
        // For simplicity, let's assume you have a method for opening a text input menu
        // For example purposes, let's create a simple text input menu here

        player.closeInventory(); // Close any existing inventory

        // Create a new inventory with a text input prompt
        Inventory textInputInventory = Bukkit.createInventory(null, 9, "Enter new title");

        // Add an item to represent the text input
        assert XMaterial.PAPER.parseMaterial() != null;
        ItemStack textInputItem = new ItemStack(XMaterial.PAPER.parseMaterial());
        ItemMeta textInputItemMeta = textInputItem.getItemMeta();
        assert textInputItemMeta != null;
        textInputItemMeta.setDisplayName(ChatColor.YELLOW + "Enter");
        textInputItem.setItemMeta(textInputItemMeta);
        textInputInventory.setItem(4, textInputItem);

        // Open the text input menu
        player.openInventory(textInputInventory);

        // Store the callback for later use
        textInputCallbacks.put(player.getUniqueId(), callback);
    }

    // You need to call this method when you receive a player's input
    public void handleTextInput(UUID playerId, String input) {
        if (textInputCallbacks.containsKey(playerId)) {
            // Get the callback associated with the player
            Consumer<String> callback = textInputCallbacks.get(playerId);

            // Remove the callback from the map to prevent memory leaks
            textInputCallbacks.remove(playerId);

            // Execute the callback with the player's input
            callback.accept(input);
        }
    }

    @NotNull
    private Runnable getRunnable() {
        int price = this.sPlayer.getNextPointPrice();
        return () -> {
            if (MainConfig.POINTS_FUNDING_SOURCE.getValue().doTransaction(this.sPlayer, price, this.player)) {
                this.sPlayer.setPoints(this.sPlayer.getPoints() + 1);
                XSound.UI_BUTTON_CLICK.play(this.player, 1.0F, 1.0F);
                this.open(this.player);
            } else {
                XSound.ENTITY_ITEM_BREAK.play(this.player, 1.0F, 0.6F);
            }
        };
    }
}
