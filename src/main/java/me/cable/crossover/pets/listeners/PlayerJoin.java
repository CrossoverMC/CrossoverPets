package me.cable.crossover.pets.listeners;

import me.cable.crossover.pets.handler.PlayerHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoin implements Listener {

    @EventHandler
    public void event(@NotNull PlayerJoinEvent e) {
        Player player = e.getPlayer();
        PlayerHandler.getPlayer(player); // load
    }
}
