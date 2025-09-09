package com.birdsprime.capturablemobsplugin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class MobSpawnerUseListener implements Listener {

    private final CapturableMobsPlugin plugin;

    public MobSpawnerUseListener(CapturableMobsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemMeta meta = event.getItemInHand().getItemMeta();
        if (meta == null) return;

        if (!meta.getPersistentDataContainer().has(plugin.getMobCageKey(), PersistentDataType.INTEGER)) return;
        if (!meta.getPersistentDataContainer().has(plugin.getMobTypeKey(), PersistentDataType.STRING)) return;

        String mobType = meta.getPersistentDataContainer().get(plugin.getMobTypeKey(), PersistentDataType.STRING);
        String customName = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "custom_name"), PersistentDataType.STRING);

        final Block block = event.getBlockPlaced();
        final Location loc = block.getLocation();        

        // OPTIONAL: Set spinning display mob in placed spawner
        String entityId = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "spawner_entity"), PersistentDataType.STRING);
        if (entityId != null) {
            BlockState state = block.getState();
            if (state instanceof CreatureSpawner spawner) {
                EntityType displayType = EntityType.fromName(entityId.replace("minecraft:", ""));
                if (displayType != null) {
                    spawner.setSpawnedType(displayType);
                    spawner.update();
                }
            }
        }            

        // Delay task to allow block to "exist" before breaking
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = loc.getWorld();
                if (world == null) return;

                // Visuals
                world.spawnParticle(Particle.CLOUD, loc.add(0.5, 0.5, 0.5), 20);
                world.playSound(loc, Sound.BLOCK_ANVIL_BREAK, 1f, 1f);

                // Remove the block
                block.setType(Material.AIR);

                // Spawn the mob
                try {
                    EntityType type = EntityType.valueOf(mobType);
                    LivingEntity entity = (LivingEntity) world.spawnEntity(loc.add(0.5, 0, 0.5), type);
                    if (customName != null) {
                        entity.setCustomName(customName);
                        entity.setCustomNameVisible(true);
                    }
                } catch (Exception ignored) {}
            }
        }.runTaskLater(plugin, 20L); // 20 ticks = 1 second
    }
}
