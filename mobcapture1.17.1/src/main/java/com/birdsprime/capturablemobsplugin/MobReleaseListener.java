package com.birdsprime.capturablemobsplugin;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class MobReleaseListener implements Listener {

    private final CapturableMobsPlugin plugin;

    private final NamespacedKey cageKey = new NamespacedKey("capturablemobsplugin", "mob_cage");
    private final NamespacedKey mobTypeKey = new NamespacedKey("capturablemobsplugin", "mob_type");

    public static final java.util.Map<java.util.UUID, Long> releaseCooldowns = new java.util.HashMap<>(); // Store cooldowns

    public MobReleaseListener(CapturableMobsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return; // Only handle main hand
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return; // Only respond to right-clicks

        Player player = event.getPlayer();
        if (!player.isSneaking()) return; // Ensure the player is sneaking

        // Check for cooldown to prevent spam clicking
        Long lastRelease = releaseCooldowns.get(player.getUniqueId());
        if (lastRelease != null && System.currentTimeMillis() - lastRelease < 500) {
            // Block release if less than 500ms since the last release
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.SPAWNER) return; // Only work with spawners (mob cages)

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return; // Ensure the item has metadata
        if (!meta.getPersistentDataContainer().has(mobTypeKey, PersistentDataType.STRING)) return; // Ensure the mob type exists

        String mobType = meta.getPersistentDataContainer().get(mobTypeKey, PersistentDataType.STRING);
        Location loc = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation().add(0.5, 1, 0.5);
        World world = loc.getWorld();
        if (world == null) return;

        // Spawn the mob from the cage
        try {
            EntityType type = EntityType.valueOf(mobType);
            LivingEntity entity = (LivingEntity) world.spawnEntity(loc, type);

            // If in reusable mode, reset the mob data after releasing
            if (plugin.cageMode.equals("reusable")) {
                // Remove the mob data (mobType)
                meta.getPersistentDataContainer().remove(mobTypeKey);

                // Reset the lore and enchantments (clear mob-related data)
                meta.setLore(List.of("§7Stored Mob: §8None"));
                meta.removeEnchant(org.bukkit.enchantments.Enchantment.DURABILITY); // You can add more enchantments if needed
                item.setItemMeta(meta);  // Apply the changes to the item
            }

            // Set cooldown for the next release
            releaseCooldowns.put(player.getUniqueId(), System.currentTimeMillis());

            player.sendMessage("Released a " + mobType + "!");
            
        } catch (Exception ignored) {
            // Handle errors silently (you can log the error if needed)
        }
    }

    
}
