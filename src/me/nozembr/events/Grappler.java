package me.nozembr.events;


import me.nozembr.KipvpAbilities;
import me.wazup.kitbattle.Kitbattle;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grappler implements Listener
{
    static ArrayList<String> grappler;
    private final Server server;

    public KipvpAbilities plugin;

    public Grappler(final KipvpAbilities instance) {

        grappler = new ArrayList<String>();
        this.server = Bukkit.getServer();
        this.plugin = instance;
    }
    @EventHandler
    public void onPlayerFish(final PlayerFishEvent event) {
        final Player player = event.getPlayer();
        ItemStack mao = player.getItemInHand();
        if (mao.getType() == Material.FISHING_ROD) {
            if (mao.hasItemMeta() &&
                    mao.getItemMeta().hasDisplayName() &&
                    mao.getItemMeta().getDisplayName().equals("§4Grappler")) {
                if (plugin.getConfig().getString("grappler").equalsIgnoreCase("true")) {
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
                        final org.bukkit.util.Vector v = player.getVelocity();
                        v.setX(v_x);
                        v.setY(v_y);
                        v.setZ(v_z);
                        player.setVelocity(v);
                        grappler.add(player.getName());
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask((Plugin) this, new Runnable() {
                            @Override
                            public void run() {
                                grappler.remove(player.getName());
                            }
                        }, plugin.getConfig().getInt("grapplercooldown") * 20L);
                    }
                }
            }
        }
    }
}
