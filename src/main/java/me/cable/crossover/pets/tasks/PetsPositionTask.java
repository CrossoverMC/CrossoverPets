package me.cable.crossover.pets.tasks;

import me.cable.crossover.pets.handler.PlayerHandler;
import me.cable.crossover.pets.handler.SettingsHandler;
import me.cable.crossover.pets.instance.EquippedPet;
import me.cable.crossover.pets.instance.PetsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PetsPositionTask implements Runnable {

    private @NotNull Location getPetLocation(@NotNull Location origin, int angle, double radius) {
        float playerAngle = -origin.getYaw();
        if (playerAngle < 0) playerAngle = 360 + playerAngle;

        double rad = Math.toRadians(angle + playerAngle);
        double x = Math.sin(rad) * radius;
        double z = Math.cos(rad) * radius;
        return origin.add(x, 0, z);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Location playerLoc = player.getLocation();
            PetsPlayer petsPlayer = PlayerHandler.getPlayer(player);
            List<EquippedPet> equippedPets = petsPlayer.getEquippedPets();
            int totalEquippedPets = equippedPets.size();

            List<Integer> angles = SettingsHandler.getConfig().intList("pet-positions.angles." + totalEquippedPets);
            double radius = SettingsHandler.getConfig().doub("pet-positions.distance");

            for (int i = 0; i < totalEquippedPets; i++) {
                int angle = (angles.size() > i) ? angles.get(i) : 0;
                ArmorStand armorStand = equippedPets.get(i).armorStand();
                Location loc = getPetLocation(playerLoc.clone(), angle, radius);
                armorStand.teleport(loc);
            }
        }
    }
}
