package com.birdsprime.capturablemobsplugin;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class MobCageCraftListener implements Listener {
    private final CapturableMobsPlugin plugin;

    public MobCageCraftListener(CapturableMobsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();

        // Only replace Mob Cage recipes
        if (result == null || result.getType() != Material.SPAWNER) return;

        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;

        // Check if it's your custom recipe (should match cage key tag)
        if (!meta.getPersistentDataContainer().has(plugin.getMobCageKey(), PersistentDataType.INTEGER)) return;

        // Replace the result with a new non-stackable item
        ItemStack newCage = result.clone();
        ItemMeta newMeta = newCage.getItemMeta();

        if (newMeta == null) return;

        // Add UUID to make it unstackable
        NamespacedKey uniqueKey = new NamespacedKey(plugin, "unique_id");
        newMeta.getPersistentDataContainer().set(uniqueKey, PersistentDataType.STRING, UUID.randomUUID().toString());

        // Set display and tooltip again (optional redundancy)
        newMeta.setDisplayName("§rMob Cage");
        newMeta.setLore(java.util.List.of("§7Stored Mob: §8None"));
        newCage.setItemMeta(newMeta);

        // Update crafting result
        newCage.setAmount(1); // Prevent crafting multiple stacked cages
        event.getInventory().setResult(newCage);

    }
}
