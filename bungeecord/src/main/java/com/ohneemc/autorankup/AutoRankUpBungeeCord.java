package com.ohneemc.autorankup;

import net.md_5.bungee.api.plugin.Plugin;

public final class AutoRankUpBungeeCord extends Plugin {
    static AutoRankUpBungeeCord autoRankUpBungeeCord;

    static String CHANNEL = "autorank:rank";
    static String SUB_CHANNEL = "rankup";

    @Override
    public void onEnable() {
        autoRankUpBungeeCord = this;
        getProxy().registerChannel(CHANNEL);
        getLogger().info("Plugin enabled!");

        // Register event
        getProxy().getPluginManager().registerListener(this, new PluginMsg());
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }
}
