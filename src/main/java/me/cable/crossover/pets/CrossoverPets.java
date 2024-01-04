package me.cable.crossover.pets;

import me.cable.crossover.main.shop.ShopItem;
import me.cable.crossover.pets.command.MainCommand;
import me.cable.crossover.pets.command.PetsCommand;
import me.cable.crossover.pets.handler.MovementHandler;
import me.cable.crossover.pets.handler.PetsConfigHandler;
import me.cable.crossover.pets.handler.PlayerHandler;
import me.cable.crossover.pets.handler.SettingsHandler;
import me.cable.crossover.pets.instance.PetShopItem;
import me.cable.crossover.pets.listeners.InventoryClick;
import me.cable.crossover.pets.listeners.PlayerJoin;
import me.cable.crossover.pets.listeners.PlayerQuit;
import me.cable.crossover.pets.tasks.PetSlotsTask;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class CrossoverPets extends JavaPlugin {

    private SettingsHandler settingsHandler;
    private PetsConfigHandler petsConfigHandler;

    public static @NotNull CrossoverPets getInstance() {
        return JavaPlugin.getPlugin(CrossoverPets.class);
    }

    @Override
    public void onEnable() {
        addPermissions();
        initializeHandlers();
        registerListeners();
        registerCommands();
        startTasks();
        ShopItem.register("pet", PetShopItem::new);
    }

    @Override
    public void onDisable() {
        PlayerHandler.cleanup();
    }

    private void addPermissions() {
        PluginManager pluginManager = getServer().getPluginManager();
        List<String> permissions = List.of(
                "crossoverpets.extrapets"
        );

        for (String permission : permissions) {
            pluginManager.addPermission(new Permission(permission));
        }
    }

    private void initializeHandlers() {
        settingsHandler = new SettingsHandler(this);
        petsConfigHandler = new PetsConfigHandler(this);
        new MovementHandler(this);
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new InventoryClick(), this);
        pluginManager.registerEvents(new PlayerJoin(), this);
        pluginManager.registerEvents(new PlayerQuit(), this);
    }

    private void registerCommands() {
        new MainCommand(this).register("crossoverpets");
        new PetsCommand(this).register("pets");
    }

    private void startTasks() {
        BukkitScheduler bukkitScheduler = getServer().getScheduler();
        bukkitScheduler.scheduleSyncRepeatingTask(this, new PetSlotsTask(), 0, 5 * 20);
    }

    public SettingsHandler getSettingsHandler() {
        return settingsHandler;
    }

    public PetsConfigHandler getPetsConfigHandler() {
        return petsConfigHandler;
    }
}
