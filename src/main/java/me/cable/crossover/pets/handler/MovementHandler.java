package me.cable.crossover.pets.handler;

import me.cable.crossover.pets.CrossoverPets;
import me.cable.crossover.pets.instance.EquippedPet;
import me.cable.crossover.pets.instance.PetsPlayer;
import me.cable.crossover.pets.movement.Movement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MovementHandler {

    public MovementHandler(@NotNull CrossoverPets crossoverPets) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(crossoverPets, this::tick, 0, 1);
    }

    private void startMovement(@NotNull EquippedPet equippedPet, @NotNull Movement movement) {
        equippedPet.setCurrentMovement(movement);
        movement.start();
    }

    private void equippedPetTick(@NotNull EquippedPet equippedPet) {
        List<Movement> allMovements = equippedPet.getMovements();
        Movement currentMovement = equippedPet.getCurrentMovement();

        for (Movement m : allMovements) {
            if (m.override() && (currentMovement == null || currentMovement.getClass() != m.getClass())) {
                if (currentMovement != null) currentMovement.stop();
                startMovement(equippedPet, m);
                return;
            }
        }

        if (currentMovement == null && equippedPet.isMovementReady()) {
            List<Movement> availableMovements = allMovements.stream().filter(a -> !a.strictlyOverride()).toList();

            if (!availableMovements.isEmpty()) {
                Movement movement = availableMovements.get((int) (Math.random() * availableMovements.size()));
                startMovement(equippedPet, movement);
            }
        }
    }

    private void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PetsPlayer petsPlayer = PlayerHandler.getPlayer(player);

            for (EquippedPet equippedPet : petsPlayer.getEquippedPets()) {
                equippedPetTick(equippedPet);
            }
        }
    }
}
