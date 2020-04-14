package com.ohneemc.autorankup.bungee;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

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

            Bukkit.broadcastMessage(msg);
        }
    }

    public static void sendBroadcast(String msg) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SUB_CHANNEL);
        out.writeUTF(msg);
        byte[] data = out.toByteArray();

        new BukkitRunnable() {
            @Override
            public void run() {
                Collection<? extends Player> players = getServer().getOnlinePlayers();
                Player p = Iterables.getFirst(players, null);
                if (p == null) {
                    return;
                }
                Bukkit.getLogger().log(Level.INFO, "Sent to: " + CHANNEL + " : " + msg);
                p.sendPluginMessage(getAutoRankUpSpigot(), CHANNEL, data);

                cancel();
            }
        }.runTaskTimer(getAutoRankUpSpigot(), 1L, 100L);
    }
}
