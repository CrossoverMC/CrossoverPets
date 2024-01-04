package me.cable.crossover.pets.movement;

import me.cable.crossover.pets.CrossoverPets;
import me.cable.crossover.pets.instance.EquippedPet;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class OrbitMovement extends Movement {

    private BukkitTask task;

    public OrbitMovement(@NotNull EquippedPet equippedPet, @NotNull Player player) {
        super(equippedPet, player);
    }

    @Override
    public @NotNull String id() {
        return "orbit";
    }

    private @NotNull Location calcLoc(double angleDeg, double radius) {
        double rad = Math.toRadians(angleDeg);
        double x = Math.sin(rad) * radius;
        double z = Math.cos(rad) * radius;
        Location loc = player.getLocation().add(x, 1 - getBodyHeight(), z);

        float yaw = (float) angleDeg;
        if (yaw < 0) yaw = 360 + yaw;
        loc.setYaw(180 - yaw);

        return loc;
    }

    @Override
    public void start() {
        int duration = getSettings().integer("duration");
        double radius = getSettings().integer("radius");
        double angleInterval = 360.0 / duration;

        task = new BukkitRunnable() {

            double angle;

            @Override
            public void run() {
                angle += angleInterval;
                if (angle >= 360) angle = 0;

                Location loc = calcLoc(angle, radius);
                armorStand.teleport(loc);
            }
        }.runTaskTimer(CrossoverPets.getInstance(), 0, 1);
    }

    @Override
    public void stop() {
        task.cancel();
        task = null;
    }
}
