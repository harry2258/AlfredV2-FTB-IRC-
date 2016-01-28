/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.*;
import com.harry2258.Alfred.json.Perms;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.harry2258.Alfred.api.CommandRegistry.commands;

/**
 * @author Hardik
 */
public class Add extends Command {
    private Config config;

    public Add() {
        super("Add", "Adds the Permission/User to the list", "Add [Group] [User/Permission]");
    }

    private static JsonElement Json(String[] list) {
        final JsonArray Array = new JsonArray();
        for (final String tmp : list) {
            final JsonPrimitive JsonList = new JsonPrimitive(tmp);
            Array.add(JsonList);
        }
        return Array;
    }

    private static boolean isAdded(User user, String channel, String[] type) {
        String sender = Main.Login.get(user.getNick());
        Perms perm = Main.map.get(channel.toLowerCase());
        if (sender == null) return false;
        if (type[1].equalsIgnoreCase("mod")) {
            for (String users : perm.getPermission().getMods()) {
                if (users.equalsIgnoreCase(sender)) {
                    return true;
                }
            }
        } else if (type[1].equalsIgnoreCase("admin")) {
            for (String users : perm.getPermission().getAdmins()) {
                if (users.equalsIgnoreCase(sender)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        String type = args[1];
        if (!config.useDatabase) {
            File file = new File(System.getProperty("user.dir") + "/perms/" + event.getChannel().getName().toLowerCase() + "/" + "perms.json");
            if (!file.exists()) {
                JsonUtils.createJsonStructure(file);
            }
            String Jsonfile = file.toString();
            String temp;

            if (type.equalsIgnoreCase("mod")) {
                if (args.length == 3) {
                    if (!event.getChannel().getUsers().contains(event.getBot().getUserChannelDao().getUser(args[2]))) {
                        MessageUtils.sendChannel(event, "There is no user by that name!");
                        return false;
                    }
                    User newUser = event.getBot().getUserChannelDao().getUser(args[2]);
                    String newuser = Utils.getAccount(event.getBot().getUserChannelDao().getUser(args[2]), event);
                    if (newuser.isEmpty()) {
                        return false;
                    }

                    String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                    JsonObject jsonObj = JsonUtils.getJsonObject(strFileJson);
                    if (!isAdded(newUser, event.getChannel().getName(), args)) {
                        temp = jsonObj.getAsJsonObject("Perms").get("Mods").toString();
                        String[] tempArray = (temp.replaceAll("[\\[\\]\"]", "") + "," + newuser + "").split(",");
                        jsonObj.getAsJsonObject("Perms").add("Mods", Json(tempArray));
                        JsonUtils.writeJsonFile(file, jsonObj.toString());
                        MessageUtils.sendUserNotice(event, newuser + " is now a Moderator for channel " + event.getChannel().getName());
                        String perms = JsonUtils.getStringFromFile(Jsonfile);
                        Perms p = JsonUtils.getPermsFromString(perms);
                        Main.map.put(event.getChannel().getName().toLowerCase(), p);
                        MessageUtils.sendUserNotice(event, "Reloaded Permissions");
                        event.getBot().getUserChannelDao().getUser(args[2]).send().notice("You are now a " + Colors.BOLD + "MODERATOR" + Colors.NORMAL + " for channel " + event.getChannel().getName());
                        return true;
                    } else {
                        MessageUtils.sendChannel(event, newuser + " is already on the list!");
                        return true;
                    }
                }
            }

            if (type.equalsIgnoreCase("modperms")) {
                if (args.length == 3) {
                    if (commands.containsKey(Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase()) || new File("plugins/" + Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase() + ".bsh").exists() || args[2].equalsIgnoreCase("custom") || args[2].equalsIgnoreCase("custom.command")) {
                        String check = args[2];
                        String command;
                        if (!check.contains("command.")) {
                            command = "command." + check;
                        } else {
                            command = check;
                        }
                        String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                        JsonObject jsonObj = JsonUtils.getJsonObject(strFileJson);
                        if (!jsonObj.getAsJsonObject("Perms").get("ModPerms").toString().contains(command)) {
                            temp = jsonObj.getAsJsonObject("Perms").get("ModPerms").toString();
                            String[] tempArray = (temp.replaceAll("[\\[\\]\"]", "") + "," + command + "").split(",");
                            jsonObj.getAsJsonObject("Perms").add("ModPerms", Json(tempArray));
                            JsonUtils.writeJsonFile(file, jsonObj.toString());
                            MessageUtils.sendUserNotice(event, "Moderators are now able to use the command '" + args[2] + "'");
                            String perms = JsonUtils.getStringFromFile(Jsonfile);
                            Perms p = JsonUtils.getPermsFromString(perms);
                            Main.map.put(event.getChannel().getName().toLowerCase(), p);
                            MessageUtils.sendUserNotice(event, "Reloaded Permissions");
                            return true;
                        } else {
                            MessageUtils.sendChannel(event, args[2] + " is already on the list!");
                            return true;
                        }
                    } else {
                        MessageUtils.sendChannel(event, "There is no command by that name!");
                        return true;
                    }
                }
            }

            if (type.equalsIgnoreCase("admin")) {
                if (args.length == 3) {
                    if (!event.getChannel().getUsers().contains(event.getBot().getUserChannelDao().getUser(args[2])) || new File("plugins/" + Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase() + ".bsh").exists()) {
                        MessageUtils.sendChannel(event, "There is no user by that name!");
                        return false;
                    }
                    User newUser = event.getBot().getUserChannelDao().getUser(args[2]);
                    String newuser = Utils.getAccount(event.getBot().getUserChannelDao().getUser(args[2]), event);
                    if (newuser.isEmpty()) {
                        return false;
                    }
                    String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                    JsonObject jsonObj = JsonUtils.getJsonObject(strFileJson);
                    if (!isAdded(newUser, event.getChannel().getName(), args)) {
                        temp = jsonObj.getAsJsonObject("Perms").get("Admins").toString();
                        String[] tempArray = (temp.replaceAll("[\\[\\]\"]", "") + "," + newuser + "").split(",");
                        jsonObj.getAsJsonObject("Perms").add("Admins", Json(tempArray));
                        JsonUtils.writeJsonFile(file, jsonObj.toString());
                        MessageUtils.sendUserNotice(event, newuser + " is now an Admin for channel " + event.getChannel().getName());
                        String perms = JsonUtils.getStringFromFile(Jsonfile);
                        Perms p = JsonUtils.getPermsFromString(perms);
                        Main.map.put(event.getChannel().getName().toLowerCase(), p);
                        MessageUtils.sendUserNotice(event, "Reloaded Permissions");
                        event.getBot().getUserChannelDao().getUser(args[2]).send().notice("You are now an " + Colors.BOLD + "ADMIN" + Colors.NORMAL + " for channel " + event.getChannel().getName());
                        return true;
                    } else {
                        MessageUtils.sendChannel(event, newuser + " is already on the list!");
                        return true;
                    }
                }
            }

            if (type.equalsIgnoreCase("everyone")) {
                if (args.length == 3) {
                    if (commands.containsKey(Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase()) || new File("plugins/" + Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase() + ".bsh").exists() || args[2].equalsIgnoreCase("custom") || args[2].equalsIgnoreCase("custom.command")) {
                        String check = args[2];
                        String command;
                        if (!check.contains("command.")) {
                            command = "command." + check;
                        } else {
                            command = check;
                        }

                        String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                        JsonObject jsonObj = JsonUtils.getJsonObject(strFileJson);
                        if (!jsonObj.getAsJsonObject("Perms").get("Everyone").toString().contains(command)) {
                            temp = jsonObj.getAsJsonObject("Perms").get("Everyone").toString();
                            String[] tempArray = (temp.replaceAll("[\\[\\]\"]", "") + "," + command + "").split(",");
                            jsonObj.getAsJsonObject("Perms").add("Everyone", Json(tempArray));
                            JsonUtils.writeJsonFile(file, jsonObj.toString());
                            MessageUtils.sendUserNotice(event, "Everyone is now able to use the command '" + args[2] + "'");
                            String perms = JsonUtils.getStringFromFile(Jsonfile);
                            Perms p = JsonUtils.getPermsFromString(perms);
                            Main.map.put(event.getChannel().getName().toLowerCase(), p);
                            MessageUtils.sendUserNotice(event, "Reloaded Permissions");
                            return true;
                        } else {
                            MessageUtils.sendChannel(event, args[2] + " is already on the list!");
                            return true;
                        }
                    } else {
                        MessageUtils.sendChannel(event, "There is no command by that name!");
                        return true;
                    }
                }
            }

            if (type.equalsIgnoreCase("global")) {
                if (PermissionManager.hasExec(event.getUser().getNick())) {
                    if (args.length == 3) {
                        if (commands.containsKey(Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase()) || new File("plugins/" + Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase() + ".bsh").exists()) {
                            String check = args[2];
                            String command;
                            if (!check.contains("command.")) {
                                command = "command." + check;
                            } else {
                                command = check;
                            }

                            String strFileJson = JsonUtils.getStringFromFile(Main.globalperm.toString());
                            JsonObject jsonObj = JsonUtils.getJsonObject(strFileJson);
                            if (!jsonObj.get("Permissions").toString().contains(command)) {
                                temp = jsonObj.getAsJsonObject("Permissions").toString();
                                String[] tempArray = (temp.replaceAll("[\\[\\]\"]", "") + "," + command + "").split(",");
                                jsonObj.add("Permissions", Json(tempArray));
                                JsonUtils.writeJsonFile(Main.globalperm, jsonObj.toString());
                                MessageUtils.sendUserNotice(event, args[2] + " was added to the list!");
                                MessageUtils.sendUserNotice(event, "Reloaded Permissions");
                                return true;
                            } else {
                                MessageUtils.sendChannel(event, args[2] + " is already on the list!");
                                return true;
                            }
                        } else {
                            MessageUtils.sendChannel(event, "There is no command by that name!");
                            return true;
                        }
                    }
                } else {
                    MessageUtils.sendUserNotice(event, "You need to be Exec to add permission to this group!");
                    return true;
                }
            }
        } else {
            String strFileJson;
            String channel;
            String temp;
            PreparedStatement stmt1 = Main.database.prepareStatement("SELECT Channel, Permission FROM `Channel_Permissions` WHERE Channel = '" + event.getChannel().getName() + "';");
            ResultSet rs1 = stmt1.executeQuery();
            rs1.next();
            strFileJson = rs1.getString("Permission");
            channel = rs1.getString("Channel");

            if (type.equalsIgnoreCase("mod")) {
                if (args.length == 3) {
                    if (!event.getChannel().getUsers().contains(event.getBot().getUserChannelDao().getUser(args[2])) || new File("plugins/" + Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase() + ".bsh").exists()) {
                        MessageUtils.sendChannel(event, "There is no user by that name!");
                        return false;
                    }

                    User newUser = event.getBot().getUserChannelDao().getUser(args[2]);
                    String newuser = Utils.getAccount(event.getBot().getUserChannelDao().getUser(args[2]), event);
                    if (newuser.isEmpty()) {
                        System.out.println("Could not get User info!");
                        return false;
                    }

                    if (!Main.Login.containsKey(newUser.getNick())) {
                        Main.Login.put(newUser.getNick(), newuser);
                    }

                    JsonObject jsonObj = JsonUtils.getJsonObject(strFileJson);
                    if (!isAdded(newUser, event.getChannel().getName(), args)) {
                        temp = jsonObj.getAsJsonObject("Perms").get("Mods").toString();
                        String[] tempArray = (temp.replaceAll("[\\[\\]\"]", "") + "," + newuser + "").split(",");
                        jsonObj.getAsJsonObject("Perms").add("Mods", Json(tempArray));
                        stmt1 = Main.database.prepareStatement("UPDATE `Channel_Permissions` SET `Permission` = '" + jsonObj.toString() + "' WHERE `Channel` = '" + channel + "';");
                        stmt1.execute();
                        MessageUtils.sendUserNotice(event, newuser + " is now an Moderator for channel " + event.getChannel().getName());
                        Perms p = JsonUtils.getPermsFromString(jsonObj.toString());
                        Main.map.put(event.getChannel().getName().toLowerCase(), p);
                        MessageUtils.sendUserNotice(event, "Reloaded Permissions");
                        event.getBot().getUserChannelDao().getUser(args[2]).send().notice("You are now an " + Colors.BOLD + "MODERATOR" + Colors.NORMAL + " for channel " + event.getChannel().getName());
                        return true;
                    } else {
                        MessageUtils.sendChannel(event, newuser + " is already on the list!");
                        return true;
                    }
                }
            }
            if (type.equalsIgnoreCase("modperms")) {
                if (args.length == 3) {
                    if (commands.containsKey(Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase()) || new File("plugins/" + Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase() + ".bsh").exists() || args[2].equalsIgnoreCase("custom") || args[2].equalsIgnoreCase("custom.command")) {
                        String check = args[2];
                        String command;
                        if (!check.contains("command.")) {
                            command = "command." + check;
                        } else {
                            command = check;
                        }

                        JsonObject jsonObj = JsonUtils.getJsonObject(strFileJson);
                        if (!jsonObj.getAsJsonObject("Perms").get("ModPerms").toString().contains(command)) {
                            temp = jsonObj.getAsJsonObject("Perms").get("ModPerms").toString();
                            String[] tempArray = (temp.replaceAll("[\\[\\]\"]", "") + "," + command + "").split(",");
                            jsonObj.getAsJsonObject("Perms").add("ModPerms", Json(tempArray));
                            stmt1 = Main.database.prepareStatement("UPDATE `Channel_Permissions` SET `Permission` = '" + jsonObj.toString() + "' WHERE `Channel` = '" + channel + "';");
                            stmt1.execute();
                            MessageUtils.sendUserNotice(event, "Moderators are now able to use the command '" + args[2] + "'");
                            Perms p = JsonUtils.getPermsFromString(jsonObj.toString());
                            Main.map.put(event.getChannel().getName().toLowerCase(), p);
                            MessageUtils.sendUserNotice(event, "Reloaded Permissions");
                            return true;
                        } else {
                            MessageUtils.sendChannel(event, args[2] + " is already on the list!");
                            return true;
                        }
                    } else {
                        MessageUtils.sendChannel(event, "There is no command by that name!");
                        return true;
                    }
                }
            }
            if (type.equalsIgnoreCase("admin")) {
                if (args.length == 3) {
                    if (!event.getChannel().getUsers().contains(event.getBot().getUserChannelDao().getUser(args[2])) || new File("plugins/" + Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase() + ".bsh").exists()) {
                        MessageUtils.sendChannel(event, "There is no user by that name!");
                        return false;
                    }

                    User newUser = event.getBot().getUserChannelDao().getUser(args[2]);
                    String newuser = Utils.getAccount(event.getBot().getUserChannelDao().getUser(args[2]), event);

                    if (newuser.isEmpty()) {
                        System.out.println("Could not get User info!");
                        return false;
                    }

                    if (!Main.Login.containsKey(newUser.getNick())) {
                        Main.Login.put(newUser.getNick(), newuser);
                    }

                    JsonObject jsonObj = JsonUtils.getJsonObject(strFileJson);
                    if (!isAdded(newUser, event.getChannel().getName(), args)) {
                        temp = jsonObj.getAsJsonObject("Perms").get("Admins").toString();
                        String[] tempArray = (temp.replaceAll("[\\[\\]\"]", "") + "," + newuser + "").split(",");
                        jsonObj.getAsJsonObject("Perms").add("Admins", Json(tempArray));
                        stmt1 = Main.database.prepareStatement("UPDATE `Channel_Permissions` SET `Permission` = '" + jsonObj.toString() + "' WHERE `Channel` = '" + channel + "';");
                        stmt1.execute();
                        MessageUtils.sendUserNotice(event, newuser + " is now an Admin for channel " + event.getChannel().getName());
                        Perms p = JsonUtils.getPermsFromString(jsonObj.toString());
                        Main.map.put(event.getChannel().getName().toLowerCase(), p);
                        MessageUtils.sendUserNotice(event, "Reloaded Permissions");
                        event.getBot().getUserChannelDao().getUser(args[2]).send().notice("You are now an " + Colors.BOLD + "ADMIN" + Colors.NORMAL + " for channel " + event.getChannel().getName());
                        return true;
                    } else {
                        MessageUtils.sendChannel(event, newuser + " is already on the list!");
                        return true;
                    }
                }
            }
            if (type.equalsIgnoreCase("everyone")) {

                if (args.length == 3) {
                    if (commands.containsKey(Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase()) || new File("plugins/" + Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase() + ".bsh").exists() || args[2].equalsIgnoreCase("custom") || args[2].equalsIgnoreCase("custom.command")) {
                        String check = args[2];
                        String command;
                        if (!check.contains("command.")) {
                            command = "command." + check;
                        } else {
                            command = check;
                        }

                        JsonObject jsonObj = JsonUtils.getJsonObject(strFileJson);
                        if (!jsonObj.getAsJsonObject("Perms").get("Everyone").toString().contains(command)) {
                            temp = jsonObj.getAsJsonObject("Perms").get("Everyone").toString();
                            String[] tempArray = (temp.replaceAll("[\\[\\]\"]", "") + "," + command + "").split(",");
                            jsonObj.getAsJsonObject("Perms").add("Everyone", Json(tempArray));
                            stmt1 = Main.database.prepareStatement("UPDATE `Channel_Permissions` SET `Permission` = '" + jsonObj.toString() + "' WHERE `Channel` = '" + channel + "';");
                            stmt1.execute();
                            MessageUtils.sendUserNotice(event, "Everyone is now able to use the command '" + args[2] + "'");
                            Perms p = JsonUtils.getPermsFromString(jsonObj.toString());
                            Main.map.put(event.getChannel().getName().toLowerCase(), p);
                            System.out.println(Main.map.get(event.getChannel().getName()) + "\n" + Main.map.get("#gazserver"));

                            MessageUtils.sendUserNotice(event, "Reloaded Permissions");
                            return true;
                        } else {
                            MessageUtils.sendChannel(event, args[2] + " is already on the list!");
                            return true;
                        }
                    } else {
                        MessageUtils.sendChannel(event, "There is no command by that name!");
                        return true;
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
        PermissionManager manager1 = manager;
    }

}
