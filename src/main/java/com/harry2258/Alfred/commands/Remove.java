/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.*;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.File;

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
    public boolean execute (MessageEvent event) {
        File file = new File(System.getProperty("user.dir") + "/perms/" + event.getChannel().getName().toLowerCase() + "/" + "perms.json");
        if (!file.exists()) {
            JsonUtils.createJsonStructure(file);
        }
        String Jsonfile = file.toString();
        String[] args = event.getMessage().split(" ");
        String type = args[1];
        JsonArray temp = new JsonArray();
        Boolean inChan = false;
        if (type.equalsIgnoreCase("mod")) {
            System.out.println("Removing user from " + Jsonfile);
            if (args.length == 3) {
                String mod = args[2];
                if (event.getChannel().getUsers().contains(event.getBot().getUserChannelDao().getUser(args[2]))) {
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
                    String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                    JsonObject jsonObj = JsonUtils.getJsonObject(strFileJson);
                    if (jsonObj.getAsJsonObject("Perms").get("Mods").getAsString().contains(mod)) {
                        temp = (jsonObj.getAsJsonObject("Perms").getAsJsonArray("Mods"));
                        temp.remove(mod);
                        jsonObj.getAsJsonObject("Perms").remove("Mods");
                        jsonObj.getAsJsonObject("Perms").add("Mods", temp);

                        JsonUtils.writeJsonFile(file, jsonObj.toString());
                        event.getUser().send().notice(args[2] + " is no longer a Moderator for channel " + event.getChannel().getName());
                        Main.map.put(event.getChannel().getName(), JsonUtils.getStringFromFile(Jsonfile));
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
                    System.out.println(ex);
                }
            }
        }

        if (type.equalsIgnoreCase("modperms")) {
            if (args.length == 3) {
                String check = args[2];
                String command = null;
                if (!check.contains("command.")) {
                    command = "command." + check;
                } else {
                    command = check;
                }

                try {
                    String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                    JsonObject jsonObj = JsonUtils.getJsonObject(strFileJson);
                    if (jsonObj.getAsJsonObject("Perms").get("ModPerms").getAsString().contains(command)) {
                        temp = (jsonObj.getAsJsonObject("Perms").getAsJsonArray("ModPerms"));
                        temp.remove(command);
                        jsonObj.getAsJsonObject("Perms").remove("ModPerms");
                        jsonObj.getAsJsonObject("Perms").add("ModPerms", temp);
                        JsonUtils.writeJsonFile(file, jsonObj.toString());
                        event.getUser().send().notice("Moderators can no longer use the command '" + args[2] + "'");
                        Main.map.put(event.getChannel().getName(), JsonUtils.getStringFromFile(Jsonfile));
                        event.getUser().send().notice("Reloaded Permissions");
                        return true;
                    } else {
                        event.getChannel().send().message(command + " is not on the list!");
                        return true;
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
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
                    String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                    JsonObject jsonObj = JsonUtils.getJsonObject(strFileJson);
                    if (jsonObj.getAsJsonObject("Perms").get("Admins").getAsString().contains(mod)) {
                        temp = jsonObj.getAsJsonObject("Perms").getAsJsonArray("Admins");
                        temp.remove(mod);
                        jsonObj.getAsJsonObject("Perms").remove("Admins");
                        jsonObj.getAsJsonObject("Perms").add("Admins", temp);

                        JsonUtils.writeJsonFile(file, jsonObj.toString());
                        event.getUser().send().notice(args[2] + " is no longer an Admin for channel " + event.getChannel().getName());
                        Main.map.put(event.getChannel().getName(), JsonUtils.getStringFromFile(Jsonfile));
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
                    System.out.println(ex);
                }
            }
        }

        if (type.equalsIgnoreCase("everyone")) {
            if (args.length == 3) {
                String check = args[2];
                String command = null;
                if (!check.contains("command.")) {
                    command = "command." + check;
                } else {
                    command = check;
                }

                try {
                    String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                    JsonObject jsonObj = JsonUtils.getJsonObject(strFileJson);
                    if (jsonObj.getAsJsonObject("Perms").get("Everyone").getAsString().contains(command)) {
                        temp = (jsonObj.getAsJsonObject("Perms").getAsJsonArray("Everyone"));
                        temp.remove(command);
                        jsonObj.getAsJsonObject("Perms").remove("Everyone");
                        jsonObj.getAsJsonObject("Perms").add("Everyone", temp);

                        JsonUtils.writeJsonFile(file, jsonObj.toString());
                        String strJson = JsonUtils.getStringFromFile(JsonUtils.Jsonfile);
                        event.getUser().send().notice("Regular users can no longer use the command '" + args[2] + "'");
                        Main.map.put(event.getChannel().getName(), JsonUtils.getStringFromFile(Jsonfile));
                        event.getUser().send().notice("Reloaded Permissions");
                        return true;
                    } else {
                        event.getChannel().send().message(args[2] + " is not on the list!");
                        return true;
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }

        if (type.equalsIgnoreCase("global")) {
            if (args.length == 3) {
                String check = args[2];
                String command = null;
                if (!check.contains("command.")) {
                    command = "command." + check;
                } else {
                    command = check;
                }

                try {
                    String strFileJson = JsonUtils.getStringFromFile(Main.globalperm.toString());
                    JsonObject jsonObj = JsonUtils.getJsonObject(strFileJson);
                    if (jsonObj.get("Permissions").getAsString().contains(command)) {
                        temp = (jsonObj.getAsJsonArray("Permissions"));
                        temp.remove(command);
                        jsonObj.remove("Permissions");
                        jsonObj.add("Permissions", temp);

                        JsonUtils.writeJsonFile(Main.globalperm, jsonObj.toString());
                        event.getUser().send().notice(args[2] + " was removed from the list!");
                        return true;
                    } else {
                        event.getChannel().send().message(args[2] + " is not on the list!");
                        return true;
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }
        return false;
    }

    @Override
    public void setConfig (Config config) {
        this.config = config;
    }

    @Override
    public void setManager (PermissionManager manager) {
        this.manager = manager;
    }

}
