package com.ykn.servermanager.event;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import com.ykn.servermanager.util.ConfigReader;
import com.ykn.servermanager.util.Data;

public class ProjectileHitListener implements Listener {
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {

        //射中怪物时触发消息
        Projectile hitter = event.getEntity();
        Entity hitEntity = event.getHitEntity();
        if (hitEntity != null && hitter != null) {
            if (hitEntity instanceof LivingEntity && hitter instanceof AbstractArrow) {
                if (ConfigReader.getBoolean("function.shootMessage.active")) {
                    Data.projectileHitCooldown = 3;
                    Data.projectileHitEntity = (LivingEntity) hitEntity;
                }
            }
        }



    }
}
