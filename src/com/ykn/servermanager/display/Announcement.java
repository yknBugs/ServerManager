package com.ykn.servermanager.display;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import com.ykn.servermanager.util.ConfigReader;
import com.ykn.servermanager.util.Data;
import com.ykn.servermanager.util.Util;

public class Announcement {
    private static int getCooldown(String announcement) {
        if (Data.announcementCooldown.containsKey(announcement)) {
            return Data.announcementCooldown.get(announcement);
        } else {
            return 0;
        }
    }

    private static void setCooldown(String announcement) {
        int minCd = ConfigReader.getInt("announcement." + announcement + ".cooldownMin");
        int maxCd = ConfigReader.getInt("announcement." + announcement + ".cooldownMax");
        final int finalCd = (int) (Math.random() * (maxCd - minCd) + minCd);
        Data.announcementCooldown.put(announcement, finalCd);
        if (ConfigReader.getBoolean("debugMsg.info.active") && ConfigReader.getBoolean("function.announcement.active")) {
            String info = ConfigReader.getString("debugMsg.info.announcementCd");
            info = info.replaceAll("#1", announcement);
            info = info.replaceAll("#2", Integer.toString(finalCd));
            ConfigReader.info(info);
        }        
    }

    private static void tickCooldown() {
        Set<String> announcementList = Data.announcementCooldown.keySet();
        for (String announcement : announcementList) {
            int getCd = Data.announcementCooldown.get(announcement);
            if (getCd > 0) {
                Data.announcementCooldown.put(announcement, getCd - 1);
            }
        }
    }

    private static boolean singleAnnounce(String announcement, int currentStack, int maxStack, boolean ignoreCooldown, boolean resetCooldown) {
        if (currentStack >= maxStack) {
            return false;
        }
        if (ignoreCooldown == false && getCooldown(announcement) > 0) {
            return true;
        }
        if (resetCooldown) {
            setCooldown(announcement);
        }
        
        if ("bossbar".equalsIgnoreCase(ConfigReader.getString("announcement." + announcement + ".type"))) {
            int exitTime = ConfigReader.getInt("announcement." + announcement + ".exitTime");
            String msg = new String();
            if (ConfigReader.getBoolean("announcement." + announcement + ".perfix")) {
                msg = ConfigReader.getString("perfix.text");
            }
            msg = msg + ConfigReader.getString("announcement." + announcement + ".text");
            msg = msg.replace('&', '\u00a7');

            boolean b = false;
            if (ConfigReader.getBoolean("announcement." + announcement + ".forceShow")) {
                Bossbar.forceCreate(msg, BarColor.WHITE, BarStyle.SOLID, exitTime, exitTime);
                b = true;
            } else {
                b = Bossbar.create(msg, BarColor.WHITE, BarStyle.SOLID, exitTime, exitTime);
            }
    
            if (b) {
                Bossbar.show(Util.getOnlinePlayers());
            }
        } else if ("chat".equalsIgnoreCase(ConfigReader.getString("announcement." + announcement + ".type"))) {
            String msg = new String();
            if (ConfigReader.getBoolean("announcement." + announcement + ".perfix")) {
                msg = ConfigReader.getString("perfix.text");
            }
            msg = msg + ConfigReader.getString("announcement." + announcement + ".text");
            msg = msg.replace('&', '\u00a7');
            Bukkit.broadcastMessage(msg);
        } else if ("command".equalsIgnoreCase(ConfigReader.getString("announcement." + announcement + ".type"))) {
            Util.executeCommand(ConfigReader.getString("announcement." + announcement + ".text"));
        } else if ("list".equalsIgnoreCase(ConfigReader.getString("announcement." + announcement + ".type"))) {
            boolean ignoreCd = ConfigReader.getBoolean("announcement." + announcement + ".ignoreCooldown");
            boolean resetCd = ConfigReader.getBoolean("announcement." + announcement + ".resetCooldown");
            List<String> announcementList = ConfigReader.getStringList("announcement." + announcement + ".list");
            for (String listAnnouncement : announcementList) {
                if (listAnnouncement.equalsIgnoreCase(announcement) && ConfigReader.getBoolean("debugMsg.warn.active")) {
                    ConfigReader.warn(ConfigReader.getString("debugMsg.warn.announcementCircle"));
                }

                boolean successful = singleAnnounce(listAnnouncement, currentStack + 1, maxStack, ignoreCd, resetCd); 
                if (!successful) {
                    return false;
                }
            }
        }

        return true;
    }

    private static void announce() {
        List<String> announcementList = ConfigReader.getStringList("function.announcement.list");
        for (String announcement : announcementList) {
            if (getCooldown(announcement) == 0) {
                int maxStack = ConfigReader.getInt("function.announcement.maxStack");
                boolean b = singleAnnounce(announcement, 0, maxStack, true, true);
                if (b == false && ConfigReader.getBoolean("debugMsg.warn.active")) {
                    ConfigReader.warn(ConfigReader.getString("debugMsg.warn.announcementOverflow"));
                }
            }
        }
    }

    public static void init() {
        List<String> announcementList = ConfigReader.getStringList("function.announcement.list");
        for (String announcement : announcementList) {
            setCooldown(announcement);
        }
    }

    public static void unInit() {
        Data.announcementCooldown.clear();
    }

    public static void tick() {
        try {
            if (ConfigReader.getBoolean("function.announcement.active")) {
                announce();
    
                tickCooldown();
            }
        } catch (Exception e) {
            ConfigReader.warn(ConfigReader.getString("debugMsg.error.announcementError"));
            if (ConfigReader.getBoolean("debugMsg.error.printStacktrace")) {
                e.printStackTrace();
            }
        }
        
    }
    
}
