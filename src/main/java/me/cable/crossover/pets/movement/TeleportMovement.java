package me.cable.crossover.pets.movement;

import me.cable.crossover.pets.instance.EquippedPet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TeleportMovement extends Movement {

    private static final double MAX_DISTANCE = 150;

    public TeleportMovement(@NotNull EquippedPet equippedPet, @NotNull Player player) {
        super(equippedPet, player);
    }

    @Override
    public @NotNull String id() {
        throw new UnsupportedOperationException("ID should not need to be used");
    }

    @Override
    public boolean strictlyOverride() {
        return true;
    }

    @Override
    public boolean override() {
        Location armorLoc = armorStand.getLocation();
        Location playerLoc = player.getLocation();
        return !Objects.equals(playerLoc.getWorld(), armorLoc.getWorld())
                || armorLoc.distanceSquared(playerLoc) > MAX_DISTANCE * MAX_DISTANCE;
    }

    @Override
    public void start() {
        Bukkit.broadcastMessage("teleporting: " + equippedPet.getPetId());
        armorStand.teleport(player.getLocation().subtract(0, getBodyHeight(), 0));
        finish();
    }
}
