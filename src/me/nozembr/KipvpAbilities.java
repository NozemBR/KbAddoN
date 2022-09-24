package me.nozembr;

import me.nozembr.events.Gladiator;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class KipvpAbilities extends JavaPlugin implements Listener {


    private Server server;

    public static KipvpAbilities instance;
    static ArrayList<String> grappler;

    public KipvpAbilities plugin;
    public String cooldownMessage;
    static List<String> cooldown8;
    ArrayList<Player> cooldown;


    static {
        KipvpAbilities.cooldown8 = new ArrayList<String>();
    }

    public KipvpAbilities() {
        this.server = Bukkit.getServer();
        this.cooldownMessage = ChatColor.RED + "pode nao!";
        grappler = new ArrayList<String>();
        this.server = Bukkit.getServer();
        this.cooldown = new ArrayList<Player>();
        this.plugin = instance;
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
        this.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
    }

    public void onEnable() {
        final File f = new File(this.getDataFolder(), "config.yml");
        if (!f.exists()) {
            this.saveDefaultConfig();
            this.getServer().getPluginManager().registerEvents(new Gladiator(this), this);
            Bukkit.getConsoleSender().sendMessage("§a[KitAbillitiesN] Plugin carregado.");
        }
        this.loadConfiguration();
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§a[KitAbillitiesN] Plugin descarregado.");
    }


    @EventHandler
    public void onPlayerFish(final PlayerFishEvent event) {
        final Player player = event.getPlayer();
        ItemStack mao = player.getItemInHand();
        if (mao.getType() == Material.FISHING_ROD) {
            if (mao.hasItemMeta() &&
                    mao.getItemMeta().hasDisplayName() &&
                    mao.getItemMeta().getDisplayName().equals("§4Grappler")) {
                if (this.getConfig().getString("grappler").equalsIgnoreCase("true")) {
                    if (grappler.contains(player.getName())) {
                        return;
                    }
                    if (event.getState().equals(PlayerFishEvent.State.IN_GROUND) || event.getState().equals(PlayerFishEvent.State.FAILED_ATTEMPT) || event.getState().equals(PlayerFishEvent.State.CAUGHT_ENTITY)) {
                        final Location lc = player.getLocation();
                        final Location to = event.getHook().getLocation();
                        lc.setY(lc.getY() + 0.5);
                        player.teleport(lc);
                        final double g = -0.08;
                        final double t;
                        final double d = t = to.distance(lc);
                        final double v_x = (1.0 + 0.07 * t) * (to.getX() - lc.getX()) / t;
                        final double v_y = (1.0 + 0.03 * t) * (to.getY() - lc.getY()) / t - 0.5 * g * t;
                        final double v_z = (1.0 + 0.07 * t) * (to.getZ() - lc.getZ()) / t;
                        final Vector v = player.getVelocity();
                        v.setX(v_x);
                        v.setY(v_y);
                        v.setZ(v_z);
                        player.setVelocity(v);

                        grappler.add(player.getName());
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask((Plugin) this, (Runnable) new Runnable() {
                            @Override
                            public void run() {
                                final Player player = event.getPlayer();
                                grappler.remove(player.getName());
                            }
                        }, (long) (KipvpAbilities.this.getConfig().getInt("grapplercooldown") * 20));
                    }
                }
            }
        }
    }
}
