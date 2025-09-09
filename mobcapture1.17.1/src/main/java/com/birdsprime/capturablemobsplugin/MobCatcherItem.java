package com.birdsprime.capturablemobsplugin;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MobCatcherItem {

    // Create a Mob Cage item
    public static ItemStack createMobCatcher() {
        ItemStack item = new ItemStack(Material.SPAWNER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // Set custom name
            meta.setDisplayName("§rMob Cage");

            // Add flags
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            // Add persistent tags
            NamespacedKey cageKey = new NamespacedKey("capturablemobsplugin", "mob_cage");
            NamespacedKey uniqueKey = new NamespacedKey("capturablemobsplugin", "unique_id");

            meta.getPersistentDataContainer().set(cageKey, PersistentDataType.INTEGER, 1);
            meta.getPersistentDataContainer().set(uniqueKey, PersistentDataType.STRING, UUID.randomUUID().toString());

            // Override tooltip with padded lore
            List<String> lore = new ArrayList<>();
            lore.add("§7Stored Mob: §8None");
            lore.add("§8"); // blank line to suppress spawner tooltip
            lore.add("§8Right-click to capture a mob");

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    // Update the lore for Mob Cage when a mob is captured
    public static void updateMobCageLore(ItemStack item, String mobType) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            if (mobType != null && !mobType.isEmpty()) {
                lore.add("§7Stored Mob: §e" + mobType);
            } else {
                lore.add("§7Stored Mob: §8None");
            }

            lore.add("§7Right-click to release mob");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    public static boolean hasSpawnEgg(EntityType type) {
        try {
            Material egg = Material.valueOf(type.name() + "_SPAWN_EGG");
            return egg != Material.AIR;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
