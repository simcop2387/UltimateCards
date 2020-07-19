package com.github.norbo11;

import java.util.ArrayList;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.commands.PluginExecutor;
import com.github.norbo11.listeners.CardsListener;
import com.github.norbo11.util.MapMethods;
import com.github.norbo11.util.MoneyMethods;
import com.github.norbo11.util.config.PluginConfig;
import com.github.norbo11.util.config.SavedTables;

public class UltimateCards extends JavaPlugin {

    public static final String TAG = "[UC]&f";
    public static final String LINE_STRING = "---------------------------------------";
    
    private static UltimateCards instance;

    // Misc
    private String version;
    private Economy economy;


    public UltimateCards() {
        super();
        instance = this;
    }

    public static UltimateCards getInstance() {
        if (instance == null) {
            instance = JavaPlugin.getPlugin(UltimateCards.class);
        }
        return instance;
    }
    
    public Economy getEconomy() {
        return economy;
    }

    public String getVersion() {
        return version;
    }

    private void addPermissions() {
        for (ArrayList<PluginCommand> commandGroup : PluginExecutor.commands) {
            for (PluginCommand cmd : commandGroup) {
                getServer().getPluginManager().addPermission(new Permission(cmd.getPermissionNodes().get(1), PermissionDefault.OP));
            }
        }
    }

    @Override
    public void onDisable() {
        if (PluginConfig.isCleanupOnDisable()) {
            MoneyMethods.returnMoney();
            MapMethods.restoreAllMaps();
        }
        getLogger().info("UltimateCards v" + version + " plugin disabled!");
    }

    @Override
    public void onEnable() {
        version = getDescription().getVersion();

        // Set all listeners and create classes
        getServer().getPluginManager().registerEvents(new CardsListener(), this);
        addPermissions();

        // Creates all files
        saveDefaultConfig();

        // Hook into vault economy
        if (!setupEconomy()) return;

        // Create/load configs
        try {
            PluginConfig.load();
            SavedTables.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set all commands to the command executor
        PluginExecutor pluginExecutor = new PluginExecutor();
        getCommand("cards").setExecutor(pluginExecutor);
        getCommand("table").setExecutor(pluginExecutor);
        getCommand("poker").setExecutor(pluginExecutor);
        getCommand("blackjack").setExecutor(pluginExecutor);
        getCommand("bj").setExecutor(pluginExecutor);

        if (!(getConfig().getDouble("table.fixRake") <= 1 && getConfig().getDouble("table.fixRake") >= -1)) {
            getLogger().severe("Check your config file! The field fixRake must be either -1 or 0-1!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("UltimateCards v" + version + " plugin enabled!");
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().severe("Economy plugin not detected! You need an ECONOMY plugin such as iConomy to run this plugin! iConomy DL at: http://dev.bukkit.org/server-mods/iconomy/");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        economy = rsp.getProvider();
        getLogger().info("Hooked into " + economy.getName());
        return true;
    }
}
