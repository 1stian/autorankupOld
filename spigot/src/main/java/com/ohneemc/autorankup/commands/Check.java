package com.ohneemc.autorankup.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Check implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("timecheck")){
            if (args.length < 1){
                sender.sendMessage("You must give me a name to check... /timecheck <name>");
            }else{
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null){
                    return false;
                }
                sender.sendMessage(checkPlayer(target));
                return true;
            }
        }
        return false;
    }

    /***
     *
     * @param player What player to grab data from
     * @return A string with total time, active time and afk time
     */
    private String checkPlayer(Player player){
        String plan;
        String total = "%plan_player_time_total%";
        String active = "%plan_player_time_active%";
        String afk = "%plan_player_time_afk%";

        plan = ChatColor.GREEN + "Player: " + ChatColor.AQUA + player.getName() + ChatColor.GREEN + " Total: "
                + ChatColor.AQUA + total + ChatColor.GREEN + " Active: " + ChatColor.AQUA + active + ChatColor.GREEN
                + " Afk " + ChatColor.AQUA + afk;

        plan = PlaceholderAPI.setPlaceholders(player, plan);

        return plan;
    }
}
