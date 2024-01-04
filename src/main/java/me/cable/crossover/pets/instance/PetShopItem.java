package me.cable.crossover.pets.instance;

import me.cable.crossover.main.handler.PlayerData;
import me.cable.crossover.main.shop.ShopItem;
import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.util.ItemBuilder;
import me.cable.crossover.pets.handler.PetsConfigHandler;
import me.cable.crossover.pets.handler.PlayerHandler;
import me.cable.crossover.pets.handler.SettingsHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PetShopItem extends ShopItem {

    public PetShopItem(@NotNull ConfigurationSection config) {
        super(config);
    }

    private @NotNull String petId() {
        return config.snn("pet");
    }

    @Override
    public boolean hasItem(@NotNull Player player) {
        YamlConfiguration playerData = PlayerData.get(player.getUniqueId());
        return playerData.getStringList(PlayerHandler.PLAYER_DATA_PATH_PETS_INVENTORY).contains(petId());
    }

    @Override
    public @NotNull ItemStack getItem(@NotNull Player player) {
        String petId = petId();
        ConfigHelper petConfig = PetsConfigHandler.getConfig().ch(petId);
        return new ItemBuilder()
                .config(SettingsHandler.getConfig().csnn("shop-pet-item"))
                .hdb(petConfig.integer("hdb"))
                .placeholder("name", petConfig.snn("name"))
                .lorePlaceholder("description", PetsConfigHandler.getPetDescription(petId))
                .create();
    }

    @Override
    public void onPurchase(@NotNull Player player) {
        PlayerHandler.getPlayer(player).givePet(petId());
    }
}
