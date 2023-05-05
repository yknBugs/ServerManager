package com.ykn.servermanager.command;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.CommandBlock;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ykn.servermanager.display.Bossbar;
import com.ykn.servermanager.util.ConfigReader;
import com.ykn.servermanager.util.Data;
import com.ykn.servermanager.util.Util;

public class SMCommand implements CommandExecutor {
     //指令servermanager,显示插件信息
    private boolean cmdServerManager(CommandSender sender, Command command, String label, String[] args) {
        String perfixText = ConfigReader.getString("perfix.text").replace('&', '\u00a7');
        String logPerfix = ConfigReader.getString("debugMsg.perfix");

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("servermanager.servermanager")) {
                player.sendMessage(perfixText + "服务器管理插件 v0.0.5 由 ykn 制作，于 2022.9.7 发布");
                return true;
            } else {
                return false;
            }
        } else if (sender instanceof CommandBlock) {
            return false;
        } else {
            sender.sendMessage(logPerfix + "Server Manager v0.0.5 made by ykn, complied at 2022.9.7");
            return true;
        }
    }

    //指令servermanager broadcast，在指定位置发送聊天消息
    private boolean cmdBroadcast(CommandSender sender, Command command, String label, String[] args) {
        if (ConfigReader.getBoolean("command.broadcast.active")) {
            //开启了指令功能
            if (args.length == 1) {
                //缺少参数
                Util.sendCmdMessage(sender, 
                ConfigReader.getBoolean("command.broadcast.messagePerfix"), 
                ConfigReader.getString("command.broadcast.lessArgsMsg"));
            } else if (args[1].equalsIgnoreCase("bossbar")) {
                //servermanager broadcast bossbar
                return cmdBroadcastBossbar(sender, command, label, args);
            } else if (args[1].equalsIgnoreCase("chat")) {
                //servermanager broadcast chat
                return cmdBroadcastChat(sender, command, label, args);
            } else {
                //未知的参数
                Util.sendCmdMessage(sender, 
                ConfigReader.getBoolean("command.broadcast.messagePerfix"), 
                ConfigReader.getString("command.broadcast.wrongArgMsg"));
            }
        }

        return false;
    }

    //指令servermanager broadcast bossbar <tick> <hasPerfix> <isForce> <Message>, 显示插件信息
    //指令servermanager broadcast bossbar clear 清除bossbar消息
    private boolean cmdBroadcastBossbar(CommandSender sender, Command command, String label, String[] args) {
        if (ConfigReader.getBoolean("command.broadcast.bossbar.active")) {
            //开启了指令功能
            if (args.length >= 3) {
                if (args[2].equalsIgnoreCase("clear")) {
                    //清除boss栏
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (!player.hasPermission("servermanager.broadcast.bossbar")) {
                            //没有权限
                            Util.sendCmdMessage(sender, 
                            ConfigReader.getBoolean("command.broadcast.bossbar.messagePerfix"), 
                            ConfigReader.getString("command.broadcast.bossbar.noPermissionMsg"));
                            return true;
                        }
                    } 

                    if (Bossbar.getHasBossbar() == false) {
                        Util.sendCmdMessage(sender, 
                        ConfigReader.getBoolean("command.broadcast.bossbar.messagePerfix"), 
                        ConfigReader.getString("command.broadcast.bossbar.failClearMsg"));
                        return true;
                    }

                    Bossbar.forceRemove();
                    Util.sendCmdMessage(sender, 
                    ConfigReader.getBoolean("command.broadcast.bossbar.messagePerfix"), 
                    ConfigReader.getString("command.broadcast.bossbar.successClearMsg"));
                    return true;
                }
            }

            if (args.length < 6) {
                //缺少参数
                Util.sendCmdMessage(sender, 
                ConfigReader.getBoolean("command.broadcast.bossbar.messagePerfix"), 
                ConfigReader.getString("command.broadcast.bossbar.lessArgsMsg"));
            } else {
                int exitTime = 0;
                if (args[2].matches("^[0-9]{1,4}$")) {
                    exitTime = Integer.parseInt(args[2]);
                } else {
                    Util.sendCmdMessage(sender, 
                    ConfigReader.getBoolean("command.broadcast.bossbar.messagePerfix"), 
                    ConfigReader.getString("command.broadcast.bossbar.wrongTick"));
                    return true;
                }

                boolean hasPerfix = false;
                if (args[3].equalsIgnoreCase("true")) {
                    hasPerfix = true;
                } else if (args[3].equalsIgnoreCase("false")) {
                    hasPerfix = false;
                } else {
                    Util.sendCmdMessage(sender, 
                    ConfigReader.getBoolean("command.broadcast.bossbar.messagePerfix"), 
                    ConfigReader.getString("command.broadcast.bossbar.wrongBoolean"));
                    return true;
                }

                boolean isForce = false;
                if (args[4].equalsIgnoreCase("true")) {
                    isForce = true;
                } else if (args[4].equalsIgnoreCase("false")) {
                    isForce = false;
                } else {
                    Util.sendCmdMessage(sender, 
                    ConfigReader.getBoolean("command.broadcast.bossbar.messagePerfix"), 
                    ConfigReader.getString("command.broadcast.bossbar.wrongBoolean"));
                    return true;
                }

                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (!player.hasPermission("servermanager.broadcast.bossbar")) {
                        //没有权限
                        Util.sendCmdMessage(sender, 
                        ConfigReader.getBoolean("command.broadcast.bossbar.messagePerfix"), 
                        ConfigReader.getString("command.broadcast.bossbar.noPermissionMsg"));
                        return true;
                    }
                } 

                String title = new String();
                if (hasPerfix) {
                    title = ConfigReader.getString("perfix.text").replace('&', '\u00a7');
                }
                for (int i = 5; i < args.length; i++) {
                    if (i > 5) {
                        title = title + " ";
                    }
                    title = title + args[i];
                }

                boolean b = false;
                if (isForce) {
                    Bossbar.forceCreate(title, BarColor.WHITE, BarStyle.SOLID, exitTime, exitTime);
                    b = true;
                } else {
                    b = Bossbar.create(title, BarColor.WHITE, BarStyle.SOLID, exitTime, exitTime);
                }
        
                if (b) {
                    Util.sendCmdMessage(sender, 
                    ConfigReader.getBoolean("command.broadcast.bossbar.messagePerfix"), 
                    ConfigReader.getString("command.broadcast.bossbar.successMsg"));

                    Bossbar.show(Util.getOnlinePlayers());
                } else {
                    Util.sendCmdMessage(sender, 
                    ConfigReader.getBoolean("command.broadcast.bossbar.messagePerfix"), 
                    ConfigReader.getString("command.broadcast.bossbar.failMsg"));
                }

                return true;
            }
        }

        return false;
    }

    //指令servermanager broadcast chat <消息>
    private boolean cmdBroadcastChat(CommandSender sender, Command command, String label, String[] args) {
        if (ConfigReader.getBoolean("command.broadcast.chat.active")) {
            if (args.length < 3) {
                //缺少参数
                Util.sendCmdMessage(sender, 
                ConfigReader.getBoolean("command.broadcast.chat.messagePerfix"), 
                ConfigReader.getString("command.broadcast.chat.lessArgsMsg"));
            } else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (!player.hasPermission("servermanager.broadcast.chat")) {
                        //没有权限
                        Util.sendCmdMessage(sender, 
                        ConfigReader.getBoolean("command.broadcast.chat.messagePerfix"), 
                        ConfigReader.getString("command.broadcast.chat.noPermissionMsg"));
                        return true;
                    }
                } 

                String title = new String();
                if (ConfigReader.getBoolean("command.broadcast.chat.messagePerfix")) {
                    title = ConfigReader.getString("perfix.text").replace('&', '\u00a7');
                }

                for(int i = 2; i < args.length; i++) {
                    if (i > 2) {
                        title = title + " ";
                    }
                    title = title + args[i];
                }

                Bukkit.broadcastMessage(title);
                return true;
            }
        }

        return false;
    }

    //指令servermanager home <操作> <名称> 设置传送点
    //servermanager home add
    //servermanager home set
    //servermanager home remove
    //servermanager home list
    //servermanager home get
    //servermanager home tp|teleport
    //servermanager home share
    private boolean cmdHome(CommandSender sender, Command command, String label, String[] args) {
        if (ConfigReader.getBoolean("command.home.active")) {
            if (!(sender instanceof Player)) {
                //不是玩家执行
                Util.sendCmdMessage(sender, 
                ConfigReader.getBoolean("command.home.messagePerfix"), 
                ConfigReader.getString("command.home.wrongSenderMsg"));
            } else if (((Player)sender).hasPermission("servermanager.home") == false) {
                //没有权限
                Util.sendCmdMessage(sender, 
                ConfigReader.getBoolean("command.home.messagePerfix"), 
                ConfigReader.getString("command.home.noPermissionMsg"));
            } else if (args.length < 2) {
                //缺少参数
                Util.sendCmdMessage(sender, 
                ConfigReader.getBoolean("command.home.messagePerfix"), 
                ConfigReader.getString("command.home.lessArgsMsg"));
            } else if (args.length < 3 && args[1].equalsIgnoreCase("list") == false) {
                //缺少参数
                Util.sendCmdMessage(sender, 
                ConfigReader.getBoolean("command.home.messagePerfix"), 
                ConfigReader.getString("command.home.lessArgsMsg"));
            } else {
                Player player = (Player) sender;
                if (args[1].equalsIgnoreCase("add")) {
                    if (Data.playerHomeData.get(player).containsKey(args[2])) {
                        //重复名称
                        Util.sendCmdMessage(sender, 
                        ConfigReader.getBoolean("command.home.messagePerfix"), 
                        ConfigReader.getString("command.home.duplicatedHome"));
                    } else {
                        Data.playerHomeData.get(player).put(args[2], player.getLocation());
                        Util.sendCmdMessage(sender, 
                        ConfigReader.getBoolean("command.home.messagePerfix"), 
                        ConfigReader.getString("command.home.successAddMsg").replaceAll("#1", args[2]));
                    }
                    return true;
                } else if (args[1].equalsIgnoreCase("set")) {
                    if (Data.playerHomeData.get(player).containsKey(args[2]) == false) {
                        //没有找到
                        Util.sendCmdMessage(sender, 
                        ConfigReader.getBoolean("command.home.messagePerfix"), 
                        ConfigReader.getString("command.home.undefinedHome"));
                    } else {
                        Data.playerHomeData.get(player).put(args[2], player.getLocation());
                        Util.sendCmdMessage(sender, 
                        ConfigReader.getBoolean("command.home.messagePerfix"), 
                        ConfigReader.getString("command.home.successSetMsg").replaceAll("#1", args[2]));
                    }
                    return true;                    
                } else if (args[1].equalsIgnoreCase("remove")) {
                    if (Data.playerHomeData.get(player).containsKey(args[2]) == false) {
                        //没有找到
                        Util.sendCmdMessage(sender, 
                        ConfigReader.getBoolean("command.home.messagePerfix"), 
                        ConfigReader.getString("command.home.undefinedHome"));
                    } else {
                        Data.playerHomeData.get(player).remove(args[2]);
                        Util.sendCmdMessage(sender, 
                        ConfigReader.getBoolean("command.home.messagePerfix"), 
                        ConfigReader.getString("command.home.successRemoveMsg").replaceAll("#1", args[2]));
                    }
                    return true;                     
                } else if (args[1].equalsIgnoreCase("list")) {
                    Set<String> homeList = Data.playerHomeData.get(player).keySet();
                    if (homeList.size() == 0) {
                        //没有设置home
                        Util.sendCmdMessage(sender, 
                        ConfigReader.getBoolean("command.home.messagePerfix"), 
                        ConfigReader.getString("command.home.emptyHomeList"));                        
                    } else {
                        Util.sendCmdMessage(sender, 
                        ConfigReader.getBoolean("command.home.messagePerfix"), 
                        ConfigReader.getString("command.home.successListMsg"));      
                        for (String homeName : homeList) {
                            String homeInfo = ConfigReader.getString("command.home.successGetMsg");
                            homeInfo = homeInfo.replaceAll("#1", homeName);
                            homeInfo = homeInfo.replaceAll("#2", Data.playerHomeData.get(player).get(homeName).getWorld().getName());
                            homeInfo = homeInfo.replaceAll("#3", Integer.toString((int)Data.playerHomeData.get(player).get(homeName).getX()));
                            homeInfo = homeInfo.replaceAll("#4", Integer.toString((int)Data.playerHomeData.get(player).get(homeName).getY()));
                            homeInfo = homeInfo.replaceAll("#5", Integer.toString((int)Data.playerHomeData.get(player).get(homeName).getZ()));
                            homeInfo = homeInfo.replaceAll("#6", Data.playerHomeData.get(player).get(homeName).getBlock().getBiome().toString().replace('_', ' ').toLowerCase());
                            Util.sendCmdMessage(sender, ConfigReader.getBoolean("command.home.messagePerfix"), homeInfo);
                        }
                    }
                    return true;
                } else if (args[1].equalsIgnoreCase("get")) {
                    if (Data.playerHomeData.get(player).containsKey(args[2]) == false) {
                        //没有找到
                        Util.sendCmdMessage(sender, 
                        ConfigReader.getBoolean("command.home.messagePerfix"), 
                        ConfigReader.getString("command.home.undefinedHome"));
                    } else {
                        String homeInfo = ConfigReader.getString("command.home.successGetMsg");
                        homeInfo = homeInfo.replaceAll("#1", args[2]);
                        homeInfo = homeInfo.replaceAll("#2", Data.playerHomeData.get(player).get(args[2]).getWorld().getName());
                        homeInfo = homeInfo.replaceAll("#3", Integer.toString((int)Data.playerHomeData.get(player).get(args[2]).getX()));
                        homeInfo = homeInfo.replaceAll("#4", Integer.toString((int)Data.playerHomeData.get(player).get(args[2]).getY()));
                        homeInfo = homeInfo.replaceAll("#5", Integer.toString((int)Data.playerHomeData.get(player).get(args[2]).getZ()));
                        homeInfo = homeInfo.replaceAll("#6", Data.playerHomeData.get(player).get(args[2]).getBlock().getBiome().toString().replace('_', ' ').toLowerCase());
                        Util.sendCmdMessage(sender, ConfigReader.getBoolean("command.home.messagePerfix"), homeInfo);
                    }
                    return true;
                } else if (args[1].equalsIgnoreCase("share")) {
                    if (Data.playerHomeData.get(player).containsKey(args[2]) == false) {
                        //没有找到
                        Util.sendCmdMessage(sender, 
                        ConfigReader.getBoolean("command.home.messagePerfix"), 
                        ConfigReader.getString("command.home.undefinedHome"));
                    } else {
                        String homeInfo = ConfigReader.getString("command.home.successShareMsg");
                        if (ConfigReader.getBoolean("command.home.messagePerfix")) {
                            homeInfo = ConfigReader.getString("perfix.text").replace('&', '\u00a7') + homeInfo;
                        }
                        homeInfo = homeInfo.replaceAll("#1", player.getName());
                        homeInfo = homeInfo.replaceAll("#2", args[2]);
                        homeInfo = homeInfo.replaceAll("#3", Data.playerHomeData.get(player).get(args[2]).getWorld().getName());
                        homeInfo = homeInfo.replaceAll("#4", Integer.toString((int)Data.playerHomeData.get(player).get(args[2]).getX()));
                        homeInfo = homeInfo.replaceAll("#5", Integer.toString((int)Data.playerHomeData.get(player).get(args[2]).getY()));
                        homeInfo = homeInfo.replaceAll("#6", Integer.toString((int)Data.playerHomeData.get(player).get(args[2]).getZ()));
                        homeInfo = homeInfo.replaceAll("#7", Data.playerHomeData.get(player).get(args[2]).getBlock().getBiome().toString().replace('_', ' ').toLowerCase());
                        Bukkit.broadcastMessage(homeInfo);
                    }
                    return true;
                } else if (args[1].equalsIgnoreCase("tp") || args[1].equalsIgnoreCase("teleport")) {
                    if (Data.playerHomeData.get(player).containsKey(args[2]) == false) {
                        //没有找到
                        Util.sendCmdMessage(sender, 
                        ConfigReader.getBoolean("command.home.messagePerfix"), 
                        ConfigReader.getString("command.home.undefinedHome"));
                    } else {
                        Data.playerLastLocation.put(player, player.getLocation());
                        player.teleport(Data.playerHomeData.get(player).get(args[2]));
                        Util.sendCmdMessage(sender, 
                        ConfigReader.getBoolean("command.home.messagePerfix"), 
                        ConfigReader.getString("command.home.successTpMsg").replaceAll("#1", args[2]));
                    }
                    return true;                           
                } else {
                    //错误参数
                    Util.sendCmdMessage(sender, 
                    ConfigReader.getBoolean("command.home.messagePerfix"), 
                    ConfigReader.getString("command.home.wrongArgMsg"));
                }
            }
        }
        return false;
    }

    private boolean cmdBack(CommandSender sender, Command command, String label, String[] args) {
        if (ConfigReader.getBoolean("command.back.active")) {
            if (!(sender instanceof Player)) {
                //不是玩家执行
                Util.sendCmdMessage(sender, 
                ConfigReader.getBoolean("command.back.messagePerfix"), 
                ConfigReader.getString("command.back.wrongSenderMsg"));
            } else if (((Player)sender).hasPermission("servermanager.back") == false) {
                //没有权限
                Util.sendCmdMessage(sender, 
                ConfigReader.getBoolean("command.back.messagePerfix"), 
                ConfigReader.getString("command.back.noPermissionMsg"));
            } else {
                Player player = (Player) sender;
                if (Data.playerLastLocation.containsKey(player)) {
                    Location destination = Data.playerLastLocation.get(player);
                    Data.playerLastLocation.put(player, player.getLocation());
                    player.teleport(destination);
                    String msg = ConfigReader.getString("command.back.successMsg");
                    msg = msg.replaceAll("#1", destination.getWorld().getName());
                    msg = msg.replaceAll("#2", Integer.toString((int)destination.getX()));
                    msg = msg.replaceAll("#3", Integer.toString((int)destination.getY()));
                    msg = msg.replaceAll("#4", Integer.toString((int)destination.getZ()));
                    msg = msg.replaceAll("#5", destination.getBlock().getBiome().toString().replace('_', ' ').toLowerCase());
                    Util.sendCmdMessage(sender, ConfigReader.getBoolean("command.back.messagePerfix"), msg);
                } else {
                    //没有上次位置
                    Util.sendCmdMessage(sender, 
                    ConfigReader.getBoolean("command.back.messagePerfix"), 
                    ConfigReader.getString("command.back.noLastLocationMsg"));
                }

                return true;
            }
        }
        return false;
    }

    /* 执行指令
     * servermanager 显示插件信息
     * servermanager broadcast 广播信息
     * servermanager broadcast bossbar <tick> <hasPerfix> <isForce> <Message> 显示boss栏消息
     * servermanager broadcast chat <hasPerfix> <Message> 在聊天栏播报信息
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return this.cmdServerManager(sender, command, label, args);
        }

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("broadcast")) {
                return this.cmdBroadcast(sender, command, label, args);
            } else if (args[0].equalsIgnoreCase("home")) {
                return this.cmdHome(sender, command, label, args);
            } else if (args[0].equalsIgnoreCase("back")) {
                return this.cmdBack(sender, command, label, args);
            }
        }
        return false;
    }
}
