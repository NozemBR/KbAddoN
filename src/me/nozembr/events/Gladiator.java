package me.nozembr.events;


import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.KitbattleAPI;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.abilities.Ability;
import me.wazup.kitbattle.managers.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.block.*;
import me.nozembr.KipvpAbilities;
import org.bukkit.potion.*;
import org.bukkit.plugin.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import java.util.*;
import org.bukkit.event.entity.*;

public class Gladiator extends Ability implements Listener {
    public static ArrayList<String> gladiadorkit;
    public boolean generateGLASS;
    public HashMap<String, Location> oldl;
    public static HashMap<String, String> emluta;
    public HashMap<Player, Location> localizacao;
    public HashMap<Location, Block> bloco;
    public HashMap<Integer, String[]> players;
    public HashMap<String, Integer> tasks;
    int nextID;
    public int id1;
    private static String prefix;
    public static final HashMap<String, Location> oldLocation;
    public static final HashMap<Player, Player> emcombate;
    public static final HashMap<String, List<Location>> blocks;
    public static int id;
    public static PlayerInteractEntityEvent entidade;


    static KipvpAbilities plugin;

    static {
        Gladiator.gladiadorkit = new ArrayList<String>();
        Gladiator.emluta = new HashMap<String, String>();
        Gladiator.prefix = ChatColor.RED + "SGPGLAD ";
        oldLocation = new HashMap<String, Location>();
        emcombate = new HashMap<Player, Player>();
        blocks = new HashMap<String, List<Location>>();
        Gladiator.id = 0;
    }

    public Gladiator(KipvpAbilities instance) {
        this.generateGLASS = true;
        this.oldl = new HashMap<String, Location>();
        this.localizacao = new HashMap<Player, Location>();
        this.bloco = new HashMap<Location, Block>();
        this.players = new HashMap<Integer, String[]>();
        this.tasks = new HashMap<String, Integer>();
        this.nextID = 0;
        this.plugin = instance;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void load(FileConfiguration fileConfiguration) {

    }

    @Override
    public Material getActivationMaterial() {
        return null;
    }

    @Override
    public EntityType getActivationProjectile() {
        return null;
    }

    @Override
    public boolean isAttackActivated() {
        return false;
    }

    @Override
    public boolean isAttackReceiveActivated() {
        return false;
    }

    @Override
    public boolean isDamageActivated() {
        return false;
    }

    @Override
    public boolean isEntityInteractionActivated() {
        return true;
    }

    @Override
    public boolean execute(Player player, PlayerData playerData, Event event) {
        return false;
    }

    @EventHandler
    public void OnPlayerCommand(final PlayerCommandPreprocessEvent e) {
        final Player p = e.getPlayer();
        if (Gladiator.emluta.containsKey(p.getName()) && e.getMessage().startsWith("/")) {
            e.setCancelled(true);
            p.sendMessage(plugin.getConfig().getString("BlockCommandGlad").replace("&", "§"));
        }
    }

    public void PlayerData(final PlayerData playerData, final Event event, Player player) {
        final Player r =  (Player) entidade.getRightClicked();
        final Kitbattle instance = Kitbattle.getInstance();
        instance.sendUseAbility(player, playerData);

                if ((PlayerDataManager.get(r) != null && PlayerDataManager.get(r).getKit() == null) || playerData.getMap().isInSpawn(r)) {
                    player.sendMessage("Only kit users");
                }
            }

    @EventHandler
    public void onPlayerEntityInteract(final PlayerInteractEntityEvent event) {
        final Player p = event.getPlayer();
        if (!(event.getRightClicked() instanceof Player)) {
            return;
        }
        final Player r = (Player) event.getRightClicked();
        if (p.getItemInHand().getType() != Material.IRON_FENCE) {
            return;
        }
        if (Gladiator.emluta.containsKey(p.getName()) || Gladiator.emluta.containsKey(r.getName())) {
            event.setCancelled(true);
            p.sendMessage(String.valueOf(String.valueOf(plugin.getConfig().getString("GladAlready").replace("&", "§"))));
            return;
        }
        final boolean isCitizensNPC = r.hasMetadata("NPC");
        if (isCitizensNPC) {
            p.sendMessage(plugin.getConfig().getString("NPC-BLOCK").replace("&", "§"));
            return;
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 110, 5));
        r.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 110, 5));
        Gladiator.emluta.put(p.getName(), r.getName());
        Gladiator.emluta.put(r.getName(), p.getName());
        Gladiator.gladiadorkit.add(p.getName());
        Gladiator.gladiadorkit.add(r.getName());
        newGladiatorArena(p, r, p.getLocation());
    }

    @EventHandler
    public void OnTeleport(final PlayerTeleportEvent e) {
        final Player p = e.getPlayer();
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && Gladiator.emluta.containsKey(p.getName())) {
            p.sendMessage(plugin.getConfig().getString("ender-pearl-block").replace("&", "§"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void OnPlayerInteract2(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if (p.getItemInHand().getType() == Material.IRON_FENCE) {
            e.setCancelled(true);
            p.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(final PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.GLASS && e.getPlayer().getGameMode() != GameMode.CREATIVE && Gladiator.emluta.containsKey(e.getPlayer().getName())) {
            e.setCancelled(true);
            e.getClickedBlock().setType(Material.BEDROCK);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)KipvpAbilities.getInstance(), (Runnable)new Runnable() {
                @Override
                public void run() {
                    if (Gladiator.emluta.containsKey(e.getPlayer().getName())) {
                        e.getClickedBlock().setType(Material.GLASS);
                    }
                }
            }, 30L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(final BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.GLASS && e.getPlayer().getGameMode() != GameMode.CREATIVE && Gladiator.emluta.containsKey(e.getPlayer().getName())) {
            e.setCancelled(true);
            e.getBlock().setType(Material.BEDROCK);
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)KipvpAbilities.getInstance(), (Runnable)new Runnable() {
                @Override
                public void run() {
                    if (e.getPlayer().getGameMode() != GameMode.CREATIVE && Gladiator.emluta.containsKey(e.getPlayer().getName())) {
                        e.getBlock().setType(Material.GLASS);
                    }
                }
            }, 30L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLeft(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        this.removerdoGlad(p);
        if (Gladiator.emluta.containsKey(e.getPlayer().getName())) {
            p.setHealth(0.0);
            final Player t = Bukkit.getServer().getPlayer((String)Gladiator.emluta.get(p.getName()));
            t.sendMessage(plugin.getConfig().getString("Enemy-Disconnect").replace("&", "§"));
        }
    }

    public static final Object newGladiatorArena(final Player p1, final Player p2, final Location loc) {
        double x = loc.getX();
        final Random random = new Random();
        final double y = 75 + random.nextInt(140);
        double z = loc.getZ();
        final double x2 = x + random.nextInt(200);
        final double z2 = z + random.nextInt(389);
        final Location loc2 = new Location(p1.getWorld(), x2, y + 30.0, z2);
        final Location loc3 = new Location(p1.getWorld(), x2, y + 30.0, z2 + 8.0);
        final Location loc4 = new Location(p1.getWorld(), x2 - 8.0, y + 30.0, z2 - 8.0);
        loc2.getWorld().refreshChunk(loc2.getChunk().getX(), loc2.getChunk().getZ());
        final List<Location> location = new ArrayList<Location>();
        location.clear();
        for (int blockX = -10; blockX <= 10; ++blockX) {
            for (int blockZ = -10; blockZ <= 10; ++blockZ) {
                for (int blockY = -1; blockY <= 10; ++blockY) {
                    final Block b = loc2.clone().add((double)blockX, (double)blockY, (double)blockZ).getBlock();
                    if (!b.isEmpty()) {
                        x = random.nextInt(-55600);
                        z = random.nextInt(99954);
                        final Location newLoc = new Location(p1.getWorld(), loc2.getBlockX() + x, 50.0, loc2.getBlockZ() + z);
                        return newGladiatorArena(p1, p2, newLoc);
                    }
                    if (blockY == 10) {
                        location.add(loc2.clone().add((double)blockX, (double)blockY, (double)blockZ));
                    }
                    else if (blockY == -1) {
                        location.add(loc2.clone().add((double)blockX, (double)blockY, (double)blockZ));
                    }
                    else if (blockX == -10 || blockZ == -10 || blockX == 10 || blockZ == 10) {
                        location.add(loc2.clone().add((double)blockX, (double)blockY, (double)blockZ));
                    }
                }
            }
        }
        for (final Location arena : location) {
            arena.getBlock().setTypeIdAndData(95, (byte)Gladiator.id, true);
        }
        Gladiator.oldLocation.put(p1.getName(), p1.getLocation());
        Gladiator.oldLocation.put(p2.getName(), p2.getLocation());
        Gladiator.blocks.put(p1.getName(), location);
        Gladiator.blocks.put(p2.getName(), location);
        p1.teleport(new Location(p1.getWorld(), loc3.getX() + 7.5, loc3.getY() + 1.0, loc3.getZ(), 140.0f, 0.0f));
        p2.teleport(new Location(p2.getWorld(), loc4.getX() + 0.5, loc4.getY() + 1.0, loc2.getZ() - 7.5, -40.0f, 0.0f));
        p1.sendMessage(String.valueOf(String.valueOf(Gladiator.prefix)) + (plugin.getConfig().getString("You-Challenge").replace("&", "§") + p2.getName()));
        p1.sendMessage(plugin.getConfig().getString("NoWinner-Warn").replace("&", "§"));
        p2.sendMessage(String.valueOf(String.valueOf(Gladiator.prefix)) + (plugin.getConfig().getString("Was-Challenge").replace("&", "§") + p1.getName()));
        p2.sendMessage(plugin.getConfig().getString("NoWinner-Warn").replace("&", "§"));
        showPlayer(p1, p2);
        return null;
    }

    public static final void showPlayer(final Player one, final Player two) {
        one.showPlayer(two);
        two.showPlayer(one);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnDeath(final PlayerDeathEvent e) {
        this.removerdoGlad(e.getEntity());
    }

    public void removerdoGlad(final Player player) {
        for (final PotionEffect pot : player.getActivePotionEffects()) {
            player.removePotionEffect(pot.getType());
        }
        if (Gladiator.emluta.containsKey(player.getName())) {
            final Player t = Bukkit.getServer().getPlayer((String)Gladiator.emluta.get(player.getName()));
            for (final Location loc : Gladiator.blocks.get(player.getName())) {
                loc.getBlock().setType(Material.AIR);
            }
            Gladiator.blocks.remove(player.getName());
            Gladiator.oldLocation.remove(player.getName());
            Gladiator.gladiadorkit.remove(player.getName());
            this.localizacao.remove(player);
            player.removePotionEffect(PotionEffectType.WITHER);
            if (Gladiator.emluta.containsKey(player.getName())) {
                if (this.oldl.containsKey(t.getName())) {
                    t.teleport((Location)this.oldl.get(t.getName()));
                }
                if (this.oldl.containsKey(player.getName())) {
                    player.teleport((Location)this.oldl.get(player.getName()));
                }
                if (t != null) {
                    for (int i = 1; i < 5; ++i) {
                        t.teleport((Location)Gladiator.oldLocation.get(t.getName()));
                    }
                }
                t.removePotionEffect(PotionEffectType.WITHER);
                Gladiator.emluta.remove(t.getName());
                Gladiator.emluta.remove(player.getName());
                Gladiator.gladiadorkit.remove(t.getName());
                Gladiator.blocks.remove(t.getName());
                this.localizacao.remove(t);
                this.oldl.remove(t.getName());
            }
            this.oldl.remove(player.getName());
        }
    }
}
