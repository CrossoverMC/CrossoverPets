package me.cable.crossover.pets.handler;

import me.cable.crossover.pets.CrossoverPets;
import me.cable.crossover.pets.instance.PetsPlayer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class PlayerHandler {

    public static final String PLAYER_DATA_PATH_PETS_INVENTORY = "pets.inventory";
    public static final String PLAYER_DATA_PATH_PETS_EQUIPPED = "pets.equipped";
    public static final NamespacedKey PET_SLOT_KEY = new NamespacedKey(CrossoverPets.getInstance(), "pet-slot-pet");

    private static final List<PetsPlayer> players = new ArrayList<>();

    private static @Nullable PetsPlayer getPlayerIfExists(@NotNull Player player) {
        for (PetsPlayer petsPlayer : players) {
            if (petsPlayer.getPlayer().equals(player)) {
                return petsPlayer;
            }
        }

        return null;
    }

    public static @NotNull PetsPlayer getPlayer(@NotNull Player player) {
        PetsPlayer petsPlayer = getPlayerIfExists(player);
        if (petsPlayer != null) return petsPlayer;

        petsPlayer = new PetsPlayer(player);
        players.add(petsPlayer);
        return petsPlayer;
    }

    public static void removePlayer(@NotNull Player player) {
        PetsPlayer petsPlayer = getPlayerIfExists(player);

        if (petsPlayer != null) {
            players.remove(petsPlayer);
            petsPlayer.cleanup();
        }
    }

    public static void cleanup() {
        for (PetsPlayer petsPlayer : players) {
            petsPlayer.cleanup();
        }
    }
}
