package me.itzjustsamu.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.Utils;
import me.itzjustsamu.playerskills.util.modifier.XMaterialModifier;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityBreedEvent;

import java.util.List;

public class BreederSkill extends Skill {
    private final ConfigPath<Integer> SPAWN_CHANCE_INCREASE = Paths.integerPath("spawn-chance-increase", 25);
    private final ConfigPath<Integer> MAX_SPAWN_AMOUNT = Paths.integerPath("max-spawn-amount", 5);

    public BreederSkill(PlayerSkills plugin) {
        super(plugin, "Breeder", "breeder", 25, 1,0);
    }

    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) {
        Entity mother = event.getMother();
        Entity father = event.getFather();

        if (mother instanceof Animals && father instanceof Animals) {
            Animals animalMother = (Animals) mother;
            World world = animalMother.getWorld();

            if (Worlds_Restriction((Player) world)) {
                return;
            }

            SPlayer sPlayer = SPlayer.get(animalMother.getUniqueId());

            if (sPlayer == null) {
                if (MainConfig.isVerboseLogging()) {
                    Utils.logError("Failed event. SPlayer for " + animalMother.getUniqueId() + " is null.");
                }
                return;
            }

            if (getLevel(sPlayer) > 0) {

                // Increase the chances of spawning multiple animals
                handleBreeding(event, SPAWN_CHANCE_INCREASE.getValue());
            }
        }
    }

    private void handleBreeding(EntityBreedEvent event, int spawnChanceIncrease) {
        // Apply the spawn chance increase
        if (Math.random() * 100 < spawnChanceIncrease) {
            // Get the entity involved in breeding
            Animals animalMother = (Animals) event.getMother();

            // Increase the chances of spawning multiple animals
            int maxSpawnAmount = MAX_SPAWN_AMOUNT.getValue();

            // Spawn multiple animals
            int spawnAmount = 1 + (int) (Math.random() * maxSpawnAmount); // Spawn between 1 and max spawn amount
            World world = animalMother.getWorld();
            for (int i = 0; i < spawnAmount; i++) {
                event.setExperience(0); // Reset experience to prevent default breeding behavior
                event.setCancelled(true); // Cancel the original breeding event
                world.spawn(event.getEntity().getLocation().clone().add(0, 1, 0), animalMother.getClass()); // Spawn a new animal at the location of the breeding animal
            }
        }
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return List.of(SPAWN_CHANCE_INCREASE, MAX_SPAWN_AMOUNT);
    }

    @Override
    public ItemBuilder getDefaultItem() {
        return new ItemBuilder()
                .addItemModifier(new NameModifier().setName("&6Breeder Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.RED_BED))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skillprice} &7point(s).",
                        "&7This skill increases the chances of spawning up to a maximum of 5 animals at once",
                        "&7during the breeding process.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&6Spawn Chance Increase: ",
                        "   &7+{prev}% &7 >>> &7+{next}%",
                        "&6Max Spawn Amount: ",
                        "   &7{prev} &7 >>> &7{next}"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int prevSpawnChanceIncrease = SPAWN_CHANCE_INCREASE.getValue();
        int prevMaxSpawnAmount = MAX_SPAWN_AMOUNT.getValue();
        return String.format("+%s%% / %s", prevSpawnChanceIncrease, prevMaxSpawnAmount);
    }

    @Override
    public String getNextString(SPlayer player) {
        int playerLevel = getLevel(player) + 1;
        int nextSpawnChanceIncrease = playerLevel * SPAWN_CHANCE_INCREASE.getValue();
        int nextMaxSpawnAmount = playerLevel * MAX_SPAWN_AMOUNT.getValue();
        return String.format("+%s%% / %s", nextSpawnChanceIncrease, nextMaxSpawnAmount);
    }
}