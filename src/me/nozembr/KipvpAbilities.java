package me.nozembr;

import me.nozembr.events.Gladiator;
import me.nozembr.events.Grappler;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class KipvpAbilities extends JavaPlugin implements Listener {


    private Server server;

    public static KipvpAbilities instance;

    public KipvpAbilities() {
        this.server = Bukkit.getServer();
    }

    public static KipvpAbilities getInstance() {
        return KipvpAbilities.instance;
    }

    public static KipvpAbilities getInstace() {
        return KipvpAbilities.instance;
    }

    public static KipvpAbilities getPlugin() {
        return KipvpAbilities.instance;
    }
    public void loadConfiguration() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.getServer().getPluginManager().registerEvents(new Gladiator(this), this);
        this.getServer().getPluginManager().registerEvents(new Grappler(this), this);
    }

    public void onEnable() {
        final File f = new File(this.getDataFolder(), "config.yml");
        if (!f.exists()) {
            this.saveDefaultConfig();
            this.getServer().getPluginManager().registerEvents(new Gladiator(this), this);
            this.getServer().getPluginManager().registerEvents(new Grappler(this), this);
            Bukkit.getConsoleSender().sendMessage("§a[KitAbillitiesN] Plugin carregado.");
        }
        this.loadConfiguration();
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§a[KitAbillitiesN] Plugin descarregado.");
    }

}
