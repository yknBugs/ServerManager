package com.ykn.servermanager.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Util {
    public static String addPerfix(boolean hasPerfix, String perfix, String text) {
        if (hasPerfix) {
            return perfix + text;
        }
        return text;
    }

    public static boolean executeCommand(String command) {
        return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public static void scheduleCommand(String command) {
        Data.hasScheduledCommand = true;
        Data.scheduledCommandList.add(command);
    }

    public static List<Player> getOnlinePlayers() {
        Collection<? extends Player> p = Bukkit.getOnlinePlayers();
        List<Player> players = new ArrayList<Player>();
        for (Player player : p) {
            players.add(player);
        }
        return players;
    }

    public static void sendCmdMessage(CommandSender sender, boolean hasPerfix, String message) {
        String perfixText = ConfigReader.getString("perfix.text").replace('&', '\u00a7');
        String logPerfix = ConfigReader.getString("debugMsg.perfix");

        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(addPerfix(hasPerfix, perfixText, message));
        } else if (sender instanceof CommandBlock) {
            return;
        } else {
            sender.sendMessage(addPerfix(hasPerfix, logPerfix, message));
        }
    }

    public static int getEntityCount() {
        List<World> worldList = Bukkit.getWorlds();
        int entityCount = 0;
        for (World world : worldList) {
            entityCount += world.getEntities().size();
        }
        return entityCount;
    }
}
