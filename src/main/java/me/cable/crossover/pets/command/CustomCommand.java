package me.cable.crossover.pets.command;

import me.cable.crossover.pets.CrossoverPets;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public abstract class CustomCommand implements TabExecutor {

    protected final CrossoverPets crossoverPets;

    public CustomCommand(@NotNull CrossoverPets crossoverPets) {
        this.crossoverPets = crossoverPets;
    }

    public void register(@NotNull String label) {
        PluginCommand pluginCommand = crossoverPets.getCommand(label);

        if (pluginCommand == null) {
            throw new IllegalStateException("Invalid command: " + label);
        }

        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(this);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return Collections.emptyList();
    }
}
