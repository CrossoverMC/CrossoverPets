package me.cable.crossover.pets.movement;

import me.cable.crossover.pets.CrossoverPets;
import me.cable.crossover.pets.instance.EquippedPet;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

public class JumpMovement extends Movement {

    private BukkitTask task;

    public JumpMovement(@NotNull EquippedPet equippedPet, @NotNull Player player) {
        super(equippedPet, player);
    }

    @Override
    public @NotNull String id() {
        return "jump";
    }

    private double calcHeight(int time, int duration, double maxHeight) {
        double a = -4 * maxHeight / (duration * duration);
        double p = duration / 2.0;
        return a * NumberConversions.square(time - p) + maxHeight;
    }

    @Override
    public void start() {
        Location originalLoc = armorStand.getLocation();
        double height = getSettings().doub("height");
        int duration = getSettings().integer("duration");

        task = new BukkitRunnable() {

            int time;

            @Override
            public void run() {
                time++;
                double currentHeight = calcHeight(time, duration, height);
                armorStand.teleport(originalLoc.clone().add(0, currentHeight, 0));

                if (time >= duration) {
                    finish();
                }
            }
        }.runTaskTimer(CrossoverPets.getInstance(), 0, 1);
    }

    @Override
    public void stop() {
        task.cancel();
        task = null;
    }
}
