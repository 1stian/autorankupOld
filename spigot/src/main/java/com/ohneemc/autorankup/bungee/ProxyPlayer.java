package com.ohneemc.autorankup.bungee;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.ohneemc.autorankup.AutoRankUpSpigot.getAutoRankUpSpigot;

public class ProxyPlayer implements PluginMessageListener {

public static List<String> ProxiedPlayer = new ArrayList<>();

    private static void askProxyPlayers(){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerList");
        out.writeUTF("ALL");

        // If you don't care about the player
        // Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        // Else, specify them
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        Player p = Iterables.getFirst(players, null);

        p.sendPluginMessage(getAutoRankUpSpigot(), "BungeeCord", out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("PlayerList")) {
            //String server = in.readUTF(); // The name of the server you got the player list of, as given in args.
            String[] playerList = in.readUTF().split(", ");

            ProxiedPlayer.addAll(Arrays.asList(playerList));
        }
    }
}
