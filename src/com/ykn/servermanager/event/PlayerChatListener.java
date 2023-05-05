package com.ykn.servermanager.event;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.ykn.servermanager.util.ConfigReader;
import com.ykn.servermanager.util.Util;

public class PlayerChatListener implements Listener {

    private void executeMatches(AsyncPlayerChatEvent event, String triggerString) {
        String message = event.getMessage();
        List<String> regexList = ConfigReader.getStringList("chatTrigger." + triggerString + ".regexList");
        boolean matchSuccess = false;
        boolean isCancel = ConfigReader.getBoolean("chatTrigger." + triggerString + ".isCancel");
        boolean hasAction = ConfigReader.getBoolean("chatTrigger." + triggerString + ".hasAction");
        for (String regex : regexList) {
            String fixRegex = regex;
            if (!regex.matches("^\\^")) {
                fixRegex = ".*" + fixRegex;
            }
            if (!regex.matches("\\$$")) {
                fixRegex = fixRegex + ".*";
            }

            if (message.matches(fixRegex)) {
                //符合正则表达式则执行内容
                matchSuccess = true;
                if (ConfigReader.getBoolean("debugMsg.info.active")) {
                    String info = ConfigReader.getString("debugMsg.info.chatTriggerInfo");
                    info = info.replaceAll("#1", event.getPlayer().getName());
                    info = info.replaceAll("#2", message);
                    info = info.replaceAll("#3", regex);
                    ConfigReader.info(info);
                }

                if (isCancel) {
                    //设置取消
                    event.setCancelled(true);
                    break;
                } else if (ConfigReader.getBoolean("chatTrigger." + triggerString + ".isReplace")) {
                    //不取消则替换掉内容
                    String replacement = ConfigReader.getString("chatTrigger." + triggerString + ".replacement");
                    message = message.replaceAll(regex, replacement);
                    event.setMessage(message);
                }
            }
        } 
        
        if (hasAction && matchSuccess) {
            //替换成功且有执行指令的需求则执行指令
            List<String> cmdList = ConfigReader.getStringList("chatTrigger." + triggerString + ".actionCmdList");
            for (String cmd : cmdList) {
                String command = cmd.replaceAll("#1", event.getPlayer().getName());
                command = command.replace('&', '\u00a7');
                Util.scheduleCommand(command);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        try {
            if (ConfigReader.getBoolean("function.chatTrigger.active")) {
                //遍历所有的聊天检测项目
                List<String> triggerList = ConfigReader.getStringList("function.chatTrigger.list");
                for (String triggerString : triggerList) {
                    //遍历一个项目中的所有正则表达式                    
                    this.executeMatches(event, triggerString);
                }
            }
        } catch (Exception e) {
            ConfigReader.warn(ConfigReader.getString("debugMsg.error.playerChatError"));
            if (ConfigReader.getBoolean("debugMsg.error.printStacktrace")) {
                e.printStackTrace();
            }
        }
    }
}
