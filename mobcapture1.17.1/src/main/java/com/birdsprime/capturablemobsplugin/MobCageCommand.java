package com.birdsprime.capturablemobsplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MobCageCommand implements CommandExecutor {

    private final CapturableMobsPlugin plugin;

    public MobCageCommand(CapturableMobsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (plugin.recipeEnabled) {
            sender.sendMessage("§cThis command is disabled while the recipe is enabled.");
            return true;
        }
        
        if (sender instanceof Player player) {
            player.getInventory().addItem(MobCatcherItem.createMobCatcher());
            player.sendMessage("You have received a Mob Cage!");
        } else {
            sender.sendMessage("This command can only be used by players.");
        }        
        return true;
    }
}
