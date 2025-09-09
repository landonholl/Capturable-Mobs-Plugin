package com.birdsprime.capturablemobsplugin;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.enchantments.Enchantment;
import java.util.UUID;


import java.util.ArrayList;
import java.util.List;

public class MobCaptureListener implements Listener {

    private final NamespacedKey cageKey = new NamespacedKey("capturablemobsplugin", "mob_cage");
    private final NamespacedKey mobTypeKey = new NamespacedKey("capturablemobsplugin", "mob_type");
    private final NamespacedKey isBabyKey = new NamespacedKey("capturablemobsplugin", "is_baby");
    private final NamespacedKey customNameKey = new NamespacedKey("capturablemobsplugin", "custom_name");
    private final NamespacedKey sheepColorKey = new NamespacedKey("capturablemobsplugin", "sheep_color");

    public static final java.util.Map<java.util.UUID, Long> pickupCooldowns = new java.util.HashMap<>();

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return; // Ensure it's the player's main hand
    
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.SPAWNER) return; // Check if it's a mob cage
    
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.getPersistentDataContainer().has(cageKey, PersistentDataType.INTEGER)) return; // Ensure it's a valid mob cage
    
        // Check if the cage already contains a mob
        if (meta.getPersistentDataContainer().has(mobTypeKey, PersistentDataType.STRING)) {
            player.sendMessage("§cThis cage already contains a mob!");
            return;
        }
    
        LivingEntity entity = (LivingEntity) event.getRightClicked();   
        if (entity == null) return;
        if (entity instanceof Player) {
            player.sendMessage("§cYou cannot capture other players.");
            return;
        }        
    
        EntityType type = entity.getType();
        meta.getPersistentDataContainer().set(mobTypeKey, PersistentDataType.STRING, type.name()); // Store the mob type
        // Set spawner preview if mob has a spawn egg
        if (MobCatcherItem.hasSpawnEgg(type)) {
            meta.getPersistentDataContainer().set(new NamespacedKey("capturablemobsplugin", "spawner_entity"), PersistentDataType.STRING, "minecraft:" + type.name().toLowerCase());
        }

    
        // Store baby status if the entity is an Ageable mob
        if (entity instanceof Ageable ageable) {
            meta.getPersistentDataContainer().set(isBabyKey, PersistentDataType.INTEGER, ageable.isAdult() ? 0 : 1);
        }
    
        // Store custom name if the entity has one
        if (entity.getCustomName() != null) {
            meta.getPersistentDataContainer().set(customNameKey, PersistentDataType.STRING, entity.getCustomName());
        }
    
        // Store sheep color if the entity is a sheep
        if (entity instanceof Sheep sheep) {
            meta.getPersistentDataContainer().set(sheepColorKey, PersistentDataType.STRING, sheep.getColor().name());
        }
    
        // Update lore for the mob cage
        List<String> lore = new ArrayList<>();
        lore.add("§7Stored Mob: §e" + type.name());
        meta.setLore(lore);
    
        // Apply enchantment and hide it
        meta.addEnchant(Enchantment.DURABILITY, 1, true); //CHANGE BACK TO UNBREAKIN
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    
        // Apply changes to the item
        item.setItemMeta(meta);
    
        // Remove the captured entity
        entity.remove();
    
        // Send feedback to the player
        player.sendMessage("§eCaptured a " + type.name() + "!");
    
        // Prevent multiple captures by adding a cooldown
        pickupCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
    
}
