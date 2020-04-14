package com.ohneemc.autorankup.events;

import com.ohneemc.autorankup.checks.CanRankUp;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayer implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CanRankUp canRankUp = new CanRankUp();
        canRankUp.rankUp(player);
    }
}
