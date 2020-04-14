package com.ohneemc.autorankup;

import net.md_5.bungee.api.plugin.Plugin;

public final class AutoRankUpBungeeCord extends Plugin {

    @Override
    public void onEnable() {
        getProxy().registerChannel("autorank:rank");
        getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }
}
