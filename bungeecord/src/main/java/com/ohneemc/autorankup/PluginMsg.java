package com.ohneemc.autorankup;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Collection;

import static com.ohneemc.autorankup.AutoRankUpBungeeCord.*;

public class PluginMsg implements Listener {

    @EventHandler
    public void on(PluginMessageEvent event) {
        if (!event.getTag().equalsIgnoreCase("BungeeCord")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subchannel = in.readUTF();

        if (subchannel.equalsIgnoreCase(CHANNEL)){
            short len = in.readShort();
            byte[] msgbytes = new byte[len];
            in.readFully(msgbytes);

            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
            try {
                String msg = msgin.readUTF();
                autoRankUpBungeeCord.getLogger().info("Received msg: " + msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*
        if (event.getReceiver() instanceof ProxiedPlayer) {
            ProxiedPlayer receiver = (ProxiedPlayer) event.getReceiver();
            autoRankUpBungeeCord.getLogger().info("Received msg from: " + receiver.getName());
            autoRankUpBungeeCord.getLogger().info(msg);
            sendCustomData(receiver, msg, in.readShort());
        }

        if (event.getReceiver() instanceof Server) {
            Server receiver = (Server) event.getReceiver();
            autoRankUpBungeeCord.getLogger().info("Received msg from: " + receiver.getInfo().getName());
            autoRankUpBungeeCord.getLogger().info(msg);

        }
         */
    }

    public void sendCustomData(ProxiedPlayer player, String data1, int data2) {
        Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();
        // perform a check to see if globally are no players
        if (networkPlayers == null || networkPlayers.isEmpty()) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SUB_CHANNEL); // the channel could be whatever you want
        out.writeUTF(data1); // this data could be whatever you want
        out.writeInt(data2); // this data could be whatever you want

        // we send the data to the server
        // using ServerInfo the packet is being queued if there are no players in the server
        // using only the server to send data the packet will be lost if no players are in it
        player.getServer().getInfo().sendData( CHANNEL, out.toByteArray() );


        //player.getServer().getInfo().sendData( CHANNEL, out.toByteArray() );
        //server.sendData(CHANNEL, out.toByteArray());
        //autoRankUpBungeeCord.getProxy().getServerInfo(autoRankUpBungeeCord.getProxy()
        //        .getName()).sendData(CHANNEL, out.toByteArray());
        //autoRankUpBungeeCord.getProxy().getServerInfo("ALL").sendData(CHANNEL, out.toByteArray());
    }
}
