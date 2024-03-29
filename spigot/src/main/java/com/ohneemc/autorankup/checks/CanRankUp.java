package com.ohneemc.autorankup.checks;

import com.ohneemc.autorankup.AutoRankUpSpigot;
import com.ohneemc.autorankup.bungee.Messages;
import com.ohneemc.autorankup.config.Config;
import com.ohneemc.autorankup.helpers.Enums;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ohneemc.autorankup.AutoRankUpSpigot.*;

public class CanRankUp {
    private static final Logger log = Logger.getLogger("Minecraft");

    // List
    private static HashMap<String, String> rankTime = new HashMap<>();
    private static HashMap<String, String> rankTo = new HashMap<>();

    // Const
    private static String playerTag = "{player}";
    private static String groupTag = "{group}";
    private static String groupToTag = "{rank_to}";
    /***
     *
     * @param player The player to rank up
     */
    public static void rankUp(Player player){
        // Getting current group of player.
        String playerGroup = null;
        if (!getVault() && !getPex()){
            String groupPlaceholder = getPlaceholder();
            if (groupPlaceholder == null){
                log.info("[AutoRankUp] - No placeholder is specified.. Can't do anything. Using vault?");
                return;
            }
            playerGroup = PlaceholderAPI.setPlaceholders(player, groupPlaceholder);
        }else{
            if (!getPex())
                playerGroup = getPerms().getPrimaryGroup(player);
        }

        if (getPex()) {
            PermissionUser user = PermissionsEx.getUser(player);
            playerGroup = PlaceholderAPI.setPlaceholders(player, "%vault_rank%");
        }

        // Checking first if true, proceed with rank up.
        if (rankChecker(player, playerGroup)){
            if (getLogToConsole())
                log.info("[AutoRankUp] - " + player.getName() + " ranked up!");
            // What group the player should rank up to
            String toRank = rankTo.get(playerGroup);

            if (getVault() && !getPex()){
                if (rankUpVault(player, toRank)){
                    sendMessages(player,toRank);
                    if (getLogToConsole())
                        log.log(Level.INFO, "[AutoRankUp] - Rankup successful - VAULT");
                }else{
                    log.warning("[AutoRankUp] - Something wrong happened while ranking "
                            + player.getName() + " up with vault..");
                }
                return;
            }

            if (getPex()){
                PermissionUser user = PermissionsEx.getUser(player);
                List<String> pexList = new ArrayList<>();
                pexList.add(toRank);
                if (rankUpPex(user, pexList)){
                    sendMessages(player,toRank);
                    if (getLogToConsole())
                        log.log(Level.INFO, "[AutoRankUp] - Rankup successful - PEX");
                }else{
                    log.warning("[AutoRankUp] - Something wrong happened while ranking "
                            + player.getName() + " up with PEX..");
                }
                return;
            }

            // Getting command to execute on rank up
            String rawCommand = Config.getString("ranks."+playerGroup+".command");

            if (rawCommand == null){
                log.log(Level.INFO, "[AutoRankUp] - Command section is empty for: {0}", playerGroup);
                return;
            }

            String replacePlayer = rawCommand.replace(playerTag, player.getName());
            String command = replacePlayer.replace(groupTag, toRank);

            ConsoleCommandSender console = Bukkit.getConsoleSender();
            if (Bukkit.dispatchCommand(console, command)){
                if (getLogToConsole())
                    log.log(Level.INFO, "[AutoRankUp] - Command successful");

                // Checks if the player is eligible for the next one as well.
                // This is here so if a player has more time then more than one rank.
                if (rankChecker(player, toRank)){
                    rankUp(player);
                    return;
                }
                sendMessages(player, toRank);
            }else{
                log.log(Level.SEVERE, "[AutoRankUp] - Command failed");
            }
        }
    }

    /***
     *
     * @param player Which player to rank up
     * @param newGroup What group the player should rank to.
     * @return True or false
     */
    private static boolean rankUpVault(Player player, String newGroup){
        try {
            getPerms().playerAddGroup(player, newGroup);
            return true;
        }catch (Exception e){
            log.warning("[AutoRankUp] : ERROR! " + e);
            return false;
        }
    }

    private static boolean rankUpPex(PermissionUser player, List<String> newGroup){
        try {
            //player.addGroup(newGroup);
            player.setParentsIdentifier(newGroup, null);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    /***
     *
     * @param player The player who ranked up
     * @param toRank What rank the player ranked up to
     */
    private static void sendMessages(Player player, String toRank){
        if (getPlayerEnb()){
            String rPlayer = AutoRankUpSpigot.getPlayerMsg().replace(playerTag, player.getName());
            String rGroup = rPlayer.replace(groupToTag, toRank);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', rGroup));
        }
        if (getBroadcastEnabled() && !getBungeeBroadcast()){
            String rPlayer = AutoRankUpSpigot.getBroadcastMsg().replace(playerTag, player.getName());
            String rGroup = rPlayer.replace(groupToTag, toRank);

            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', rGroup));
        }

        if (getBungeeBroadcast()){
            String rPlayer = AutoRankUpSpigot.getBroadcastMsg().replace(playerTag, player.getName());
            String rGroup = rPlayer.replace(groupToTag, toRank);

            Messages.sendBroadcast(rGroup);
        }
    }

    /***
     *
     * @param player The player to check
     * @return True if eligible or false if not
     */
    private static boolean rankChecker(Player player, String group){
        // Grabbing ranks and time
        grabRankUps();
        if (!rankTo.containsKey(group)){
            return false;
        }
        // Getting how long the player has been active for
        String activeTime = getType(player, Enums.PlanType.ACTIVE);
        // Getting the time required for rank up
        String timeToRank = rankTime.get(group);

        if (timeToRank == null || activeTime == null){
            return false;
        }

        // Strings with only numbers
        String strippedActive = activeTime.toLowerCase().replaceAll("([a-z])", "");
        String strippedTo = timeToRank.toLowerCase().replaceAll("([a-z])", "");

        // Splitting the numbers
        String[] splitActive = strippedActive.split(" ");
        String[] splitTo = strippedTo.split(" ");

        boolean canRankUp = false;

        // Active play time the player has.
        int pDay;
        int pHour;
        int pMin;

        // Trying to parse ints, if not, it will be 0.
        try {
            if (activeTime.contains("d")){
                pDay = Integer.parseInt(splitActive[0]);
            }else{
                pDay = 0;
            }
        }catch (Exception ex){
            pDay = 0;
        }
        try {
            if (activeTime.contains("h") && splitActive.length > 2){
                pHour = Integer.parseInt(splitActive[1]);
            }else if (activeTime.contains("h")){
                pHour = Integer.parseInt(splitActive[0]);
            }else{
                pHour = 0;
            }
        }catch (Exception ex){
            pHour = 0;
        }
        try {
            if (activeTime.contains("m") && splitActive.length > 3) {
                pMin = Integer.parseInt(splitActive[2]);
            }else if(activeTime.contains("m")){
                pMin = Integer.parseInt(splitActive[0]);
            }else{
                pMin = 0;
            }
        }catch (Exception ex){
            pMin = 0;
        }


        // Values needed to rank up!
        int toDay;
        int toHour;
        int toMin;
        try{
            toDay = Integer.parseInt(splitTo[0]);
            toHour = Integer.parseInt(splitTo[1]);
            toMin = Integer.parseInt(splitTo[2]);
        }catch (Exception ex){
            log.log(Level.WARNING, "[AutoRankUp] Could not parse time from config. Is the formatting correct? 0d 0h 0m");
            return false;
        }

        // If day has passed -> rank up
        if (pDay > toDay){
            canRankUp = true;
        }

        // If the day is the same as the rank up day
        if (pDay == toDay){
            // If hour has passed -> rank up
            if (pHour > toHour){
                canRankUp = true;
            }
            // If the hours is the same as the rank up day
            if (pHour == toHour){
                // If the minute has passed -> rank up
                if (pMin > toMin){
                    canRankUp = true;
                }
                // If the minute is the same as the rank up day -> rank up
                if (pMin == toMin){
                    canRankUp = true;
                }
            }
        }
        // If true, the player ranks up!
        return canRankUp;
    }

    /***
     *
     * @param player What player to grab data from
     * @param planType What type of data you want
     * @return A string with the data you requested
     */
    private static String getType(Player player, Enums.PlanType planType){
        String plan;
        switch (planType) {
            case ACTIVE:
                plan = PlaceholderAPI.setPlaceholders(player, "%plan_player_time_active%");
                break;
            case AFK:
                plan = PlaceholderAPI.setPlaceholders(player, "%plan_player_time_afk%");
                break;
            case TOTAL:
                plan = PlaceholderAPI.setPlaceholders(player, "%plan_player_time_total%");
                break;
                default: return null;
        }
        return plan;
    }

    /***
     * Grabbing ranks and how long it will take to reach of them
     */
    private static void grabRankUps(){
        ConfigurationSection section = Config.getSection("ranks");
        if (section == null){
            Bukkit.getLogger().log(Level.SEVERE, "Could not grab ranks from config.. Please check your config.");
            return;
        }

        for (String ranks : section.getKeys(false)){
            rankTime.put(ranks, Config.getString("ranks."+ranks+".time"));
            rankTo.put(ranks, Config.getString("ranks."+ranks+".to"));
        }
    }
}
