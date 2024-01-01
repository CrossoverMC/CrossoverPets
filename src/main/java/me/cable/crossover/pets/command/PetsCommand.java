package me.cable.crossover.pets.command;

import me.cable.crossover.pets.CrossoverPets;
import me.cable.crossover.pets.menu.PetsMenu;
import me.cable.crossover.main.util.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PetsCommand extends CustomCommand {

    public PetsCommand(@NotNull CrossoverPets crossoverPets) {
        super(crossoverPets);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Color.ERROR + "Only players may use this command!");
            return true;
        }

        new PetsMenu(player).open();
        return true;
    }
}
