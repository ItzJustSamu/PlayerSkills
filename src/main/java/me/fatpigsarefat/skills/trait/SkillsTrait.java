package me.fatpigsarefat.skills.trait;

import me.fatpigsarefat.skills.helper.MessageHelper;
import me.fatpigsarefat.skills.listeners.InventoryClick;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.event.EventHandler;

public class SkillsTrait extends Trait {
    public SkillsTrait() {
        super("playerskills");
    }

    @EventHandler
    public void click(NPCRightClickEvent event) {
        if (event.getNPC().hasTrait(SkillsTrait.class)) {
            MessageHelper messageHelper = new MessageHelper();
            if (event.getClicker().hasPermission("playerskills.npc-use")) {
                InventoryClick.reconstructInventory(event.getClicker(), true);
            } else {
                event.getClicker().sendMessage(messageHelper.getMessage("no_permissions_message", new String[0]));
            }
        }
    }

    public void load(DataKey key) {}

    public void save(DataKey key) {}

    public void onAttach() {}

    public void onDespawn() {}

    public void onSpawn() {}

    public void onRemove() {}
}
