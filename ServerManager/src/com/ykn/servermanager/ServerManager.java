package com.ykn.servermanager;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.ykn.servermanager.command.SMCommand;
import com.ykn.servermanager.command.SMCommandTab;
import com.ykn.servermanager.display.Announcement;
import com.ykn.servermanager.event.EntityDamageByEntityListener;
import com.ykn.servermanager.event.MobDeathListener;
import com.ykn.servermanager.event.PlayerChatListener;
import com.ykn.servermanager.event.PlayerMoveListener;
import com.ykn.servermanager.event.ProjectileHitListener;
import com.ykn.servermanager.event.TimeChangeListener;
import com.ykn.servermanager.util.ConfigReader;

public class ServerManager extends JavaPlugin {

    BukkitTask tickTask;

    @Override
    public void onEnable() {
        //保存配置文件
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();
        ConfigReader.setServerManager(this);

        //初始化
        Announcement.init();

        //注册指令
        try {
            this.getCommand("servermanager").setExecutor(new SMCommand());
            this.getCommand("servermanager").setTabCompleter(new SMCommandTab());
        } catch (NullPointerException exception) {
            if(ConfigReader.getBoolean("debugMsg.error.active")) {
                getLogger().warning(ConfigReader.getString("debugMsg.error.commandInitError"));
                if (ConfigReader.getBoolean("debugMsg.error.printStacktrace")) {
                    exception.printStackTrace();
                }
            }
        }

        //注册事件监听器
        this.getServer().getPluginManager().registerEvents(new MobDeathListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        this.getServer().getPluginManager().registerEvents(new ProjectileHitListener(), this);
        this.getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        this.tickTask = new TimeChangeListener().runTaskTimer(this, 0, 1);

        //加载完成
        if(ConfigReader.getBoolean("debugMsg.info.active")) {
            getLogger().info(ConfigReader.getString("debugMsg.info.onPluginEnable"));
        }
    }

    @Override
    public void onDisable() {
        //卸载监听器
        HandlerList.unregisterAll();
        this.tickTask.cancel();

        Announcement.unInit();

        //卸载完成
        if(ConfigReader.getBoolean("debugMsg.info.active")) {
            getLogger().info(ConfigReader.getString("debugMsg.info.onPluginDisable"));
        }
    }
}
