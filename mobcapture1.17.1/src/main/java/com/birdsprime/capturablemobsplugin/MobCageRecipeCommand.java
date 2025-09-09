package com.birdsprime.capturablemobsplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MobCageRecipeCommand implements CommandExecutor {

    private final CapturableMobsPlugin plugin;

    public MobCageRecipeCommand(CapturableMobsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§eUsage: /mobcagerecipe <enable|disable>");
            return true;
        }

        if (args[0].equalsIgnoreCase("disable")) {
            Bukkit.removeRecipe(plugin.getMobCageKey());
            plugin.recipeEnabled = false;
            sender.sendMessage("§cMob Cage recipe disabled.");
        } else if (args[0].equalsIgnoreCase("enable")) {
            plugin.recipeEnabled = true;
            plugin.registerMobCageRecipe(); // removes and re-adds recipe
            sender.sendMessage("§aMob Cage recipe enabled.");
        } else {
            sender.sendMessage("§eUsage: /mobcagerecipe <enable|disable>");
        }

        return true;
    }
}
