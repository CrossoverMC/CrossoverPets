package me.cable.crossover.pets.tasks;

import me.cable.crossover.main.util.Utils;
import me.cable.crossover.pets.handler.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PetSlotsTask implements Runnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!Utils.hasBypass(player)) {
                PlayerHandler.getPlayer(player).updateInventoryPetSlots();
            }
        }
    }
}
