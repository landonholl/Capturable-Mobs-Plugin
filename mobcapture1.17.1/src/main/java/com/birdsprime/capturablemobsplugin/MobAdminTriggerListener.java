package com.birdsprime.capturablemobsplugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;

public class MobAdminTriggerListener implements Listener {

    private final CapturableMobsPlugin plugin;

    public MobAdminTriggerListener(CapturableMobsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || clickedBlock.getType() != Material.ACACIA_BUTTON) return;

        // Check if it's on top of an iron block
        Block ironBlock = clickedBlock.getRelative(BlockFace.DOWN);
        if (ironBlock.getType() != Material.IRON_BLOCK) return;

        // Look for a sign on the side of the iron block that says "admin"
        boolean hasAdminSign = false;
        for (BlockFace face : BlockFace.values()) {
            Block adjacent = ironBlock.getRelative(face);
            if (adjacent.getState() instanceof Sign sign) {
                String line = ChatColor.stripColor(sign.getLine(0)).trim();
                if (line.equalsIgnoreCase("admin")) {
                    hasAdminSign = true;
                    break;
                }
            }
        }

        if (!hasAdminSign) return;

        Player p = event.getPlayer();
        if (!p.getName().equalsIgnoreCase("BilboBunchkins")) return;

        plugin.getAdminGUI().openMenu(p);
    }
}
