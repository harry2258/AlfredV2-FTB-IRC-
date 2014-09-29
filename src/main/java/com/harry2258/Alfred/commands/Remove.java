/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.*;
import com.harry2258.Alfred.json.Permission;
import com.harry2258.Alfred.json.Perms;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * @author Hardik
 */
public class Remove extends Command {
    private Config config;
    private PermissionManager manager;

    public Remove() {
        super("Remove", "Removes the Permission/User from the list", "remove [" + Colors.DARK_BLUE + "Permission" + Colors.NORMAL + "/" + Colors.DARK_GREEN + "User" + Colors.NORMAL + "] ["
                + Colors.DARK_BLUE + "Permission" + Colors.NORMAL + "/" + Colors.DARK_GREEN + "User" + Colors.NORMAL + "]");
    }

    @Override
    public boolean execute(MessageEvent event) {
        String[] args = event.getMessage().split(" ");
        String type = args[1];

        if (!config.useDatabase) {
            File file = new File(System.getProperty("user.dir") + "/perms/" + event.getChannel().getName().toLowerCase() + "/" + "perms.json");
            if (!file.exists()) {
                JsonUtils.createJsonStructure(file);
            }
            String Jsonfile = file.toString();
            List<String> temp;
            Boolean inChan = false;
            try {
                String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                Perms perm = JsonUtils.getPermsFromString(strFileJson);
                Permission p = perm.getPermission();

                if (type.equalsIgnoreCase("mod")) {
                    if (args.length == 3) {
                        String mod = args[2];
                        if (event.getChannel().getUsers().contains(event.getBot().getUserChannelDao().getUser(args[2]))) {
                            inChan = true;
                            try {
                                mod = Utils.getAccount(event.getBot().getUserChannelDao().getUser(args[2]), event);
                            } catch (Exception ex) {
                                System.out.println("Got an error while getting player login!");
                            }
                            event.getBot().getUserChannelDao().getUser(args[2]).send()
                                    .notice("You are no longer a " + Colors.BOLD + "MODERATOR" + Colors.NORMAL + " for channel " + event.getChannel().getName());
                        } else {
                            mod = args[2];
                        }
                        try {
                            if (p.getMods().contains(mod)) {
                                temp = p.getMods();
                                temp.remove(mod);
                                p.setMods(temp);
                                perm.setPermission(p);
                                JsonUtils.writeJsonFile(file, perm);
                                event.getUser().send().notice(args[2] + " is no longer a Moderator for channel " + event.getChannel().getName());
                                Main.map.put(event.getChannel().getName(), perm);
                                event.getUser().send().notice("Reloaded Permissions");
                                if (inChan) {
                                    event.getBot().getUserChannelDao().getUser(args[2]).send()
                                            .notice("You are no longer a " + Colors.BOLD + "MODERATOR" + Colors.NORMAL + " for channel " + event.getChannel().getName());
                                }
                                return true;
                            } else {
                                event.getChannel().send().message(args[2] + " is not on the list!");
                                return true;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                if (type.equalsIgnoreCase("modperms")) {
                    if (args.length == 3) {
                        String check = args[2];
                        String command;
                        if (!check.contains("command.")) {
                            command = "command." + check;
                        } else {
                            command = check;
                        }

                        try {
                            if (p.getModPerms().contains(command)) {
                                temp = (p.getModPerms());
                                temp.remove(command);
                                p.setModPerms(temp);
                                perm.setPermission(p);
                                JsonUtils.writeJsonFile(file, perm);
                                event.getUser().send().notice("Moderators can no longer use the command '" + args[2] + "'");
                                Main.map.put(event.getChannel().getName(), perm);
                                event.getUser().send().notice("Reloaded Permissions");
                                return true;
                            } else {
                                event.getChannel().send().message(command + " is not on the list!");
                                return true;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                if (type.equalsIgnoreCase("admin")) {
                    if (args.length == 3) {
                        String mod = args[2];
                        if (event.getChannel().getUsers().contains(event.getBot().getUserChannelDao().getUser(args[2]))) {
                            try {
                                mod = Utils.getAccount(event.getBot().getUserChannelDao().getUser(args[2]), event);
                            } catch (Exception ex) {
                                System.out.println("Got an error while getting player login!");
                            }
                            inChan = true;
                        } else {
                            mod = args[2];
                        }
                        try {
                            if (p.getAdmins().contains(mod)) {
                                temp = p.getAdmins();
                                temp.remove(mod);
                                p.setAdmins(temp);
                                perm.setPermission(p);
                                JsonUtils.writeJsonFile(file, perm);
                                event.getUser().send().notice(args[2] + " is no longer an Admin for channel " + event.getChannel().getName());
                                Main.map.put(event.getChannel().getName(), perm);
                                event.getUser().send().notice("Reloaded Permissions");
                                if (inChan) {
                                    event.getBot().getUserChannelDao().getUser(args[2]).send()
                                            .notice("You are no longer an " + Colors.BOLD + "ADMIN" + Colors.NORMAL + " for channel " + event.getChannel().getName());
                                }
                                return true;
                            } else {
                                event.getChannel().send().message(args[2] + " is not on the list!");
                                return true;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                if (type.equalsIgnoreCase("everyone")) {
                    if (args.length == 3) {
                        String check = args[2];
                        String command;
                        if (!check.contains("command.")) {
                            command = "command." + check;
                        } else {
                            command = check;
                        }

                        try {
                            if (p.getEveryone().contains(command)) {
                                temp = p.getEveryone();
                                temp.remove(command);
                                p.setEveryone(temp);
                                perm.setPermission(p);
                                JsonUtils.writeJsonFile(file, perm);
                                event.getUser().send().notice("Regular users can no longer use the command '" + args[2] + "'");
                                Main.map.put(event.getChannel().getName(), perm);
                                event.getUser().send().notice("Reloaded Permissions");
                                return true;
                            } else {
                                event.getChannel().send().message(args[2] + " is not on the list!");
                                return true;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                if (type.equalsIgnoreCase("global")) {
                    if (args.length == 3) {
                        String check = args[2];
                        String command;
                        if (!check.contains("command.")) {
                            command = "command." + check;
                        } else {
                            command = check;
                        }

                        try {
                            String s = JsonUtils.getStringFromFile(Main.globalperm.toString());
                            Perms perm2 = JsonUtils.getPermsFromString(s);
                            if (perm2.getGlobal().contains(command)) {
                                temp = perm2.getGlobal();
                                temp.remove(command);
                                perm2.setGlobal(temp);
                                JsonUtils.writeJsonFile(Main.globalperm, perm2);
                                event.getUser().send().notice(args[2] + " was removed from the list!");
                                return true;
                            } else {
                                event.getChannel().send().message(args[2] + " is not on the list!");
                                return true;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String strFileJson;
            String channel;

            List<String> temp;

            Boolean inChan = false;

            try {
                PreparedStatement stmt1 = Main.database.prepareStatement("SELECT Channel, Permission FROM `Channel_Permissions` WHERE Channel = '" + event.getChannel().getName() + "';");
                ResultSet rs1 = stmt1.executeQuery();
                rs1.next();
                strFileJson = rs1.getString("Permission");
                System.out.println(strFileJson);
                channel = rs1.getString("Channel");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            Perms perm = JsonUtils.getPermsFromString(strFileJson);
            Permission p = perm.getPermission();

            if (type.equalsIgnoreCase("mod")) {
                if (args.length == 3) {
                    String mod = args[2];
                    if (event.getChannel().getUsers().contains(event.getBot().getUserChannelDao().getUser(args[2]))) {
                        inChan = true;
                        try {
                            mod = Utils.getAccount(event.getBot().getUserChannelDao().getUser(args[2]), event);
                        } catch (Exception ex) {
                            System.out.println("Got an error while getting player login!");
                        }
                        event.getBot().getUserChannelDao().getUser(args[2]).send()
                                .notice("You are no longer a " + Colors.BOLD + "MODERATOR" + Colors.NORMAL + " for channel " + event.getChannel().getName());
                    } else {
                        mod = args[2];
                    }
                    try {
                        if (p.getMods().contains(mod)) {
                            temp = p.getMods();
                            temp.remove(mod);
                            p.setMods(temp);
                            perm.setPermission(p);
                            PreparedStatement stmt1 = Main.database.prepareStatement("UPDATE `Channel_Permissions` SET `Permission` = '" + JsonUtils.GSON.toJson(perm) + "' WHERE `Channel` = '" + channel + "';");
                            stmt1.execute();
                            event.getUser().send().notice(args[2] + " is no longer a Moderator for channel " + event.getChannel().getName());
                            Main.map.put(event.getChannel().getName(), perm);
                            event.getUser().send().notice("Reloaded Permissions");
                            if (inChan) {
                                event.getBot().getUserChannelDao().getUser(args[2]).send()
                                        .notice("You are no longer a " + Colors.BOLD + "MODERATOR" + Colors.NORMAL + " for channel " + event.getChannel().getName());
                            }
                            return true;
                        } else {
                            event.getChannel().send().message(args[2] + " is not on the list!");
                            return true;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            if (type.equalsIgnoreCase("modperms")) {
                if (args.length == 3) {
                    String check = args[2];
                    String command;
                    if (!check.contains("command.")) {
                        command = "command." + check;
                    } else {
                        command = check;
                    }

                    try {
                        if (p.getModPerms().contains(command)) {
                            temp = (p.getModPerms());
                            temp.remove(command);
                            p.setModPerms(temp);
                            perm.setPermission(p);
                            PreparedStatement stmt1 = Main.database.prepareStatement("UPDATE `Channel_Permissions` SET `Permission` = '" + JsonUtils.GSON.toJson(perm) + "' WHERE `Channel` = '" + channel + "';");
                            stmt1.execute();
                            event.getUser().send().notice("Moderators can no longer use the command '" + args[2] + "'");
                            Main.map.put(event.getChannel().getName(), perm);
                            event.getUser().send().notice("Reloaded Permissions");
                            return true;
                        } else {
                            event.getChannel().send().message(command + " is not on the list!");
                            return true;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            if (type.equalsIgnoreCase("admin")) {
                if (args.length == 3) {
                    String mod = args[2];
                    if (event.getChannel().getUsers().contains(event.getBot().getUserChannelDao().getUser(args[2]))) {
                        try {
                            mod = Utils.getAccount(event.getBot().getUserChannelDao().getUser(args[2]), event);
                        } catch (Exception ex) {
                            System.out.println("Got an error while getting player login!");
                        }
                        inChan = true;
                    } else {
                        mod = args[2];
                    }
                    try {
                        if (p.getAdmins().contains(mod)) {
                            temp = p.getAdmins();
                            temp.remove(mod);
                            p.setAdmins(temp);
                            perm.setPermission(p);
                            PreparedStatement stmt1 = Main.database.prepareStatement("UPDATE `Channel_Permissions` SET `Permission` = '" + JsonUtils.GSON.toJson(perm) + "' WHERE `Channel` = '" + channel + "';");
                            stmt1.execute();
                            event.getUser().send().notice(args[2] + " is no longer an Admin for channel " + event.getChannel().getName());
                            Main.map.put(event.getChannel().getName(), perm);
                            event.getUser().send().notice("Reloaded Permissions");
                            if (inChan) {
                                event.getBot().getUserChannelDao().getUser(args[2]).send()
                                        .notice("You are no longer an " + Colors.BOLD + "ADMIN" + Colors.NORMAL + " for channel " + event.getChannel().getName());
                            }
                            return true;
                        } else {
                            event.getChannel().send().message(args[2] + " is not on the list!");
                            return true;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            if (type.equalsIgnoreCase("everyone")) {
                if (args.length == 3) {
                    String check = args[2];
                    String command;
                    if (!check.contains("command.")) {
                        command = "command." + check;
                    } else {
                        command = check;
                    }

                    try {
                        if (p.getEveryone().contains(command)) {
                            temp = p.getEveryone();
                            temp.remove(command);
                            p.setEveryone(temp);
                            perm.setPermission(p);
                            PreparedStatement stmt1 = Main.database.prepareStatement("UPDATE `Channel_Permissions` SET `Permission` = '" + JsonUtils.GSON.toJson(perm) + "' WHERE `Channel` = '" + channel + "';");
                            stmt1.execute();
                            event.getUser().send().notice("Regular users can no longer use the command '" + args[2] + "'");
                            Main.map.put(event.getChannel().getName(), perm);
                            event.getUser().send().notice("Reloaded Permissions");
                            return true;
                        } else {
                            event.getChannel().send().message(args[2] + " is not on the list!");
                            return true;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public void setManager(PermissionManager manager) {
        this.manager = manager;
    }

}
