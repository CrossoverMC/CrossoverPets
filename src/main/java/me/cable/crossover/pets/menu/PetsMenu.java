package me.cable.crossover.pets.menu;

import me.cable.crossover.main.handler.PlayerData;
import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.util.ItemBuilder;
import me.cable.crossover.pets.handler.PetsConfigHandler;
import me.cable.crossover.pets.handler.PlayerHandler;
import me.cable.crossover.pets.handler.SettingsHandler;
import me.cable.crossover.pets.instance.PetsPlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PetsMenu extends PagedMenu {

    public PetsMenu(@NotNull Player player) {
        super(player);

        onClick((e, tag) -> {
            if (tag != null && tag.startsWith("PET_")) {
                String petId = tag.substring("PET_".length());

                PetsPlayer petsPlayer = PlayerHandler.getPlayer(player);

                if (petsPlayer.hasPetEquipped(petId)) {
                    SettingsHandler.getConfig().message("messages.pet-already-equipped").send(player);
                    return;
                }
                if (petsPlayer.getTotalEquipped() >= petsPlayer.getMaxEquipped()) {
                    SettingsHandler.getConfig().message("messages.max-equipped-pets").send(player);
                    return;
                }

                petsPlayer.equipPet(petId);
            }
        });
    }

    @Override
    protected @NotNull String title() {
        return SettingsHandler.getConfig().snn("pets-menu.title");
    }

    @Override
    protected int rows() {
        return SettingsHandler.getConfig().integer("pets-menu.rows");
    }

    @Override
    protected @NotNull List<ItemStack> items() {
        List<ItemStack> items = new ArrayList<>();

        YamlConfiguration playerData = PlayerData.get(player.getUniqueId());
        List<String> pets = playerData.getStringList(PlayerHandler.PLAYER_DATA_PATH_PETS_INVENTORY);

        for (String petId : PetsConfigHandler.getPetIds()) {
            if (!pets.contains(petId)) continue; // does not have pet

            ConfigHelper petConfig = PetsConfigHandler.getConfig().ch(petId);
            ItemStack item = new ItemBuilder()
                    .config(SettingsHandler.getConfig().csnn("pets-menu.items.pet"))
                    .hdb(petConfig.integer("hdb"))
                    .placeholder("name", petConfig.snn("name"))
                    .lorePlaceholder("description", PetsConfigHandler.getPetDescription(petId))
                    .create();
            tag(item, "PET_" + petId);
            items.add(item);
        }

        return items;
    }

    @Override
    protected @NotNull List<Integer> itemSlots() {
        return SettingsHandler.getConfig().intList("pets-menu.pet-slots");
    }

    @Override
    protected @NotNull ItemBuilder nextItem() {
        return new ItemBuilder().config(SettingsHandler.getConfig().csnn("pets-menu.items.next"));
    }

    @Override
    protected @NotNull ItemBuilder previousItem() {
        return new ItemBuilder().config(SettingsHandler.getConfig().csnn("pets-menu.items.previous"));
    }
}
