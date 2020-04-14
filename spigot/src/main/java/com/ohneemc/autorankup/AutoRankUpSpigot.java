package com.ohneemc.autorankup;

import com.ohneemc.autorankup.bungee.Messages;
import com.ohneemc.autorankup.checks.CanRankUp;
import com.ohneemc.autorankup.config.Config;
import com.ohneemc.autorankup.events.OnPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class AutoRankUpSpigot extends JavaPlugin implements Listener {
    //Public
    public static boolean debug = false;
    public static final Logger log = Logger.getLogger("Minecraft");
    public static final String CHANNEL = "autorank:rank";
    public static final String SUB_CHANNEL = "rankup";

    // Private
    private static boolean BUNGEE_BROADCAST;
    private static boolean PLAYER_JOIN;
    private static boolean BROADCAST_ENB;
    private static String BROADCAST_MSG;
    private static boolean PLAYER_ENB;
    private static String PLAYER_MSG;
    private static int FREQUENCY;

    private static AutoRankUpSpigot autoRankUpSpigot;

    @Override
    public void onEnable() {
        // Saving and reloading config file
        saveDefaultConfig();
        reloadConfig();

        // Filling values
        BUNGEE_BROADCAST = Config.getBoolean("bungee.broadcast");
        PLAYER_JOIN = Config.getBoolean("settings.login");
        BROADCAST_ENB = Config.getBoolean("settings.broadcast");
        BROADCAST_MSG = Config.getString("settings.broadcastmessage");
        PLAYER_ENB = Config.getBoolean("settings.player");
        PLAYER_MSG = Config.getString("settings.playermessage");
        FREQUENCY = Config.getInteger("settings.frequency");


        // Messages - Registering channels
        if (getBungeeBroadcast()){
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
            this.getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, new Messages());
        }

        // Checking for plugins.
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getPluginManager().registerEvents(this, this);
        } else {
            throw new RuntimeException("Could not find PlaceholderAPI!! Plugin can not work without it!");
        }
        if (Bukkit.getPluginManager().getPlugin("Plan") == null) {
            throw new RuntimeException("Could not find Plan!! Plugin can not work without it!");
        }

        // Check on player join
        if (getPlayerJoin())
            getServer().getPluginManager().registerEvents(new OnPlayer(), this);

        // Initiate task
        task();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /***
     * Initiates the task
     */
    private void task() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                    for (Player player : Bukkit.getOnlinePlayers()){
                        CanRankUp.rankUp(player);
                    }
                }, 120L, (long) getFREQUENCY() * 1200L
        );
    }

    public static AutoRankUpSpigot getAutoRankUpSpigot(){ return autoRankUpSpigot; }

    public static boolean getBungeeBroadcast(){ return BUNGEE_BROADCAST; }

    public static boolean getPlayerJoin(){ return PLAYER_JOIN; }

    public static boolean getBroadcastEnabled(){ return BROADCAST_ENB; }

    public static String getBroadcastMsg(){ return BROADCAST_MSG; }

    public static boolean getPlayerEnb(){ return PLAYER_ENB; }

    public static String getPlayerMsg() { return PLAYER_MSG; }

    public static int getFREQUENCY() { return FREQUENCY; }
}
