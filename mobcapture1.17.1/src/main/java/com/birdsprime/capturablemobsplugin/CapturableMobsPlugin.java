package com.birdsprime.capturablemobsplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.TNTPrimed;

public class CapturableMobsPlugin extends JavaPlugin {

    public boolean recipeEnabled = true;
    public NamespacedKey mobCageKey;
    public NamespacedKey mobTypeKey;  // Declare mobTypeKey

    public String cageMode = "reusable";  // Default mode is reusable

    @Override
    public void onEnable() {

        CapturableMobsPlugin self = this;

        mobCageKey = new NamespacedKey(this, "mob_cage");
        mobTypeKey = new NamespacedKey(this, "mob_type");  // Ensure mobTypeKey is initialized here

        registerMobCageRecipe();   // Must come before reloadCageMode()

        // Register the plugin commands
        this.getCommand("mobcage").setExecutor(new MobCageCommand(this));
        this.getCommand("mobcagerecipe").setExecutor(new MobCageRecipeCommand(this));
        this.getCommand("mobcagemode").setExecutor(new MobCageModeCommand(this));
        this.adminGUI = new MobAdminGUI(this);
        getServer().getPluginManager().registerEvents(adminGUI, this);
        getServer().getPluginManager().registerEvents(new MobAdminTriggerListener(this), this);
        getServer().getPluginManager().registerEvents(new ThunderStrikeListener(this), this);
        this.getServer().getPluginManager().registerEvents(new MobReleaseListener(this), this);
        this.getServer().getPluginManager().registerEvents(new MobCageCraftListener(this), this);

        reloadCageMode();

        getLogger().info("CapturableMobsPlugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("CapturableMobsPlugin disabled!");
    }

    public void registerMobCageRecipe() {
        if (!recipeEnabled) return;

        // Define the Mob Cage recipe
        ItemStack mobCage = MobCatcherItem.createMobCatcher();
        ShapedRecipe mobCageRecipe = new ShapedRecipe(mobCageKey, mobCage);

        // Set the recipe shape
        mobCageRecipe.shape("III", 
                            "I I", 
                            "III");

        // Set the ingredients (I = iron bars)
        mobCageRecipe.setIngredient('I', Material.IRON_BARS);

        // Register the recipe
        Bukkit.addRecipe(mobCageRecipe);
    }


    public void reloadCageMode() {
        HandlerList.unregisterAll(this);
    
        getServer().getPluginManager().registerEvents(new MobCaptureListener(), this);
        getServer().getPluginManager().registerEvents(new MobPlacePreventionListener(this), this);
        getServer().getPluginManager().registerEvents(new MobCageCraftListener(this), this);
        getServer().getPluginManager().registerEvents(adminGUI, this);
        getServer().getPluginManager().registerEvents(new MobAdminTriggerListener(this), this);
        getServer().getPluginManager().registerEvents(new ThunderStrikeListener(this), this);
    
        if (cageMode.equalsIgnoreCase("reusable")) {
            getServer().getPluginManager().registerEvents(new MobReleaseListener(this), this);
        } else if (cageMode.equalsIgnoreCase("single-use")) {
            getServer().getPluginManager().registerEvents(new MobSpawnerUseListener(this), this);
        }
    
        getLogger().info("Cage mode changed to " + cageMode);
    }

    public NamespacedKey getMobCageKey() {
        return mobCageKey;
    }

    public NamespacedKey getMobTypeKey() {
        return mobTypeKey;  // Add this method to access mobTypeKey
    }

    private MobAdminGUI adminGUI;

    public MobAdminGUI getAdminGUI() {
        return adminGUI;
    }

}
