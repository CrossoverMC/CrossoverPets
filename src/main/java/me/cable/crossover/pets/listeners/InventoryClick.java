package me.cable.crossover.pets.listeners;

import me.cable.crossover.main.util.ItemUtils;
import me.cable.crossover.pets.handler.PlayerHandler;
import me.cable.crossover.pets.instance.PetsPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InventoryClick implements Listener {

    @EventHandler
    public void event(@NotNull InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        ItemStack item = e.getCurrentItem();
        String petId = ItemUtils.getStrPd(item, PlayerHandler.PET_SLOT_KEY);

        if (petId != null) {
            PetsPlayer petsPlayer = PlayerHandler.getPlayer(player);
            petsPlayer.unequipPet(petId);
        }
    }
}
