package me.cable.crossover.pets.handler;

import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.util.YamlLoader;
import me.cable.crossover.pets.CrossoverPets;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SettingsHandler {

    private final CrossoverPets crossoverPets;

    private final File file;
    private static YamlConfiguration config;

    public SettingsHandler(@NotNull CrossoverPets crossoverPets) {
        this.crossoverPets = crossoverPets;
        file = new File(crossoverPets.getDataFolder(), "config.yml");
        load(null);
    }

    public static @NotNull ConfigHelper getConfig() {
        return new ConfigHelper(config);
    }

    public void load(@Nullable Player player) {
        config = new YamlLoader(file).resource(crossoverPets)
                .logger(crossoverPets).player(player).load().config();
    }
}
