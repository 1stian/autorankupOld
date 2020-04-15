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
import static org.bukkit.Bukkit.getServer;


public class Messages implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equalsIgnoreCase(CHANNEL)) {
            return;
        }


        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String subchannel = in.readUTF();
            if (subchannel.equals(SUB_CHANNEL)) {
                short len = in.readShort();
                byte[] data = new byte[len];
                in.readFully(data);

                //bytearray to string
                String s = new String(data);

                Bukkit.broadcastMessage("SUB");
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        //ByteArrayDataInput in = ByteStreams.newDataInput( message );
        //String subChannel = in.readUTF();
        //if ( subChannel.equalsIgnoreCase( SUB_CHANNEL ) )
        //{
        //   String msg = in.readUTF();
        //    int sh = in.readInt();

        //    Bukkit.broadcastMessage("SUB");
        //   Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
        //}
    }

    public static void forwardString(String subChannel, String target, String s) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                try {
                    out.writeUTF("Forward");
                    out.writeUTF(target);
                    out.writeUTF(subChannel); // "customchannel" for example
                    byte[] data = s.getBytes();
                    out.writeShort(data.length);
                    out.write(data);


                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                Collection<? extends Player> players = getServer().getOnlinePlayers();
                Player p = Iterables.getFirst(players, null);

                p.sendPluginMessage(getAutoRankUpSpigot(), CHANNEL, b.toByteArray());
                cancel();
            }
        }.runTaskTimer(getAutoRankUpSpigot(), 40L, 100L);
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
                } catch (IOException exception) {
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
