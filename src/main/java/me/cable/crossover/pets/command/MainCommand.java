package me.cable.crossover.pets.command;

import me.cable.crossover.main.util.Color;
import me.cable.crossover.pets.CrossoverPets;
import me.cable.crossover.pets.handler.PetsConfigHandler;
import me.cable.crossover.pets.handler.PlayerHandler;
import me.cable.crossover.pets.handler.SettingsConfigHandler;
import me.cable.crossover.pets.instance.PetsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainCommand extends CustomCommand {

    private final SettingsConfigHandler settingsConfigHandler;
    private final PetsConfigHandler petsConfigHandler;

    public MainCommand(@NotNull CrossoverPets crossoverPets) {
        super(crossoverPets);
        settingsConfigHandler = crossoverPets.getSettingsConfigHandler();
        petsConfigHandler = crossoverPets.getPetsConfigHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            PluginDescriptionFile pdf = crossoverPets.getDescription();
            sender.sendMessage(Color.SUCCESS + "Server is running " + pdf.getName() + " v" + pdf.getVersion() + ".");
            return true;
        }

        switch (args[0]) {
            case "pet" -> {
                String usage = Color.ERROR + "Usage: /" + label + " pet <give|remove> <player> <pet>";

                if (args.length < 4) {
                    sender.sendMessage(usage);
                    return true;
                }

                String targetName = args[2];
                Player target = Bukkit.getPlayer(targetName);

                if (target == null) {
                    sender.sendMessage(Color.ERROR + "The player " + Color.SPECIAL + targetName
                            + Color.ERROR + " does not exist!");
                    return true;
                }

                String petId = args[3];

                if (!PetsConfigHandler.isPetValid(petId)) {
                    sender.sendMessage(Color.ERROR + "The pet " + Color.SPECIAL + petId
                            + Color.ERROR + " is invalid!");
                    return true;
                }

                PetsPlayer petsPlayer = PlayerHandler.getPlayer(target);

                switch (args[1]) {
                    case "give" -> {
                        if (petsPlayer.givePet(petId)) {
                            sender.sendMessage(Color.SUCCESS + "Gave " + Color.SPECIAL + target.getName()
                                    + Color.SUCCESS + " the pet " + Color.SPECIAL + petId + Color.SUCCESS + ".");
                        } else {
                            sender.sendMessage(Color.SPECIAL + target.getName() + Color.ERROR + " already has the pet "
                                    + Color.SPECIAL + petId + Color.ERROR + ".");
                        }
                    }
                    case "remove" -> {
                        if (petsPlayer.removePet(petId)) {
                            sender.sendMessage(Color.SUCCESS + "Removed the pet " + Color.SPECIAL + petId
                                    + Color.SUCCESS + " from " + Color.SPECIAL + target.getName() + Color.SUCCESS + ".");
                        } else {
                            sender.sendMessage(Color.SPECIAL + target.getName() + Color.ERROR + " does not have the pet "
                                    + Color.SPECIAL + petId + Color.ERROR + ".");
                        }
                    }
                    default -> sender.sendMessage(usage);
                }
            }
            case "reload" -> {
                long millis = System.currentTimeMillis();
                Player player = (sender instanceof Player p) ? p : null;

                settingsConfigHandler.load(player);
                petsConfigHandler.load(player);

                sender.sendMessage(Color.SUCCESS + "Configuration reloaded in "
                        + Color.SPECIAL + (System.currentTimeMillis() - millis) + Color.SUCCESS + " ms.");
            }
            default -> sender.sendMessage(Color.ERROR + "Unknown sub-command!");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            for (String s : List.of("pet", "reload")) {
                if (s.startsWith(args[0])) {
                    list.add(s);
                }
            }
        } else if (args[0].equals("pet")) {
            switch (args.length) {
                case 2 -> {
                    for (String s : List.of("give", "remove")) {
                        if (s.startsWith(args[1])) {
                            list.add(s);
                        }
                    }
                }
                case 3 -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        String s = player.getName();

                        if (s.startsWith(args[2])) {
                            list.add(s);
                        }
                    }
                }
                case 4 -> {
                    for (String s : PetsConfigHandler.getPetIds()) {
                        if (s.startsWith(args[3])) {
                            list.add(s);
                        }
                    }
                }
            }
        }

        return list;
    }
}
