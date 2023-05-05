package com.ykn.servermanager.command;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.ykn.servermanager.util.ConfigReader;
import com.ykn.servermanager.util.Data;

public class SMCommandTab implements TabCompleter {
    //servermanager broadcast <位置>
    private List<String> cmdBroadcast(CommandSender sender, Command command, String label, String[] args) {
        List<String> result = new LinkedList<String>();
        if (args.length == 2) {
            if (ConfigReader.getBoolean("command.broadcast.bossbar.active")) {
                result.add("bossbar");
            }
            if (ConfigReader.getBoolean("command.broadcast.chat.active")) {
                result.add("chat");
            }
            return result;
        } else if (args[1].equalsIgnoreCase("bossbar")) {
            //servermanager broadcast bossbar
            return cmdBroadcastBossbar(sender, command, label, args);
        } else if (args[1].equalsIgnoreCase("chat")) {
            //servermanager broadcast chat
            return null;
        }

        return null;
    }

    //servermanager broadcast bossbar <tick> <hasPerfix> <isForce> <Message>
    //servermanager broadcast bossbar clear
    private List<String> cmdBroadcastBossbar(CommandSender sender, Command command, String label, String[] args) {
        List<String> result = new LinkedList<String>();
        if (args.length == 3) {
            result.add("clear");
            result.add("200");
            result.add("600");
            result.add("1200");
            result.add("2400");
            result.add("6000");
            return result;
        }

        if (args.length == 4 || args.length == 5) {
            if (args[2].equalsIgnoreCase("clear")) {
                return null;
            }
            result.add("true");
            result.add("false");
            return result;
        }

        if (args.length == 6) {
            return null;
        }

        return null;
    }

    //指令servermanager home <操作> <名称>
    //servermanager home add
    //servermanager home set
    //servermanager home remove
    //servermanager home list
    //servermanager home get
    //servermanager home tp|teleport
    //servermanager home share
    private List<String> cmdHome(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 2) {
                List<String> result = new LinkedList<String>();
                result.add("add");
                result.add("set");
                result.add("remove");
                result.add("list");
                result.add("get");
                result.add("tp");
                result.add("teleport");
                result.add("share");
                return result;
            } else if (args.length == 3) {
                if (args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("remove") || 
                args[1].equalsIgnoreCase("get") || args[1].equalsIgnoreCase("tp") || 
                args[1].equalsIgnoreCase("teleport") || args[1].equalsIgnoreCase("share")) {
                    Player player = (Player) sender;
                    List<String> result = new LinkedList<String>();
                    Set<String> homeList = Data.playerHomeData.get(player).keySet();
                    for (String homeName : homeList) {
                        result.add(homeName);
                    }

                    return result;
                }
            }
        }

        return null;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> result = new LinkedList<String>();
        if (args.length == 1) {
            if (ConfigReader.getBoolean("command.broadcast.active")) {
                result.add("broadcast");
            }
            if (ConfigReader.getBoolean("command.home.active") && (sender instanceof Player)) {
                result.add("home");
            }
            if (ConfigReader.getBoolean("command.back.active") && (sender instanceof Player)) {
                result.add("back");
            }

            return result;
        }

        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("broadcast")) {
                return this.cmdBroadcast(sender, command, label, args);
            }
            if (args[0].equalsIgnoreCase("home")) {
                return this.cmdHome(sender, command, label, args);
            }
            if (args[0].equalsIgnoreCase("back")) {
                return null;
            }
        }

        return null;
    }
}
