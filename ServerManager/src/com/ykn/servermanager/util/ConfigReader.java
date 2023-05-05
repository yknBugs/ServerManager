package com.ykn.servermanager.util;

import java.util.List;
import com.ykn.servermanager.ServerManager;

public class ConfigReader {
    private static ServerManager serverManager;
    
    public static void setServerManager(ServerManager manager) {
        serverManager = manager;
    }

    public static String getString(String path) {
        return serverManager.getConfig().getString(path);
    }

    public static boolean getBoolean(String path) {
        return serverManager.getConfig().getBoolean(path);
    }

    public static int getInt(String path) {
        return serverManager.getConfig().getInt(path);
    }

    public static double getDouble(String path) {
        return serverManager.getConfig().getDouble(path);
    }

    public static List<String> getStringList(String path) {
        return serverManager.getConfig().getStringList(path);
    }

    public static void info(String msg) {
        serverManager.getLogger().info(msg);
    }

    public static void warn(String msg) {
        serverManager.getLogger().warning(msg);
    }
}
