package me.cable.crossover.pets.instance;

import me.cable.crossover.main.util.NumberUtils;
import me.cable.crossover.pets.handler.PetsConfigHandler;
import me.cable.crossover.pets.movement.Movement;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class EquippedPet {

    private final @NotNull String petId;
    private final @NotNull ArmorStand armorStand;
    private final List<Movement> movements = new ArrayList<>();
    private @Nullable Movement currentMovement;
    private int movementCooldown;

    public EquippedPet(@NotNull String petId, @NotNull ArmorStand armorStand) {
        this.petId = petId;
        this.armorStand = armorStand;
    }

    public @NotNull String getPetId() {
        return petId;
    }

    public @NotNull ArmorStand getArmorStand() {
        return armorStand;
    }

    public void addMovement(@NotNull Movement movement) {
        movements.add(movement);
    }

    public @NotNull List<Movement> getMovements() {
        return List.copyOf(movements);
    }

    public @Nullable Movement getCurrentMovement() {
        return currentMovement;
    }

    public void setCurrentMovement(@Nullable Movement currentMovement) {
        this.currentMovement = currentMovement;
        resetMovementCooldown();
    }

    private void resetMovementCooldown() {
        String[] movementCooldowns = PetsConfigHandler.getConfig().snn(petId + ".movement.cooldown").split(",");

        try {
            int min = Integer.parseInt(movementCooldowns[0]);
            int max = Integer.parseInt(movementCooldowns[1]);
            movementCooldown = NumberUtils.random(min, max);
        } catch (NumberFormatException ex) {
            movementCooldown = 20;
        }
    }

    public boolean isMovementReady() {
        if (movementCooldown > 0) movementCooldown--;
        return movementCooldown <= 0;
    }
}
