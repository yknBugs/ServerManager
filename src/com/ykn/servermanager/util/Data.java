package com.ykn.servermanager.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.ykn.servermanager.display.Announcement;

public class Data {
    private static int tickTime = 0;
    //计划执行指令，用于在非主线程执行指令的需求
    public static boolean hasScheduledCommand = false;
    public static List<String> scheduledCommandList = new LinkedList<String>();
    //限制实体数量相关变量
    public static int broadcastEntityCountCD = 6000;
    public static int entityCountBroadcastUid = 0;
    public static int clearEntityCountDown = 0; 
    public static int entityCountBeforeClear = 0;
    public static int entityCountAfterClear = 0;
    //射中实体提示相关变量
    public static int projectileHitCooldown = 0;
    public static LivingEntity projectileHitEntity = null;
    //玩家挂机检测相关变量
    public static Map<Player, Integer> playerIdleTimeout = new HashMap<Player, Integer>();
    public static Map<Player, Integer> playerLastYaw = new HashMap<Player, Integer>();
    public static Map<Player, Integer> playerCurrentYaw = new HashMap<Player, Integer>();
    //玩家上次被怪攻击间隔
    public static Map<Player, Integer> playerHurtTime = new HashMap<Player, Integer>();
    //通知相关变量
    public static Map<String, Integer> announcementCooldown = new HashMap<String, Integer>();
    //玩家设置的家
    public static Map<Player, Map<String, Location>> playerHomeData = new HashMap<Player, Map<String, Location>>();
    public static Map<Player, Location> playerLastLocation = new HashMap<Player, Location>();

    public static void reset() {
        broadcastEntityCountCD = 6000;
        entityCountBroadcastUid = 0;
        clearEntityCountDown = 0; 
        entityCountBeforeClear = 0;
        entityCountAfterClear = 0;
        projectileHitCooldown = 0;
        projectileHitEntity = null;
        playerIdleTimeout.clear();
        playerLastYaw.clear();
        playerCurrentYaw.clear();
        playerHurtTime.clear();
        announcementCooldown.clear();
    }

    public static void freeMemory() {
        Map<Player, Integer> tempMap = new HashMap<Player, Integer>();
        List<Player> playerList = Util.getOnlinePlayers();

        for (Player player : playerList) {
            if (playerIdleTimeout.containsKey(player)) {
                tempMap.put(player, playerIdleTimeout.get(player));
            }
        }
        playerIdleTimeout.clear();
        playerIdleTimeout.putAll(tempMap);

        tempMap.clear();
        for (Player player : playerList) {
            if (playerLastYaw.containsKey(player)) {
                tempMap.put(player, playerLastYaw.get(player));
            }
        }
        playerLastYaw.clear();
        playerLastYaw.putAll(tempMap);

        tempMap.clear();
        for (Player player : playerList) {
            if (playerCurrentYaw.containsKey(player)) {
                tempMap.put(player, playerCurrentYaw.get(player));
            }
        }
        playerCurrentYaw.clear();
        playerCurrentYaw.putAll(tempMap);

        tempMap.clear();
        for (Player player : playerList) {
            if (playerHurtTime.containsKey(player)) {
                tempMap.put(player, playerHurtTime.get(player));
            }
        }
        playerHurtTime.clear();
        playerHurtTime.putAll(tempMap);
    }

    public static void tick() {
        tickTime++;
        if (broadcastEntityCountCD > 0) {
            broadcastEntityCountCD--;
        }
        if (clearEntityCountDown > 0) {
            clearEntityCountDown--;
        }
        if (projectileHitCooldown > 0) {
            projectileHitCooldown--;
        }

        List<Player> playerList = Util.getOnlinePlayers();
        for (Player player : playerList) {
            if (playerIdleTimeout.containsKey(player)) {
                playerIdleTimeout.put(player, playerIdleTimeout.get(player) + 1);
            } else {
                playerIdleTimeout.put(player, 0);
            }

            if (playerHurtTime.containsKey(player)) {
                playerHurtTime.put(player, playerHurtTime.get(player) + 1);
            } else {
                playerHurtTime.put(player, 0);
            }

            if (playerHomeData.containsKey(player) == false) {
                playerHomeData.put(player, new HashMap<String, Location>());
            }
            if (playerLastLocation.containsKey(player) == false) {
                playerLastLocation.put(player, player.getLocation());
            }
        }

        Announcement.tick();

        if (tickTime % 72000 == 0) {
            freeMemory();
        }
    }
    
}
