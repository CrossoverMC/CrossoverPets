package me.cable.crossover.pets.menu;

import me.cable.crossover.main.handler.PlayerData;
import me.cable.crossover.main.menu.MainMenu;
import me.cable.crossover.main.menu.PagedMenu;
import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.util.ItemBuilder;
import me.cable.crossover.pets.handler.PetsConfigHandler;
import me.cable.crossover.pets.handler.PlayerHandler;
import me.cable.crossover.pets.handler.SettingsConfigHandler;
import me.cable.crossover.pets.instance.PetsPlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PetsMenu extends PagedMenu {

    private static final String CONFIG_PATH = "pets-menu";

    public PetsMenu(@NotNull Player player, boolean showBack) {
        super(player);

        handleCustomItems(SettingsConfigHandler.getConfig().csnn(CONFIG_PATH + ".items.custom"));

        render(inv -> {
            if (showBack) {
                new ItemBuilder().config(SettingsConfigHandler.getConfig().csnn(CONFIG_PATH + ".items.back"))
                        .pd(itemKey, "BACK")
                        .place(inv);
            }
        });

        onClick((e, tag) -> {
            if (e.getClick() != ClickType.LEFT || tag == null) return;

            if (tag.equals("BACK")) {
                new MainMenu(player).open();
            } else if (tag.startsWith("PET_")) {
                String petId = tag.substring("PET_".length());
                PetsPlayer petsPlayer = PlayerHandler.getPlayer(player);

                if (petsPlayer.hasPetEquipped(petId)) {
                    SettingsConfigHandler.getConfig().message("messages.pet-already-equipped").send(player);
                    return;
                }
                if (petsPlayer.getTotalEquipped() >= petsPlayer.getMaxEquipped()) {
                    SettingsConfigHandler.getConfig().message("messages.max-equipped-pets").send(player);
                    return;
                }

                petsPlayer.equipPet(petId);
            }
        });
    }

    @Override
    protected @NotNull String title() {
        return SettingsConfigHandler.getConfig().snn(CONFIG_PATH + ".title");
    }

    @Override
    protected int rows() {
        return SettingsConfigHandler.getConfig().integer(CONFIG_PATH + ".rows");
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
                    .config(SettingsConfigHandler.getConfig().csnn(CONFIG_PATH + ".items.pet"))
                    .hdb(petConfig.integer("hdb"))
                    .placeholder("name", petConfig.snn("name"))
                    .lorePlaceholder("description", PetsConfigHandler.getPetDescription(petId))
                    .pd(itemKey, "PET_" + petId)
                    .create();
            items.add(item);
        }

        return items;
    }

    @Override
    protected @NotNull List<Integer> itemSlots() {
        return SettingsConfigHandler.getConfig().intList(CONFIG_PATH + ".pet-slots");
    }

    @Override
    protected @NotNull ItemBuilder nextItem() {
        return new ItemBuilder().config(SettingsConfigHandler.getConfig().csnn(CONFIG_PATH + ".items.next"));
    }

    @Override
    protected @NotNull ItemBuilder previousItem() {
        return new ItemBuilder().config(SettingsConfigHandler.getConfig().csnn(CONFIG_PATH + ".items.previous"));
    }
}
