package com.birdsprime.capturablemobsplugin;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class MobPlacePreventionListener implements Listener {

    private final CapturableMobsPlugin plugin;

    public MobPlacePreventionListener(CapturableMobsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        // Ensure that the item in hand is a mob cage (spawner)
        if (event.getItemInHand().getType() != Material.SPAWNER) return; 

        // Check if the cage is in reusable mode
        if (plugin.cageMode.equals("reusable")) {
            ItemMeta meta = event.getItemInHand().getItemMeta();
            if (meta != null && meta.getPersistentDataContainer().has(plugin.getMobCageKey(), PersistentDataType.INTEGER)) {
                // If it's an empty cage, prevent placement unless the player is sneaking
                if (!meta.getPersistentDataContainer().has(plugin.getMobTypeKey(), PersistentDataType.STRING)) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cYou cannot place an empty Mob Cage in reusable mode.");
                } else if (!event.getPlayer().isSneaking()) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cYou must sneak to place this cage with a mob.");
                } else {
                    event.getPlayer().sendMessage("§eYou have placed a mob from the Mob Cage.");
                }
            }
        }

        // Check if the cage is in single-use mode
        else if (plugin.cageMode.equals("single-use")) {
            ItemMeta meta = event.getItemInHand().getItemMeta();
            if (meta != null && meta.getPersistentDataContainer().has(plugin.getMobCageKey(), PersistentDataType.INTEGER)) {
                // Only prevent empty cages
                if (!meta.getPersistentDataContainer().has(plugin.getMobTypeKey(), PersistentDataType.STRING)) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cYou cannot place an empty Mob Cage in single-use mode.");
                }
                // ✅ Else: cage has a mob and is allowed to be placed
            }
        }        
    }
}
