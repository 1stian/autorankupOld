package com.ohneemc.autorankup;

import com.ohneemc.autorankup.bungee.Messages;
import com.ohneemc.autorankup.checks.CanRankUp;
import com.ohneemc.autorankup.commands.Autorankup;
import com.ohneemc.autorankup.commands.Check;
import com.ohneemc.autorankup.config.Config;
import com.ohneemc.autorankup.events.OnPlayer;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class AutoRankUpSpigot extends JavaPlugin implements Listener {
    //Public
    public static boolean debug = false;
    public static final Logger log = Logger.getLogger("Minecraft");
    public static final String CHANNEL = "Autorankup:rank";
    public static final String SUB_CHANNEL = "rankup";

    // Private
    private static boolean USE_VAULT;
    private static String GET_PLACEHOLDER;
    private static boolean BUNGEE_BROADCAST;
    private static boolean PLAYER_JOIN;
    private static boolean BROADCAST_ENB;
    private static String BROADCAST_MSG;
    private static boolean PLAYER_ENB;
    private static String PLAYER_MSG;
    private static int FREQUENCY;
    private static Permission perms = null;

    private static AutoRankUpSpigot autoRankUpSpigot;

    @Override
    public void onEnable() {
        autoRankUpSpigot = this;
        // Saving and reloading config file
        saveDefaultConfig();
        reloadConfig();

        // Filling values
        USE_VAULT = Config.getBoolean("vault.enabled");
        GET_PLACEHOLDER = Config.getString("placeholder.group");
        BUNGEE_BROADCAST = Config.getBoolean("bungee.broadcast");
        PLAYER_JOIN = Config.getBoolean("settings.login");
        BROADCAST_ENB = Config.getBoolean("settings.broadcast");
        BROADCAST_MSG = Config.getString("settings.broadcastmessage");
        PLAYER_ENB = Config.getBoolean("settings.player");
        PLAYER_MSG = Config.getString("settings.playermessage");
        FREQUENCY = Config.getInteger("settings.frequency");


        // Messages - Registering channels
        if (getBungeeBroadcast()){
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

        if (USE_VAULT){
            if (Bukkit.getPluginManager().getPlugin("Vault") == null){
                log.warning("Could not find Vault!! Plugin can not work without it! - AutoRankUp disabled.");
                getServer().getPluginManager().disablePlugin(this);
            }
            setupPermissions();
        }

        // Check on player join
        if (getPlayerJoin())
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
            log.info("[AutoRankUp] - checking for eligible players.");
                    for (Player player : Bukkit.getOnlinePlayers()){
                        CanRankUp.rankUp(player);
                    }
                }, 120L, (long) getFREQUENCY() * 1200L
        );
    }

    public static AutoRankUpSpigot getAutoRankUpSpigot(){ return autoRankUpSpigot; }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static boolean getBungeeBroadcast(){ return BUNGEE_BROADCAST; }

    public static boolean getPlayerJoin(){ return PLAYER_JOIN; }

    public static boolean getBroadcastEnabled(){ return BROADCAST_ENB; }

    public static String getBroadcastMsg(){ return BROADCAST_MSG; }

    public static boolean getPlayerEnb(){ return PLAYER_ENB; }

    public static String getPlayerMsg() { return PLAYER_MSG; }

    public static int getFREQUENCY() { return FREQUENCY; }

    public static boolean getVault() { return USE_VAULT; }

    public static Permission getPerms() { return perms; }

    public static String getPlaceholder() {return GET_PLACEHOLDER; }
}
