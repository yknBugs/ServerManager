package com.ykn.servermanager.event;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.ykn.servermanager.display.Bossbar;
import com.ykn.servermanager.util.ConfigReader;
import com.ykn.servermanager.util.Data;
import com.ykn.servermanager.util.Util;

public class MobDeathListener implements Listener {
    
    /**
     * 将死亡原因转化为字符串
     * @param cause 死亡原因
     * @param mode 1表示#1死了，2表示#1被#2杀死了，3表示#1在与#3的战斗中被杀，4表示#1在与#3的战斗中被#2杀死了
     * @return 死亡消息字符串，需要再次解析#1 #2 #3等参数后才能使用
     */
    private String deathReasonToString(DamageCause cause, int mode) {
        String result = new String();
        String path = new String();
        if (cause == DamageCause.BLOCK_EXPLOSION) {
            path = "function.mobDeathMessage.reason.blockExplosion";
        } else if (cause == DamageCause.CONTACT) {
            path = "function.mobDeathMessage.reason.contact";
        } else if (cause == DamageCause.CRAMMING) {
            path = "function.mobDeathMessage.reason.cramming";
        } else if (cause == DamageCause.CUSTOM) {
            path = "function.mobDeathMessage.reason.custom";
        } else if (cause == DamageCause.DRAGON_BREATH) {
            path = "function.mobDeathMessage.reason.dragonBreath";
        } else if (cause == DamageCause.DROWNING) {
            path = "function.mobDeathMessage.reason.drowing";
        } else if (cause == DamageCause.DRYOUT) {
            path = "function.mobDeathMessage.reason.dryout";
        } else if (cause == DamageCause.ENTITY_ATTACK) {
            path = "function.mobDeathMessage.reason.entityAttack";
        } else if (cause == DamageCause.ENTITY_EXPLOSION) {
            path = "function.mobDeathMessage.reason.entityExplosion";
        } else if (cause == DamageCause.ENTITY_SWEEP_ATTACK) {
            path = "function.mobDeathMessage.reason.entitySweepAttack";
        } else if (cause == DamageCause.FALL) {
            path = "function.mobDeathMessage.reason.fall";
        } else if (cause == DamageCause.FALLING_BLOCK) {
            path = "function.mobDeathMessage.reason.fallingBlock";
        } else if (cause == DamageCause.FIRE) {
            path = "function.mobDeathMessage.reason.fire";
        } else if (cause == DamageCause.FIRE_TICK) {
            path = "function.mobDeathMessage.reason.fireTick";
        } else if (cause == DamageCause.FLY_INTO_WALL) {
            path = "function.mobDeathMessage.reason.flyIntoWall";
        } else if (cause == DamageCause.FREEZE) {
            path = "function.mobDeathMessage.reason.freeze";
        } else if (cause == DamageCause.HOT_FLOOR) {
            path = "function.mobDeathMessage.reason.hotFloor";
        } else if (cause == DamageCause.LAVA) {
            path = "function.mobDeathMessage.reason.lava";
        } else if (cause == DamageCause.LIGHTNING) {
            path = "function.mobDeathMessage.reason.lightning";
        } else if (cause == DamageCause.MAGIC) {
            path = "function.mobDeathMessage.reason.magic";
        } else if (cause == DamageCause.MELTING) {
            path = "function.mobDeathMessage.reason.melting";
        } else if (cause == DamageCause.POISON) {
            path = "function.mobDeathMessage.reason.poison";
        } else if (cause == DamageCause.PROJECTILE) {
            path = "function.mobDeathMessage.reason.projectile";
        } else if (cause == DamageCause.SONIC_BOOM) {
            path = "function.mobDeathMessage.reason.sonicBoom";
        } else if (cause == DamageCause.STARVATION) {
            path = "function.mobDeathMessage.reason.starvation";
        } else if (cause == DamageCause.SUFFOCATION) {
            path = "function.mobDeathMessage.reason.suffocation";
        } else if (cause == DamageCause.SUICIDE) {
            path = "function.mobDeathMessage.reason.suicide";
        } else if (cause == DamageCause.THORNS) {
            path = "function.mobDeathMessage.reason.thorns";
        } else if (cause == DamageCause.VOID) {
            path = "function.mobDeathMessage.reason.void";
        } else if (cause == DamageCause.WITHER) {
            path = "function.mobDeathMessage.reason.wither";
        } else {
            path = "function.mobDeathMessage.reason.unknownReason";
        }

        if (mode == 1) {
            path = path + ".a";
        } else if (mode == 2) {
            path = path + ".b";
        } else if (mode == 3) {
            path = path + ".c";
        } else if (mode == 4) {
            path = path + ".d";
        }
        result = ConfigReader.getString(path);
        return result;
    }

    //显示生物死亡消息
    private void showDeathMessage(LivingEntity entity) {
        if (ConfigReader.getBoolean("function.mobDeathMessage.active") && (!(entity instanceof Player))) {
            int exitTime = ConfigReader.getInt("function.mobDeathMessage.exitTime");
            double healthLimit = ConfigReader.getDouble("function.mobDeathMessage.healthLimit");
            double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            EntityDamageEvent entityDamageEvent = entity.getLastDamageCause();
            String msg = new String();
            if (maxHealth < healthLimit) {
                if (ConfigReader.getBoolean("function.mobDeathMessage.bossbarPerfix")) {
                    msg = ConfigReader.getString("perfix.text").replace('&', '\u00a7');
                }
            } else {
                if (ConfigReader.getBoolean("function.mobDeathMessage.messagePerfix")) {
                    msg = ConfigReader.getString("perfix.text").replace('&', '\u00a7');
                }
            }

            //获取上次受伤的事件
            if (entityDamageEvent == null) {
                msg = msg + ConfigReader.getString("function.mobDeathMessage.reason.noReason");
                msg = msg.replaceAll("#1", entity.getName());
                Bossbar.create(msg, BarColor.YELLOW, BarStyle.SOLID, exitTime, exitTime);
                return;
            }

            //获取攻击者
            Entity damager = null;
            Player killer = null;
            if (entityDamageEvent instanceof EntityDamageByEntityEvent) {
                damager = ((EntityDamageByEntityEvent) entityDamageEvent).getDamager();
                if (!(damager instanceof LivingEntity)) {
                    damager = null;
                }
            }
            killer = entity.getKiller();

            //两者都无
            if (damager == null && killer == null) {
                msg = msg + this.deathReasonToString(entityDamageEvent.getCause(), 1);
                msg = msg.replaceAll("#1", entity.getName());
            }
            //无攻击者，有在战斗中的玩家，为 #1 在与 #3 的战斗中xxx
            if (damager == null && killer != null) {
                msg = msg + this.deathReasonToString(entityDamageEvent.getCause(), 3);
                msg = msg.replaceAll("#1", entity.getName());
                msg = msg.replaceAll("#3", killer.getName());
            }
            //有攻击者，无在战斗中的玩家，为 #1 被 #2 xxx
            if (damager != null && killer == null) {
                msg = msg + this.deathReasonToString(entityDamageEvent.getCause(), 2);
                msg = msg.replaceAll("#1", entity.getName());
                msg = msg.replaceAll("#2", damager.getName());
            }
            //两者都有但两者不一样，为 #1 在与 #3 的战斗中被 #2 xxx
            if (damager != null && killer != null && (!(damager instanceof Player))) {
                msg = msg + this.deathReasonToString(entityDamageEvent.getCause(), 4);
                msg = msg.replaceAll("#1", entity.getName());
                msg = msg.replaceAll("#2", damager.getName());
                msg = msg.replaceAll("#3", killer.getName());
            }
            //攻击者为玩家，为 #1 被 #2 xxx
            if (damager != null && killer != null && (damager instanceof Player)) {
                msg = msg + this.deathReasonToString(entityDamageEvent.getCause(), 2);
                msg = msg.replaceAll("#1", entity.getName());
                msg = msg.replaceAll("#2", damager.getName());
            }

            //显示消息
            if (maxHealth < healthLimit) {
                Boolean b = Bossbar.create(msg, BarColor.GREEN, BarStyle.SOLID, exitTime, exitTime);
                if (b) {
                    Bossbar.show(Util.getOnlinePlayers());
                }
            } else {
                Bukkit.broadcastMessage(msg);
            }
        }
    }

    private void broadcastPlayerDeathPos(LivingEntity entity) {
        if (ConfigReader.getBoolean("function.playerDeathMessage.active") && (entity instanceof Player)) {
            String msg = new String();
            if (ConfigReader.getBoolean("function.playerDeathMessage.hasPerfix")) {
                msg = ConfigReader.getString("perfix.text").replace('&', '\u00a7');
            }
            msg = msg + ConfigReader.getString("function.playerDeathMessage.message");
            msg = msg.replaceAll("#1", entity.getName());
            msg = msg.replaceAll("#2", entity.getLocation().getWorld().getName());
            msg = msg.replaceAll("#3", Integer.toString((int)entity.getLocation().getX()));
            msg = msg.replaceAll("#4", Integer.toString((int)entity.getLocation().getY()));
            msg = msg.replaceAll("#5", Integer.toString((int)entity.getLocation().getZ()));

            if (ConfigReader.getBoolean("function.playerDeathMessage.broadcastToAll")) {
                Bukkit.broadcastMessage(msg);
            } else {
                entity.sendMessage(msg);
            }
        } 
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        try {
            this.showDeathMessage(event.getEntity());
        } catch (Exception e) {
            ConfigReader.warn(ConfigReader.getString("debugMsg.error.deathMessageError"));
            if (ConfigReader.getBoolean("debugMsg.error.printStacktrace")) {
                e.printStackTrace();
            }
        }

        try {
            this.broadcastPlayerDeathPos(event.getEntity());
        } catch (Exception e) {
            ConfigReader.warn(ConfigReader.getString("debugMsg.error.playerDeathError"));
            if (ConfigReader.getBoolean("debugMsg.error.printStacktrace")) {
                e.printStackTrace();
            }
        }

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Data.playerLastLocation.put(player, player.getLocation());
        }
    }
}
