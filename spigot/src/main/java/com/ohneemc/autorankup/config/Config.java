package com.ohneemc.autorankup.config;

import com.ohneemc.autorankup.AutoRankUpSpigot;
import org.bukkit.configuration.ConfigurationSection;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Config {

    /**
     * Report an error in reading the configuration
     * @param e Exception generated from reading configuration
     */
    private static void error(Exception e){
        if (AutoRankUpSpigot.DEBUG)
            AutoRankUpSpigot.log.log(Level.SEVERE, MessageFormat.format("Something went wrong.. {0}", e));
    }

    /**
     * Fetches a boolean from the configuration
     * @param location Configuration location of the boolean
     */
    public static boolean getBoolean(String location){
        try {return AutoRankUpSpigot.getAutoRankUpSpigot().getConfig().getBoolean(location);}
        catch (Exception e) {
            error(e);
            return false;
        }
    }

    /**
     * Fetches an integer from the configuration
     * @param location Configuration location of the integer
     */
    public static int getInteger(String location){
        try {return AutoRankUpSpigot.getAutoRankUpSpigot().getConfig().getInt(location);}
        catch (Exception e) {
            error(e);
            return 0;
        }
    }

    /**
     * Fetches a string from the configuration
     * @param location Configuration location of the string
     */
    public static String getString(String location) {
        try {return AutoRankUpSpigot.getAutoRankUpSpigot().getConfig().getString(location);}
        catch (Exception e) {
            error(e);
            return "";
        }
    }

    /**
     * Fetches a double from the configuration
     * @param location Configuration location of the double
     */
    public static double getDouble(String location) {
        try {return AutoRankUpSpigot.getAutoRankUpSpigot().getConfig().getDouble(location);}
        catch (Exception e) {
            error(e);
            return 0.0;
        }
    }

    /**
     * Fetches a double from the configuration
     * @param location Configuration location of the double
     */
    public static List<String> getList(String location){
        try {return AutoRankUpSpigot.getAutoRankUpSpigot().getConfig().getStringList(location);}
        catch (Exception e) {
            error(e);
            return new ArrayList<>();
        }
    }

    public static ConfigurationSection getSection(String section){
        try {return AutoRankUpSpigot.getAutoRankUpSpigot().getConfig().getConfigurationSection(section);}
        catch (Exception e) {
            error(e);
            return null;
        }
    }

    public static boolean reloadConfig(){
        try {
            AutoRankUpSpigot.getAutoRankUpSpigot().reloadConfig();
            return true;
        }catch (Exception e) {
            error(e);
            return false;
        }
    }
}
