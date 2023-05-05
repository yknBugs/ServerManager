package com.ykn.servermanager.event;

import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.ykn.servermanager.display.Bossbar;
import com.ykn.servermanager.util.ConfigReader;
import com.ykn.servermanager.util.Util;

public class PlayerMoveListener implements Listener {
    
    private void showBiomeChangeMsg(Player player, Biome moveFrom, Biome moveTo) {
        if (ConfigReader.getBoolean("function.biomeChangeMsg.active")) {
            int exitTime = ConfigReader.getInt("function.biomeChangeMsg.exitTime");
            String msg = new String();
            if (ConfigReader.getBoolean("function.biomeChangeMsg.bossbarPerfix")) {
                msg = ConfigReader.getString("perfix.text").replace('&', '\u00a7');
            }

            msg = msg + ConfigReader.getString("function.biomeChangeMsg.message");
            msg = msg.replaceAll("#1", player.getName());
            msg = msg.replaceAll("#2", moveTo.toString().replace('_', ' ').toLowerCase());

            Boolean b = Bossbar.create(msg, BarColor.GREEN, BarStyle.SOLID, exitTime, exitTime);
            if (b) {
                Bossbar.show(Util.getOnlinePlayers());
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        try {
            Biome moveFrom = event.getFrom().getBlock().getBiome();
            Biome moveTo = event.getTo().getBlock().getBiome();
    
            if (moveFrom != moveTo) {
                this.showBiomeChangeMsg(event.getPlayer(), moveFrom, moveTo);
            }
        } catch (Exception e) {
            ConfigReader.warn(ConfigReader.getString("debugMsg.error.biomeMessageError"));
            if (ConfigReader.getBoolean("debugMsg.error.printStacktrace")) {
                e.printStackTrace();
            }
        }

        
    }
}
