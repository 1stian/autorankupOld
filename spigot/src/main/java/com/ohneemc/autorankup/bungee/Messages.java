package com.ohneemc.autorankup.bungee;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.Collection;

import static com.ohneemc.autorankup.AutoRankUpSpigot.*;


public class Messages implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equalsIgnoreCase("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        if (subchannel.equalsIgnoreCase(CHANNEL)){
            short len = in.readShort();
            byte[] msgbytes = new byte[len];
            in.readFully(msgbytes);

            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
            try {
                String msg = msgin.readUTF();
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     *
     * @param msg Forward a message to bungee network.
     */
    public static void sendBroadcast(String msg) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF(CHANNEL);

        new BukkitRunnable() {
            @Override
            public void run() {
                ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
                DataOutputStream msgout = new DataOutputStream(msgbytes);
                try{
                    msgout.writeUTF(msg);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                out.writeShort(msgbytes.toByteArray().length);
                out.write(msgbytes.toByteArray());
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                Player p = Iterables.getFirst(players, null);
                if (p == null){
                    return;
                }
                p.sendPluginMessage(getAutoRankUpSpigot(), "BungeeCord", out.toByteArray());
                cancel();
            }
        }.runTaskTimer(getAutoRankUpSpigot(), 40L, 100L);
    }
}
