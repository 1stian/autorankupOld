package com.ohneemc.autorankup;

import com.ohneemc.autorankup.bungee.Messages;
import com.ohneemc.autorankup.checks.CanRankUp;
import com.ohneemc.autorankup.commands.Autorankup;
import com.ohneemc.autorankup.commands.Check;
import com.ohneemc.autorankup.config.Config;
import com.ohneemc.autorankup.events.OnPlayer;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.logging.Logger;

public final class AutoRankUpSpigot extends JavaPlugin implements Listener {
    //Public
    public static boolean debug = false;
    public static final Logger log = Logger.getLogger("Minecraft");
    public static final String CHANNEL = "Autorankup:rank";
    public static final String SUB_CHANNEL = "rankup";

    // Private
    private static boolean use_vault;
    private static String get_placeholders;
    private static boolean bungee_broadcast;
    private static boolean player_join;
    private static boolean broadcast_enb;
    private static String broadcast_msg;
    private static boolean player_enb;
    private static String player_msg;
    private static int frequency;
    private static Permission perms = null;

    private static AutoRankUpSpigot autoRankUpSpigot;

    @Override
    public void onEnable() {
        autoRankUpSpigot = this;
        // Saving and reloading config file
        saveDefaultConfig();
        reloadConfig();

        // Metrics
        Metrics metrics = new Metrics(this, 7197);

        // Filling values
        use_vault = Config.getBoolean("vault.enabled");
        get_placeholders = Config.getString("placeholder.group");
        bungee_broadcast = Config.getBoolean("bungee.broadcast");
        player_join = Config.getBoolean("settings.login");
        broadcast_enb = Config.getBoolean("settings.broadcast");
        broadcast_msg = Config.getString("settings.broadcastmessage");
        player_enb = Config.getBoolean("settings.player");
        player_msg = Config.getString("settings.playermessage");
        frequency = Config.getInteger("settings.frequency");


        // Messages - Registering channels
        if (getBungee_broadcast()){
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new Messages());
        }

        // Checking for plugins.
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getPluginManager().registerEvents(this, this);
        } else {
            log.warning("Could not find PlaceholderAPI!! Plugin can not work without it! - AutoRankUp disabled.");
            getServer().getPluginManager().disablePlugin(this);
        }
        if (Bukkit.getPluginManager().getPlugin("Plan") == null) {
            log.warning("Could not find Plan!! Plugin can not work without it! - AutoRankUp disabled.");
            getServer().getPluginManager().disablePlugin(this);
        }

        if (use_vault){
            if (Bukkit.getPluginManager().getPlugin("Vault") == null){
                log.warning("Could not find Vault!! Plugin can not work without it! - AutoRankUp disabled.");
                getServer().getPluginManager().disablePlugin(this);
            }
            setupPermissions();
            log.info("[AutoRankUp] - Using Vault system.");
        }

        // Check on player join
        if (getPlayer_join())
            getServer().getPluginManager().registerEvents(new OnPlayer(), this);

        // Register commands
        getCommand("check").setExecutor(new Check());
        getCommand("autorankup").setExecutor(new Autorankup());

        // Initiate task
        task();
    }

    @Override
    public void onDisable() {
        log.info("Disabling");
    }

    /***
     * Initiates the task
     */
    private void task() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            if (players == null){
                return;
            }
            log.info("[AutoRankUp] - checking for eligible players.");
                    for (Player player : Bukkit.getOnlinePlayers()){
                        CanRankUp.rankUp(player);
                    }
                }, 120L, (long) getFrequency() * 1200L
        );
    }

    public static AutoRankUpSpigot getAutoRankUpSpigot(){ return autoRankUpSpigot; }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static boolean getBungee_broadcast(){ return bungee_broadcast; }

    public static boolean getPlayer_join(){ return player_join; }

    public static boolean getBroadcastEnabled(){ return broadcast_enb; }

    public static String getBroadcast_msg(){ return broadcast_msg; }

    public static boolean getPlayer_enb(){ return player_enb; }

    public static String getPlayer_msg() { return player_msg; }

    public static int getFrequency() { return frequency; }

    public static boolean getVault() { return use_vault; }

    public static Permission getPerms() { return perms; }

    public static String getPlaceholder() {return get_placeholders; }
}
