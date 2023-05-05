package com.ykn.servermanager.display;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class Bossbar {
    private static int uid = 1;
    private static BossBar current;
    private static boolean hasBossbar = false;
    private static boolean forceFinish = false;
    private static int currentMax = 200;
    private static int currentValue = 0;

    //强制创建boss栏消息，该消息无法被除了强制创建的boss栏消息以外的消息覆盖
    public static void forceCreate(String title, BarColor color, BarStyle style, int max, int value) {
        if (hasBossbar) {
            current.removeAll();
        }
        current = Bukkit.createBossBar(title, color, style);
        current.setProgress((double) value / (double) max);
        current.setVisible(true);
        currentMax = max;
        currentValue = value;
        hasBossbar = true;
        forceFinish = true;
        uid++;
    }
    
    //创建普通的boss栏消息，可被其它消息覆盖，有强制boss栏消息时无法创建
    public static boolean create(String title, BarColor color, BarStyle style, int max, int value) {
        if (forceFinish == false) {
            if (hasBossbar) {
                current.removeAll();
            }
            current = Bukkit.createBossBar(title, color, style);
            current.setProgress((double) value / (double) max);
            current.setVisible(true);
            currentMax = max;
            currentValue = value;
            hasBossbar = true;
            uid++;
            return true;
        }
        return false;
    }

    //创建boss栏消息，仅当当前没有任何boss栏消息时候才能创建成功
    public static boolean lightCreate(String title, BarColor color, BarStyle style, int max, int value) {
        if (forceFinish == false && hasBossbar == false) {
            current = Bukkit.createBossBar(title, color, style);
            current.setProgress((double) value / (double) max);
            current.setVisible(true);
            currentMax = max;
            currentValue = value;
            hasBossbar = true;
            uid++;
            return true;
        }
        return false;
    }

    //设置消息向哪些玩家展示
    public static void show(List<Player> players) {
        current.removeAll();
        for (Player player : players) {
            current.addPlayer(player);
        }
    }

    public static void show(Player player) {
        current.removeAll();
        current.addPlayer(player);
    } 

    //每游戏刻调用一次该函数
    public static boolean tick() {
        if (currentValue > 0) {
            currentValue--;
            current.setProgress((double) currentValue / (double) currentMax);
            return true;
        } else if (hasBossbar == true) {
            current.removeAll();
            hasBossbar = false;
            forceFinish = false;
        }
        return false;
    }

    //设置标题，必须验证uid后才能设置成功
    public static boolean setTitle(int confirmUid, String title) {
        if (confirmUid == uid && hasBossbar) {
            current.setTitle(title);
            return true;
        }
        return false;
    }

    //强制设置标题
    public static boolean forceSetTitle(String title) {
        if (hasBossbar) {
            current.setTitle(title);
            return true;
        }
        return false;
    }

    //设置当前数值，无法设置强制boss栏消息的数值
    public static boolean setValue(int value) {
        if (forceFinish) {
            return false;
        }
        if (value > currentMax) {
            return false;
        }
        if (hasBossbar == false) {
            return false;
        }
        currentValue = value;
        return true;
    }

    //强制设置当前数值
    public static boolean forceSetValue(int value) {
        if (value > currentMax) {
            return false;
        }
        if (hasBossbar == false) {
            return false;
        }
        currentValue = value;
        return true;
    }

    //设置最大数值
    public static boolean setMax(int max) {
        if (forceFinish) {
            return false;
        }
        if (max < currentValue) {
            return false;
        }
        currentMax = max;
        return true;
    }

    //强制设置最大数值
    public static boolean forceSetMax(int max) {
        if (max < currentValue) {
            return false;
        }
        currentMax = max;
        return true;
    }

    //重回最大值
    public static boolean resetValue() {
        if (forceFinish) {
            return false;
        }
        currentValue = currentMax;
        return true;
    }

    //强制重回最大值
    public static boolean forceResetValue() {
        currentValue = currentMax;
        return true;
    }

    //强制移除boss栏消息
    public static boolean forceRemove() {
        if (hasBossbar) {
            current.removeAll();
            hasBossbar = false;
            forceFinish = false;
            return true;
        }
        return false;
    }

    //移除boss栏消息，无法移除强制消息
    public static boolean remove() {
        if (hasBossbar == true && forceFinish == false) {
            current.removeAll();
            hasBossbar = false;
            return true;
        }
        return false;
    }

    public static int getUid() {
        return uid;
    }

    public static BossBar getCurrent() {
        return current;
    }

    public static boolean getHasBossbar() {
        return hasBossbar;
    }

    public static boolean getForceFinish() {
        return forceFinish;
    }

    public static int getCurrentMax() {
        return currentMax;
    }

    public static int getCurrentValue() {
        return currentValue;
    }

    public static double getCurrentProgress() {
        return (double) currentValue / (double) currentMax;
    }
}
