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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;

import static com.ohneemc.autorankup.AutoRankUpSpigot.*;
import static org.bukkit.Bukkit.getServer;


public class Messages implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if ( !channel.equalsIgnoreCase(CHANNEL) )
        {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput( message );
        String subChannel = in.readUTF();
        if ( subChannel.equalsIgnoreCase( SUB_CHANNEL ) )
        {
            String msg = in.readUTF();
            int sh = in.readInt();

            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }

    public static void sendBroadcast(String msg) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF(CHANNEL);
        byte[] data = out.toByteArray();

        new BukkitRunnable() {
            @Override
            public void run() {
                Collection<? extends Player> players = getServer().getOnlinePlayers();
                ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
                DataOutputStream msgout = new DataOutputStream(msgbytes);

                Player p = Iterables.getFirst(players, null);
                if (p == null) {
                    return;
                }
                //Bukkit.getLogger().log(Level.INFO, "Sent to: " + CHANNEL + " : " + msg);
                //p.sendPluginMessage(getAutoRankUpSpigot(), CHANNEL, data);

                try {
                    msgout.writeUTF(msg); // You can do anything you want with msgout
                    msgout.writeShort(32);
                } catch (IOException exception){
                    exception.printStackTrace();
                }

                out.writeShort(msgbytes.toByteArray().length);
                out.write(msgbytes.toByteArray());

                p.sendPluginMessage(getAutoRankUpSpigot(), CHANNEL, out.toByteArray());

                cancel();
            }
        }.runTaskTimer(getAutoRankUpSpigot(), 40L, 100L);
    }
}
