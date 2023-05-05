package com.ykn.servermanager.event;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.ykn.servermanager.display.Bossbar;
import com.ykn.servermanager.util.ConfigReader;
import com.ykn.servermanager.util.Data;
import com.ykn.servermanager.util.Util;

public final class TimeChangeListener extends BukkitRunnable {

    private void limitEntity() {
        int entityCount = Util.getEntityCount();
        boolean activebd = ConfigReader.getBoolean("function.entityLimit.activeBroadcast");
        boolean activewa = ConfigReader.getBoolean("function.entityLimit.activeWarn");
        boolean activecl = ConfigReader.getBoolean("function.entityLimit.activeClear");
        int warnLevel = ConfigReader.getInt("function.entityLimit.warnLevel"); 
        int clearLevel = ConfigReader.getInt("function.entityLimit.clearLevel");

        //清除
        if (activecl) {
            if (entityCount >= clearLevel && Data.clearEntityCountDown == 0) {
                Data.clearEntityCountDown = ConfigReader.getInt("function.entityLimit.clearTime");
            }

            if (Data.clearEntityCountDown > 2) {
                String msg = new String();
                if (ConfigReader.getBoolean("function.entityLimit.bossbarPerfix")) {
                    msg = ConfigReader.getString("perfix.text").replace('&', '\u00a7');
                }
    
                msg = msg + ConfigReader.getString("function.entityLimit.clearMsg");
                msg = msg.replaceAll("#1", Integer.toString(entityCount));
                msg = msg.replaceAll("#2", Integer.toString(Data.clearEntityCountDown / 20));

                if (Data.entityCountBroadcastUid == Bossbar.getUid() && Bossbar.getHasBossbar()) {
                    Bossbar.setTitle(Data.entityCountBroadcastUid, msg);
                } else if (ConfigReader.getBoolean("function.entityLimit.forceClear")) {
                    Bossbar.forceCreate(msg, BarColor.RED, BarStyle.SOLID, ConfigReader.getInt("function.entityLimit.clearTime"), Data.clearEntityCountDown);
                    Data.entityCountBroadcastUid = Bossbar.getUid();
                    Bossbar.show(Util.getOnlinePlayers());
                } else {
                    Boolean b = Bossbar.lightCreate(msg, BarColor.RED, BarStyle.SOLID, ConfigReader.getInt("function.entityLimit.clearTime"), Data.clearEntityCountDown);
                    if (b) {
                        Data.entityCountBroadcastUid = Bossbar.getUid();
                        Bossbar.show(Util.getOnlinePlayers());
                    }
                }
                
                return;
            } 

            if (Data.clearEntityCountDown == 2) {
                //清除实体
                Data.entityCountBeforeClear = entityCount;
                Util.executeCommand("kill @e[type=minecraft:item]");
                Util.executeCommand("kill @e[type=minecraft:experience_orb]");
                Util.executeCommand("kill @e[type=#minecraft:arrows]");
                return;
            }

            if (Data.clearEntityCountDown == 1) {
                Data.entityCountAfterClear = entityCount;
                int clearCount = Data.entityCountBeforeClear - Data.entityCountAfterClear;

                //显示消息
                String msg = new String();
                if (ConfigReader.getBoolean("function.entityLimit.clearPerfix")) {
                    msg = ConfigReader.getString("perfix.text").replace('&', '\u00a7');
                }
                msg = msg + ConfigReader.getString("function.entityLimit.clearBroadcast");
                msg = msg.replaceAll("#1", Integer.toString(clearCount));

                Bukkit.broadcastMessage(msg);
                return;
            }
        }

        //警告
        if (activewa) {
            if (entityCount >= warnLevel) {
                String msg = new String();
                if (ConfigReader.getBoolean("function.entityLimit.bossbarPerfix")) {
                    msg = ConfigReader.getString("perfix.text").replace('&', '\u00a7');
                }

                msg = msg + ConfigReader.getString("function.entityLimit.warnMsg");
                msg = msg.replaceAll("#1", Integer.toString(entityCount));

                if (Data.entityCountBroadcastUid == Bossbar.getUid() && Bossbar.getHasBossbar()) {
                    Bossbar.setTitle(Data.entityCountBroadcastUid, msg);
                    Bossbar.forceSetValue(2);
                } else if (ConfigReader.getBoolean("function.entityLimit.forceWarn")) {
                    Bossbar.forceCreate(msg, BarColor.YELLOW, BarStyle.SOLID, 2, 2);
                    Data.entityCountBroadcastUid = Bossbar.getUid();
                    Bossbar.show(Util.getOnlinePlayers());
                } else {
                    Boolean b = Bossbar.lightCreate(msg, BarColor.GREEN, BarStyle.SOLID, 2, 2);
                    if (b) {
                        Data.entityCountBroadcastUid = Bossbar.getUid();
                        Bossbar.show(Util.getOnlinePlayers());
                    }
                }
                return;
            }
        }

        //播报
        if (activebd) {
            int exitTime = ConfigReader.getInt("function.entityLimit.broadcastTime");
            String msg = new String();
            if (ConfigReader.getBoolean("function.entityLimit.bossbarPerfix")) {
                msg = ConfigReader.getString("perfix.text").replace('&', '\u00a7');
            }

            msg = msg + ConfigReader.getString("function.entityLimit.broadcastMsg");
            msg = msg.replaceAll("#1", Integer.toString(entityCount));

            if (Data.entityCountBroadcastUid == Bossbar.getUid() && Bossbar.getHasBossbar()) {
                Bossbar.setTitle(Data.entityCountBroadcastUid, msg);
            }

            if (Data.broadcastEntityCountCD == 0) {
                if (ConfigReader.getBoolean("function.entityLimit.forceBroadcast")) {
                    Bossbar.forceCreate(msg, BarColor.GREEN, BarStyle.SOLID, exitTime, exitTime);
                    Data.entityCountBroadcastUid = Bossbar.getUid();
                    Bossbar.show(Util.getOnlinePlayers());
                } else {
                    Boolean b = Bossbar.create(msg, BarColor.GREEN, BarStyle.SOLID, exitTime, exitTime);
                    if (b) {
                        Data.entityCountBroadcastUid = Bossbar.getUid();
                        Bossbar.show(Util.getOnlinePlayers());
                    }
                }
                Data.broadcastEntityCountCD = ConfigReader.getInt("function.entityLimit.broadcastCD");
            }
        }
    }

    private void hitMessage() {
        if (Data.projectileHitEntity.getHealth() > 0) {
            int exitTime = ConfigReader.getInt("function.shootMessage.exitTime");
            String msg = new String();
            if (ConfigReader.getBoolean("function.shootMessage.bossbarPerfix")) {
                msg = ConfigReader.getString("perfix.text").replace('&', '\u00a7');
            }
    
            msg = msg + ConfigReader.getString("function.shootMessage.message");
            msg = msg.replaceAll("#1", Data.projectileHitEntity.getKiller().getName());
            msg = msg.replaceAll("#2", Data.projectileHitEntity.getName());
            msg = msg.replaceAll("#3", Integer.toString((int)Data.projectileHitEntity.getHealth()));
    
            boolean b = false;
            if (ConfigReader.getBoolean("function.shootMessage.forceShow")) {
                Bossbar.forceCreate(msg, BarColor.GREEN, BarStyle.SOLID, exitTime, exitTime);
                b = true;
            } else {
                b = Bossbar.create(msg, BarColor.GREEN, BarStyle.SOLID, exitTime, exitTime);
            }
    
            if (b) {
                if (ConfigReader.getBoolean("function.shootMessage.broadcastToAll")) {
                    Bossbar.show(Util.getOnlinePlayers());
                } else {
                    Bossbar.show(Data.projectileHitEntity.getKiller());
                }
            }
        }
    }

    private void idleMessage(Player player, boolean isStart) {
        if (isStart) {
            if (ConfigReader.getBoolean("function.playerIdleTimeout.kick")) {
                String msg = new String();
                if (ConfigReader.getBoolean("function.playerIdleTimeout.kickPerfix")) {
                    msg = ConfigReader.getString("perfix.text").replace('&', '\u00a7');
                }
                msg = msg + ConfigReader.getString("function.playerIdleTimeout.kickMessage");
                Data.playerIdleTimeout.put(player, 0);
                player.kickPlayer(msg);
            } else {
                String msg = new String();
                if (ConfigReader.getBoolean("function.playerIdleTimeout.messagePerfix")) {
                    msg = ConfigReader.getString("perfix.text").replace('&', '\u00a7');
                }
                msg = msg + ConfigReader.getString("function.playerIdleTimeout.idleMessage");
                msg = msg.replaceAll("#1", player.getName());
                Bukkit.broadcastMessage(msg);                
            }
        } else {
            String msg = new String();
            if (ConfigReader.getBoolean("function.playerIdleTimeout.messagePerfix")) {
                msg = ConfigReader.getString("perfix.text").replace('&', '\u00a7');
            }
            msg = msg + ConfigReader.getString("function.playerIdleTimeout.backMessage");
            msg = msg.replaceAll("#1", player.getName());
            msg = msg.replaceAll("#2", Integer.toString(Data.playerIdleTimeout.get(player) / 20));
            Bukkit.broadcastMessage(msg);
        }
    }
    
    @Override
    public void run() {
        //更新数据
        List<Player> playerList = Util.getOnlinePlayers();
        for (Player player : playerList) {
            Data.playerCurrentYaw.put(player, (int)player.getLocation().getYaw());
            if (Data.playerCurrentYaw.get(player) != Data.playerLastYaw.get(player)) {
                if (Data.playerIdleTimeout.containsKey(player) == false) {
                    Data.playerIdleTimeout.put(player, 0);
                }
                //挂机检测之结束挂机
                try {
                    if (ConfigReader.getBoolean("function.playerIdleTimeout.active")) {
                        int idleLimit = ConfigReader.getInt("function.playerIdleTimeout.idleLimit");
                        if (Data.playerIdleTimeout.get(player) >= idleLimit) {
                            this.idleMessage(player, false);
                        }
                    }                
                } catch (Exception e) {
                    ConfigReader.warn(ConfigReader.getString("debugMsg.error.idleTimeoutError"));
                    if (ConfigReader.getBoolean("debugMsg.error.printStacktrace")) {
                        e.printStackTrace();
                    }
                }

                Data.playerIdleTimeout.put(player, 0);
            }

            //挂机检测
            try {
                if (ConfigReader.getBoolean("function.playerIdleTimeout.active")) {
                    int idleLimit = ConfigReader.getInt("function.playerIdleTimeout.idleLimit");
                    if (Data.playerIdleTimeout.get(player) == idleLimit) {
                        this.idleMessage(player, true);
                    }
                }                
            } catch (Exception e) {
                ConfigReader.warn(ConfigReader.getString("debugMsg.error.idleTimeoutError"));
                if (ConfigReader.getBoolean("debugMsg.error.printStacktrace")) {
                    e.printStackTrace();
                }
            }
        }

        //实体数量限制
        try {
            this.limitEntity();
        } catch (Exception e) {
            ConfigReader.warn(ConfigReader.getString("debugMsg.error.entityCountError"));
            if (ConfigReader.getBoolean("debugMsg.error.printStacktrace")) {
                e.printStackTrace();
            }
        }

        //射中生物提示
        try {
            if (Data.projectileHitCooldown == 1 && Data.projectileHitEntity != null) {
                if (Data.projectileHitEntity.getKiller() != null) {
                    this.hitMessage();
                }
                Data.projectileHitEntity = null;
            }
        } catch (Exception e) {
            ConfigReader.warn(ConfigReader.getString("debugMsg.error.shootMessageError"));
            if (ConfigReader.getBoolean("debugMsg.error.printStacktrace")) {
                e.printStackTrace();
            }
        }


        //执行计划任务
        if (Data.hasScheduledCommand) {
            for (String command : Data.scheduledCommandList) {
                Util.executeCommand(command);
            }
            Data.hasScheduledCommand = false;
            Data.scheduledCommandList.clear();
        }

        //时间流逝相关操作
        Bossbar.tick();
        Data.tick();

        //复制数据
        Data.playerLastYaw.clear();
        Data.playerLastYaw.putAll(Data.playerCurrentYaw);
    }
    
}
