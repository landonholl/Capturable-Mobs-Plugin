package com.birdsprime.capturablemobsplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class MobCageModeCommand implements CommandExecutor {

    private final CapturableMobsPlugin plugin;

    public MobCageModeCommand(CapturableMobsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("capturablemobs.mode")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Current cage mode: " + plugin.cageMode);
            sender.sendMessage(ChatColor.GRAY + "Usage: /mobcagemode <reusable|single-use>");
            return true;
        }

        String mode = args[0].toLowerCase();
        if (!mode.equals("reusable") && !mode.equals("single-use")) {
            sender.sendMessage(ChatColor.RED + "Invalid mode. Use 'reusable' or 'single-use'.");
            return true;
        }

        plugin.cageMode = mode;
        plugin.reloadCageMode();
        sender.sendMessage(ChatColor.GREEN + "Mob Cage mode set to: " + plugin.cageMode);
        return true;
    }
}
