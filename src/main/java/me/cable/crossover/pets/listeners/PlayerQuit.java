package me.cable.crossover.pets.listeners;

import me.cable.crossover.pets.handler.PlayerHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerQuit implements Listener {

    @EventHandler
    public void event(@NotNull PlayerQuitEvent e) {
        Player player = e.getPlayer();
        PlayerHandler.removePlayer(player);
    }
}
