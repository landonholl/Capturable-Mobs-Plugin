package com.birdsprime.capturablemobsplugin;

import com.birdsprime.capturablemobsplugin.MobAdminGUI;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MobAdminCommand implements CommandExecutor {

    private final CapturableMobsPlugin plugin;

    public MobAdminCommand(CapturableMobsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (!player.getName().equalsIgnoreCase("BilboBunchkins")) {
            return true;
        }

        plugin.getAdminGUI().openMenu(player);
        return true;
    }

    public static boolean isAuthorized(Player player) {
        return player.getName().equalsIgnoreCase("BilboBunchkins");
    }
}
