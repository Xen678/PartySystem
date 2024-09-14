package de.nms.partysystem;

import org.bukkit.plugin.java.JavaPlugin;

public final class PartySystem extends JavaPlugin {


    @Override
    public void onEnable() {
        // Register the command executor
        getCommand("party").setExecutor(new PartyCommand());
        Loader.onEnable();

        // Register the event listener
        getServer().getPluginManager().registerEvents(new PartyEventListener(), this);
        Loader.onEnable();
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
