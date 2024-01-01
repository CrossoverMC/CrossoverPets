package me.cable.crossover.pets.movement;

import me.cable.crossover.main.util.NumberUtils;
import me.cable.crossover.pets.CrossoverPets;
import me.cable.crossover.pets.instance.EquippedPet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class WalkMovement extends Movement {

    private int taskId;

    public WalkMovement(@NotNull EquippedPet equippedPet, @NotNull Player player) {
        super(equippedPet, player);
    }

    @Override
    public @NotNull String id() {
        return "walk";
    }

    private @NotNull Location selectLoc() {
        double angle = Math.random() * Math.PI * 2;
        double radius = NumberUtils.random(getSettings().doub("radius-min"), getSettings().doub("radius-max"));
        double x = Math.sin(angle) * radius;
        double z = Math.cos(angle) * radius;
        return getGroundLocation(player.getLocation().add(x, 0, z));
    }

    @Override
    public void start() {
        Location targetLoc = selectLoc();

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(CrossoverPets.getInstance(), () -> {
            Location from = armorStand.getLocation();
            Vector dir = targetLoc.clone().subtract(from).toVector().normalize();
            float yaw = (float) Math.toDegrees(Math.atan2(dir.getZ(), dir.getX())) - 90;

            from.add(dir.multiply(getSettings().doub("speed")));

            if (from.distanceSquared(targetLoc) > GOAL_RANGE * GOAL_RANGE) {
                from.setYaw(yaw);
                armorStand.teleport(from);
            } else {
                targetLoc.setYaw(yaw);
                armorStand.teleport(targetLoc);
                finish();
            }
        }, 0, 1);
    }

    @Override
    public void stop() {
        Bukkit.getScheduler().cancelTask(taskId);
    }
}
