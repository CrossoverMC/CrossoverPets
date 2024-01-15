package me.cable.crossover.pets.movement;

import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.pets.handler.PetsConfigHandler;
import me.cable.crossover.pets.instance.EquippedPet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class Movement {

    public static final double BODY_HEIGHT_BIG = 1.43;
    public static final double BODY_HEIGHT_SMALL = 0.72;
    public static final double GOAL_RANGE = 0.3;

    protected final EquippedPet equippedPet;
    protected final ArmorStand armorStand;
    protected final Player player;

    public Movement(@NotNull EquippedPet equippedPet, @NotNull Player player) {
        this.equippedPet = equippedPet;
        armorStand = equippedPet.getArmorStand();
        this.player = player;
    }

    public static double getBodyHeight(boolean small) {
        return small ? BODY_HEIGHT_SMALL : BODY_HEIGHT_BIG;
    }

    public double getBodyHeight() {
        return getBodyHeight(armorStand.isSmall());
    }

    protected final void finish() {
        stop();
        equippedPet.setCurrentMovement(null);
    }

    public abstract @NotNull String id();

    public boolean strictlyOverride() {
        return false;
    }

    public boolean override() {
        return false;
    }

    public abstract void start();

    public void stop() {}

    protected final @NotNull Location getGroundLocation(@NotNull Location loc) {
        Block groundBlock = loc.getBlock();
        double bodyHeight = getBodyHeight();
        loc = loc.clone();

        if (groundBlock.getType().isAir()) {
            for (int i = 0; i < 10 && groundBlock.getType().isAir(); i++) {
                groundBlock = groundBlock.getRelative(BlockFace.DOWN);
            }

            if (!groundBlock.getType().isAir()) {
                loc.setY(groundBlock.getY() + 1 - bodyHeight);
                return loc;
            }
        } else {
            for (int i = 0; i < 10 && groundBlock.getType() != Material.AIR; i++) {
                groundBlock = groundBlock.getRelative(BlockFace.UP);
            }

            if (groundBlock.getType().isAir()) {
                loc.setY(groundBlock.getY() - bodyHeight);
                return loc;
            }
        }

        loc.setY(loc.getY() - bodyHeight);
        return loc;
    }

    protected final @NotNull ConfigHelper getSettings() {
        return PetsConfigHandler.getConfig().ch(equippedPet.getPetId() + ".movement.types." + id());
    }
}
