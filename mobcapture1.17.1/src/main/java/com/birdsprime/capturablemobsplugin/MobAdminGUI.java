package com.birdsprime.capturablemobsplugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.Sound;
import org.bukkit.Location;

public class MobAdminGUI implements Listener {

    private final CapturableMobsPlugin plugin;

    public MobAdminGUI(CapturableMobsPlugin plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, ChatColor.DARK_RED + "Admin Control");

        gui.setItem(2, createItem(Material.BLAZE_ROD, "§6Enchant Hotbar Slot 0 with Thunder"));
        gui.setItem(3, createItem(Material.TNT, "§cSpawn TNT"));
        gui.setItem(4, createItem(Material.ENDER_CHEST, "§bDuplicate Hotbar Slot 0"));
        gui.setItem(5, createItem(Material.SPAWNER, "§aGive Mob Cages"));;

        gui.setItem(8, createItem(Material.BARRIER, "§7Close"));

        player.openInventory(gui);

        gui.setItem(0, createItem(Material.PAPER, "§7Rename me to: <amount> <x> <y> <z> or <amount> <player>"));
    }

    private ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.DARK_RED + "Admin Control")) {
            event.setCancelled(true); // block item movement

            Player p = (Player) event.getWhoClicked();
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;

            String title = clicked.getItemMeta().getDisplayName();

            if (title.contains("Duplicate")) {
                ItemStack hotbarItem = p.getInventory().getItem(0);
            
                if (hotbarItem == null || hotbarItem.getType() == Material.AIR) {
                    p.sendMessage(ChatColor.RED + "No item in hotbar slot 0 to duplicate.");
                    return;
                }
            
                ItemStack clone = hotbarItem.clone();
                clone.setAmount(Math.min(hotbarItem.getMaxStackSize(), 64)); // or keep original stack size
            
                // Add to player's inventory
                p.getInventory().addItem(clone);
                p.sendMessage(ChatColor.GREEN + "Duplicated item from slot 0.");
            }
            
            if (title.contains("Thunder")) {
                ItemStack hotbarItem = p.getInventory().getItem(0);
                if (hotbarItem == null || hotbarItem.getType() == Material.AIR) {
                    p.sendMessage(ChatColor.RED + "No item in hotbar slot 0 to enchant.");
                    return;
                }
            
                ItemMeta meta = hotbarItem.getItemMeta();
                if (meta != null) {
                    NamespacedKey key = new NamespacedKey(plugin, "thunder_strike");
                    meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
                    List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
                    lore.add(ChatColor.GOLD + "Thunder-Infused");
                    meta.setLore(lore);
                    hotbarItem.setItemMeta(meta);
                    p.sendMessage(ChatColor.YELLOW + "Slot 0 item enchanted with thunder strike!");
                }
            }            

            if (title.contains("TNT")) {
                ItemStack input = p.getInventory().getItem(0); // hotbar slot 0
            
                if (input == null || !input.hasItemMeta() || !input.getItemMeta().hasDisplayName()) {
                    p.sendMessage(ChatColor.RED + "Rename the item in slot 0 to: <amount> <x> <y> <z> or <amount> <player>");
                    p.closeInventory();
                    return;
                }
            
                String name = ChatColor.stripColor(input.getItemMeta().getDisplayName()).trim();
                String[] parts = name.split("\\s+");
            
                try {
                    final int amount = Integer.parseInt(parts[0]);
            
                    final Location finalSpawnLoc;
                    final Player finalTarget;
            
                    if (parts.length == 4) {
                        double x = Double.parseDouble(parts[1]);
                        double y = Double.parseDouble(parts[2]);
                        double z = Double.parseDouble(parts[3]);
                        finalSpawnLoc = new Location(p.getWorld(), x, y, z);
                        finalTarget = null;
                    } else if (parts.length == 2) {
                        Player target = Bukkit.getPlayerExact(parts[1]);
                        if (target == null || !target.isOnline()) {
                            p.sendMessage(ChatColor.RED + "Player not found or not online.");
                            p.closeInventory();
                            return;
                        }
                        finalSpawnLoc = target.getLocation();
                        finalTarget = target;
                    } else {
                        p.sendMessage(ChatColor.RED + "Invalid format. Use: <amount> <x> <y> <z> or <amount> <player>");
                        p.closeInventory();
                        return;
                    }
            
                    // Play hiss sound first
                    finalSpawnLoc.getWorld().playSound(finalSpawnLoc, Sound.ENTITY_CREEPER_PRIMED, 1.5f, 1.0f);
            
                    // Delay for effect, then detonate all TNT instantly
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        for (int i = 0; i < amount; i++) {
                            double offsetX = (Math.random() - 0.5) * 2;
                            double offsetZ = (Math.random() - 0.5) * 2;
                            Location loc = finalSpawnLoc.clone().add(offsetX, 0.5, offsetZ);
            
                            TNTPrimed tnt = loc.getWorld().spawn(loc, TNTPrimed.class);
                            tnt.setFuseTicks(new Random().nextInt(16));
                            tnt.setYield(24.0f);
                        }
            
                        if (finalTarget != null) {
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                finalTarget.kickPlayer(ChatColor.RED + "Creeper! Aww man!");
                            }, 40L); // 40 ticks = 2 seconds                            
                        }
            
                    }, 40L); // 20 ticks = 1 second
            
                    p.sendMessage(ChatColor.RED + "BOOM! " + ChatColor.GREEN + "Surprise attack triggered.");
                    p.closeInventory();
            
                } catch (NumberFormatException e) {
                    p.sendMessage(ChatColor.RED + "Invalid number format.");
                    p.closeInventory();
                }
            }                      

            if (title.contains("Cage")) {
                ItemStack cages = MobCatcherItem.createMobCatcher();
                cages.setAmount(64);
                p.getInventory().addItem(cages);
                p.sendMessage(ChatColor.GREEN + "You received 64 Mob Cages.");
            }

            if (title.contains("Close")) {
                p.closeInventory();
            }
        }
    }

    private String randomNamespace() {
        String[] namespaces = {
            "minecraft", "paper", "spigot", "mojang", "fabric", "forge", "vanilla", "datapack",
            "bukkit", "net.minecraft", "java", "debug", "render", "dimension", "async"
        };
        return namespaces[(int) (Math.random() * namespaces.length)];
    }
    
    private String randomThing() {
        String[] things = {
            "block_entity." + randomBlockId(),
            "tick_queue",
            "dimension/" + randomDimension(),
            "loot_table/" + randomLootTable(),
            "pathfinder/update_" + (int)(Math.random() * 10000),
            "structure/start_chunk_" + (int)(Math.random() * 99999),
            "spawn_chunk_buffer",
            "network/handler_" + (int)(Math.random() * 200),
            "recipe/sync_" + randomCraftingComponent(),
            "tag/entity_type",
            "tag/item/" + randomItemId(),
            "scheduler/async_tick",
            "region/r." + (int)(Math.random()*40) + "." + (int)(Math.random()*40) + ".mca",
            "server_tick_list/block",
            "log/rotate_" + System.currentTimeMillis()
        };
        return things[(int) (Math.random() * things.length)];
    }

    private String randomBlockId() {
        String[] blocks = {"spawner", "bedrock", "redstone_block", "ruined_portal"};
        return blocks[(int)(Math.random() * blocks.length)];
    }
    
    private String randomDimension() {
        String[] dims = {"overworld", "the_nether", "the_end", "test"};
        return dims[(int)(Math.random() * dims.length)];
    }
    
    private String randomLootTable() {
        String[] tables = {"bastion_bridge", "desert_pyramid", "village_toolsmith"};
        return tables[(int)(Math.random() * tables.length)];
    }
    
    private String randomCraftingComponent() {
        String[] comps = {"iron_ingot", "mob_spawner"};
        return comps[(int)(Math.random() * comps.length)];
    }
    
    private String randomItemId() {
        String[] items = {"stone", "structure_void", "tnt_minecart"};
        return items[(int)(Math.random() * items.length)];
    }    
    
}
