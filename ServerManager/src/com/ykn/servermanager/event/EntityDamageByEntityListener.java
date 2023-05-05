package com.ykn.servermanager.event;

import java.util.List;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.ykn.servermanager.display.Bossbar;
import com.ykn.servermanager.util.ConfigReader;
import com.ykn.servermanager.util.Data;
import com.ykn.servermanager.util.Util;

public class EntityDamageByEntityListener implements Listener {

    private void monsterSurroundMessage(Player player) {
        if (ConfigReader.getBoolean("function.surroundMessage.active")) {
            double limitDistance = ConfigReader.getDouble("function.surroundMessage.monsterDistance");
            int hurtFrequency = ConfigReader.getInt("function.surroundMessage.hurtFrequency");
            int monsterCount = ConfigReader.getInt("function.surroundMessage.monsterCount");
            List<Entity> entities = player.getLocation().getWorld().getEntities();
            double minDistance = limitDistance + 1.0;
            Entity nearestEntity = null;
            int entityCount = 0;
            for (Entity entity : entities) {
                double distance = -100.0;
                if (entity instanceof Monster) {
                    try {
                        distance = entity.getLocation().distance(player.getLocation());
                    } catch (IllegalArgumentException e) {
                        distance = -100.0;
                    }
                }

                if (distance > -0.01 && distance < limitDistance) {
                    entityCount++;
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestEntity = entity;
                    }
                }
            }

            if (entityCount >= monsterCount && nearestEntity != null && Data.playerHurtTime.get(player) <= hurtFrequency) {
                int exitTime = ConfigReader.getInt("function.surroundMessage.exitTime");
                String msg = new String();
                if (ConfigReader.getBoolean("function.surroundMessage.bossbarPerfix")) {
                    msg = ConfigReader.getString("perfix.text").replace('&', '\u00a7');
                }

                msg = msg + ConfigReader.getString("function.surroundMessage.message");
                msg = msg.replaceAll("#1", player.getName());
                msg = msg.replaceAll("#2", nearestEntity.getName());
                msg = msg.replaceAll("#3", Integer.toString(entityCount));

                boolean b = false;
                if (ConfigReader.getBoolean("function.surroundMessage.forceShow")) {
                    Bossbar.forceCreate(msg, BarColor.GREEN, BarStyle.SOLID, exitTime, exitTime);
                    b = true;
                } else {
                    b = Bossbar.create(msg, BarColor.GREEN, BarStyle.SOLID, exitTime, exitTime);
                }
        
                if (b) {
                    Bossbar.show(Util.getOnlinePlayers());
                }                
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        try {
            if (event.getEntity() instanceof Player) {
                this.monsterSurroundMessage((Player)event.getEntity());
                Data.playerHurtTime.put((Player)event.getEntity(), 0);
            }
        } catch (Exception e) {
            ConfigReader.warn(ConfigReader.getString("debugMsg.error.surroundMessageError"));
            if (ConfigReader.getBoolean("debugMsg.error.printStacktrace")) {
                e.printStackTrace();
            }
        }
    }
}
