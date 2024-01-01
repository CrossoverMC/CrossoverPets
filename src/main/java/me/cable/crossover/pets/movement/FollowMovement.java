package me.cable.crossover.pets.movement;

import me.cable.crossover.pets.CrossoverPets;
import me.cable.crossover.pets.instance.EquippedPet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class FollowMovement extends Movement {

    private int taskId;

    public FollowMovement(@NotNull EquippedPet equippedPet, @NotNull Player player) {
        super(equippedPet, player);
    }

    @Override
    public @NotNull String id() {
        return "follow";
    }

    @Override
    public boolean override() {
        double distance = getSettings().doub("trigger-distance");
        return player.getLocation().distanceSquared(equippedPet.getArmorStand().getLocation()) > distance * distance;
    }

    private double extraSpeed() {
        double distance = player.getLocation().distance(armorStand.getLocation());
        return Math.min(Math.pow(1.013, Math.max(distance - 10, 0)) - 1, 2);
    }

    @Override
    public void start() {
        double angle = Math.random() * Math.PI * 2;
        double radius = getSettings().doub("distance");
        double x = Math.sin(angle) * radius;
        double z = Math.cos(angle) * radius;

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(CrossoverPets.getInstance(), () -> {
            Location from = armorStand.getLocation();
            Location to = getGroundLocation(player.getLocation().add(x, 0, z));
            Vector dir = to.clone().subtract(from).toVector().normalize();
            float yaw = (float) Math.toDegrees(Math.atan2(dir.getZ(), dir.getX())) - 90;

            from.add(dir.multiply(getSettings().doub("speed") + extraSpeed()));

            if (from.distanceSquared(to) > GOAL_RANGE * GOAL_RANGE) {
                from.setYaw(yaw);
                armorStand.teleport(from);
            } else {
                to.setYaw(yaw);
                armorStand.teleport(to);
                finish();
            }
        }, 0, 1);
    }

    @Override
    public void stop() {
        Bukkit.getScheduler().cancelTask(taskId);
    }
}
