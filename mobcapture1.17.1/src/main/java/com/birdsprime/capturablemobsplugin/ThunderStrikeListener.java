package com.birdsprime.capturablemobsplugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ThunderStrikeListener implements Listener {

    private final CapturableMobsPlugin plugin;
    private final java.util.Map<java.util.UUID, Long> cooldowns = new java.util.HashMap<>();
    private final Map<UUID, Long> recentThunderUsers = new HashMap<>();


    public ThunderStrikeListener(CapturableMobsPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        ItemStack held = player.getInventory().getItemInMainHand();
        if (held == null || held.getType() == Material.AIR) return;

        ItemMeta meta = held.getItemMeta();
        if (meta == null) return;

        NamespacedKey key = new NamespacedKey(plugin, "thunder_strike");
        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) return;

        // Cooldown check
        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(player.getUniqueId()) && now - cooldowns.get(player.getUniqueId()) < 300) return;
        cooldowns.put(player.getUniqueId(), now);

        Entity target = event.getEntity();
        Location loc = target.getLocation();

        recentThunderUsers.put(player.getUniqueId(), System.currentTimeMillis());

        loc.getWorld().strikeLightning(loc);
        loc.getWorld().createExplosion(loc, 0.5f, false, true, player); // Block-damaging explosion

        if (target instanceof Player p && p.getName().equalsIgnoreCase("BilboBunchkins")) {
            event.setDamage(0);
        }        
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;
        if (!p.getName().equalsIgnoreCase("BilboBunchkins")) return;

        long lastUse = recentThunderUsers.getOrDefault(p.getUniqueId(), 0L);
        if (System.currentTimeMillis() - lastUse > 1000) return; // More than 1s ago → not from ThunderStrike

        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause == EntityDamageEvent.DamageCause.LIGHTNING ||
            cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
            cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {

            event.setDamage(0);
            // Optionally allow knockback/hurt animation:
            // event.setCancelled(true);
            p.setFireTicks(0); // Also suppress fire
        }
    }
}
