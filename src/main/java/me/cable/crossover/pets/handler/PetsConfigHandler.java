package me.cable.crossover.pets.handler;

import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.util.YamlLoader;
import me.cable.crossover.pets.CrossoverPets;
import me.cable.crossover.pets.instance.EquippedPet;
import me.cable.crossover.pets.instance.PetsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class PetsConfigHandler {

    private final CrossoverPets crossoverPets;

    private final File file;
    private static YamlConfiguration config;

    public PetsConfigHandler(@NotNull CrossoverPets crossoverPets) {
        this.crossoverPets = crossoverPets;
        file = new File(crossoverPets.getDataFolder(), "pets.yml");
        load(null);
    }

    public static @NotNull ConfigHelper getConfig() {
        return new ConfigHelper(config);
    }


    public static @NotNull Collection<String> getPetIds() {
        return getConfig().getKeys(false);
    }

    public static boolean isPetValid(@NotNull String petId) {
        return getConfig().cs(petId) != null;
    }

    public static @NotNull List<String> getPetDescription(@NotNull String petId) {
        List<String> description = getConfig().strList(petId + ".description");
        description.replaceAll(s -> "&7" + s);
        return description;
    }

    public void load(@Nullable Player player) {
        config = new YamlLoader(file).resource(crossoverPets)
                .logger(crossoverPets).player(player).load().config();
        removeInvalidPets();
    }

    private void removeInvalidPets() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PetsPlayer petsPlayer = PlayerHandler.getPlayer(player);

            for (EquippedPet equippedPet : petsPlayer.getEquippedPets()) {
                String id = equippedPet.getPetId();

                if (!isPetValid(id)) {
                    petsPlayer.unequipPet(id);
                }
            }
        }
    }
}
