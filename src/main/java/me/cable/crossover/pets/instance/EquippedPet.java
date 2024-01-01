package me.cable.crossover.pets.instance;

import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

public record EquippedPet(@NotNull String petId, @NotNull ArmorStand armorStand) {}
