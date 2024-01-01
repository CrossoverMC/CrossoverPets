package me.cable.crossover.pets.instance;

import me.cable.crossover.main.handler.PlayerData;
import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.util.ItemBuilder;
import me.cable.crossover.pets.handler.PetsConfigHandler;
import me.cable.crossover.pets.handler.PlayerHandler;
import me.cable.crossover.pets.handler.SettingsHandler;
import me.cable.crossover.pets.movement.*;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PetsPlayer {

    private final Player player;
    private final List<EquippedPet> equippedPets = new ArrayList<>();

    private static @NotNull List<Integer> getBasicSlots() {
        return SettingsHandler.getConfig().intList("pet-slots.basic");
    }

    private static @NotNull List<Integer> getExtraSlots() {
        return SettingsHandler.getConfig().intList("pet-slots.extra");
    }

    public PetsPlayer(@NotNull Player player) {
        this.player = player;
        loadEquippedPets();
    }

    private void loadEquippedPets() {
        YamlConfiguration playerData = PlayerData.get(player.getUniqueId());

        for (String petId : playerData.getStringList(PlayerHandler.PLAYER_DATA_PATH_PETS_EQUIPPED)) {
            if (PetsConfigHandler.isPetValid(petId)) {
                equipPetInternal(petId);
            }
        }
    }

    public void cleanup() {
        // save equipped pets
        YamlConfiguration playerData = PlayerData.get(player.getUniqueId());
        List<String> ep = equippedPets.stream().map(EquippedPet::getPetId).toList();
        playerData.set(PlayerHandler.PLAYER_DATA_PATH_PETS_EQUIPPED, ep);

        for (EquippedPet equippedPet : equippedPets) {
            equippedPet.getArmorStand().remove();
        }
    }

    public @NotNull List<EquippedPet> getEquippedPets() {
        return List.copyOf(equippedPets);
    }

    public boolean hasPetEquipped(@NotNull String petId) {
        for (EquippedPet equippedPet : equippedPets) {
            if (equippedPet.getPetId().equals(petId)) {
                return true;
            }
        }

        return false;
    }

    public int getTotalEquipped() {
        return equippedPets.size();
    }

    public int getMaxEquipped() {
        int max = getBasicSlots().size();
        if (canHaveExtraPets()) max += getExtraSlots().size();
        return max;
    }

    public boolean canHaveExtraPets() {
        return player.hasPermission("crossoverpets.extrapets");
    }

    public void updateInventoryPetSlots() {
        List<Integer> basicSlots = getBasicSlots();
        List<Integer> extraSlots = getExtraSlots();
        List<Integer> allSlots = new ArrayList<>(basicSlots);
        allSlots.addAll(extraSlots);

        Inventory inv = player.getInventory();
        ItemBuilder lockedItem = new ItemBuilder()
                .config(SettingsHandler.getConfig().csnn("pet-slots.items.locked"));
        ItemBuilder unusedItem = new ItemBuilder()
                .config(SettingsHandler.getConfig().csnn("pet-slots.items.unused"));
        ItemBuilder extraSlotsItem = (canHaveExtraPets() ? unusedItem : lockedItem);

        for (int i = 0; i < basicSlots.size(); i++) {
            inv.setItem(basicSlots.get(i), unusedItem.placeholder("slot", Integer.toString(i + 1)).create());
        }
        for (int i = 0; i < extraSlots.size(); i++) {
            inv.setItem(extraSlots.get(i), extraSlotsItem
                    .placeholder("slot", Integer.toString(basicSlots.size() + i + 1))
                    .create());
        }
        for (int i = 0; i < Math.min(allSlots.size(), equippedPets.size()); i++) {
            String equippedPetId = equippedPets.get(i).getPetId();
            ConfigHelper petConfig = PetsConfigHandler.getConfig().ch(equippedPetId);
            ItemStack item = new ItemBuilder()
                    .config(SettingsHandler.getConfig().csnn("pet-slots.items.pet"))
                    .hdb(petConfig.integer("hdb"))
                    .placeholder("name", petConfig.snn("name"))
                    .lorePlaceholder("description", PetsConfigHandler.getPetDescription(equippedPetId))
                    .pd(PlayerHandler.PET_SLOT_KEY, equippedPetId)
                    .create();
            inv.setItem(allSlots.get(i), item);
        }
    }

    private void equipPetInternal(@NotNull String petId) {
        ConfigHelper petConfig = PetsConfigHandler.getConfig().ch(petId);
        boolean small = !petConfig.bool("big");
        Location spawnLoc = player.getLocation().subtract(0, Movement.getBodyHeight(small), 0);

        ArmorStand armorStand = player.getWorld().spawn(spawnLoc, ArmorStand.class, a -> {
            a.setGravity(false);
            a.setInvisible(true);
            a.setInvulnerable(true);
            a.setMarker(true);
            a.setSmall(small);

            EntityEquipment entityEquipment = a.getEquipment();

            if (entityEquipment != null) {
                int hdbId = petConfig.integer("hdb");
                entityEquipment.setHelmet(new ItemBuilder().hdb(hdbId).create());
            }
        });

        EquippedPet equippedPet = new EquippedPet(petId, armorStand);

        List<Movement> movements = List.of(
                new FollowMovement(equippedPet, player),
                new JumpMovement(equippedPet, player),
                new OrbitMovement(equippedPet, player),
                new WalkMovement(equippedPet, player)
        );

        for (Movement movement : movements) {
            if (petConfig.bool("movement.types." + movement.id() + ".enabled")) {
                equippedPet.addMovement(movement);
            }
        }

        equippedPets.add(equippedPet);
    }

    public void equipPet(@NotNull String petId) {
        equipPetInternal(petId);
        updateInventoryPetSlots();
    }

    public void unequipPet(@NotNull String petId) {
        for (EquippedPet equippedPet : equippedPets) {
            if (equippedPet.getPetId().equals(petId)) {
                equippedPet.getArmorStand().remove();
                equippedPets.remove(equippedPet);

                Movement movement = equippedPet.getCurrentMovement();
                if (movement != null) movement.stop();
                break;
            }
        }

        updateInventoryPetSlots();
    }

    public boolean givePet(@NotNull String petId) {
        YamlConfiguration playerData = PlayerData.get(player.getUniqueId());
        List<String> pets = playerData.getStringList(PlayerHandler.PLAYER_DATA_PATH_PETS_INVENTORY);

        if (pets.contains(petId)) {
            return false;
        }

        pets.add(petId);
        playerData.set(PlayerHandler.PLAYER_DATA_PATH_PETS_INVENTORY, pets);
        return true;
    }

    public boolean removePet(@NotNull String petId) {
        YamlConfiguration playerData = PlayerData.get(player.getUniqueId());
        List<String> pets = playerData.getStringList(PlayerHandler.PLAYER_DATA_PATH_PETS_INVENTORY);

        if (!pets.contains(petId)) {
            return false;
        }

        pets.remove(petId);
        playerData.set(PlayerHandler.PLAYER_DATA_PATH_PETS_INVENTORY, pets);
        // TODO: check if equipped
        return true;
    }

    public @NotNull Player getPlayer() {
        return player;
    }
}
