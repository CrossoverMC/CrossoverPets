package me.cable.crossover.pets;

import me.cable.crossover.main.menu.MainMenu;
import me.cable.crossover.main.shop.ShopItem;
import me.cable.crossover.main.task.InventoryItemsTask;
import me.cable.crossover.pets.command.MainCommand;
import me.cable.crossover.pets.command.PetsCommand;
import me.cable.crossover.pets.handler.MovementHandler;
import me.cable.crossover.pets.handler.PetsConfigHandler;
import me.cable.crossover.pets.handler.PlayerHandler;
import me.cable.crossover.pets.handler.SettingsConfigHandler;
import me.cable.crossover.pets.instance.PetShopItem;
import me.cable.crossover.pets.listeners.InventoryClick;
import me.cable.crossover.pets.listeners.PlayerJoin;
import me.cable.crossover.pets.listeners.PlayerQuit;
import me.cable.crossover.pets.menu.PetsMenu;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class CrossoverPets extends JavaPlugin {

    private SettingsConfigHandler settingsConfigHandler;
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
        ShopItem.register("pet", PetShopItem::new);
        MainMenu.petsMenuFactory = player -> new PetsMenu(player, true);
        InventoryItemsTask.registerPlacer(player -> PlayerHandler.getPlayer(player).updateInventoryPetSlots());
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
        settingsConfigHandler = new SettingsConfigHandler(this);
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

    public SettingsConfigHandler getSettingsConfigHandler() {
        return settingsConfigHandler;
    }

    public PetsConfigHandler getPetsConfigHandler() {
        return petsConfigHandler;
    }
}
