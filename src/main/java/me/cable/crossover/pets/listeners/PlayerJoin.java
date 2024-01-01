package me.cable.crossover.pets.listeners;

import me.cable.crossover.main.util.Utils;
import me.cable.crossover.pets.handler.PlayerHandler;
import me.cable.crossover.pets.instance.PetsPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoin implements Listener {

    @EventHandler
    public void event(@NotNull PlayerQuitEvent e) {
        Player player = e.getPlayer();
        PetsPlayer petsPlayer = PlayerHandler.getPlayer(player); // load

        if (!Utils.hasBypass(player)) {
            petsPlayer.updateInventoryPetSlots();
        }
    }
}
