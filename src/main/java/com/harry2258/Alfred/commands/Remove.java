/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.*;
import org.json.JSONObject;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Hardik
 */
public class Remove extends Command {
    private Config config;
    private PermissionManager manager;

    public Remove() {
        super("Remove", "Removes the Permission/User from the list", "remove [" + Colors.DARK_BLUE + "Permission" + Colors.NORMAL + "/" + Colors.DARK_GREEN + "User" + Colors.NORMAL + "] [" + Colors.DARK_BLUE + "Permission" + Colors.NORMAL + "/" + Colors.DARK_GREEN + "User" + Colors.NORMAL + "]");
    }

    @Override
    public boolean execute(MessageEvent event) {
        File file = new File(System.getProperty("user.dir") + "/perms/" + event.getChannel().getName().toLowerCase() + "/" + "perms.json");
        if (!file.exists()) {
            JsonUtils.createJsonStructure(file);
        }
        String Jsonfile = file.toString();
        String[] args = event.getMessage().split(" ");
        String type = args[1];
        ArrayList<String> temp = new ArrayList<>();
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
                    event.getBot().getUserChannelDao().getUser(args[2]).send().notice("You are no longer a " + Colors.BOLD + "MODERATOR" +  Colors.NORMAL+" for channel " + event.getChannel().getName());
                } else {
                    mod = args[2];
                }
                try {
                    String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                    JSONObject jsonObj = new JSONObject(strFileJson);
                    if (jsonObj.getJSONObject("Perms").getString("Mods").contains(mod)) {
                        for (int i = 0, length = jsonObj.getJSONObject("Perms").getJSONArray("Mods").length(); i < length; i++) {
                            temp.add(jsonObj.getJSONObject("Perms").getJSONArray("Mods").get(i).toString());
                        }
                        temp.remove(mod);
                        jsonObj.getJSONObject("Perms").remove("Mods");
                        jsonObj.getJSONObject("Perms").put("Mods", temp);

                        JsonUtils.writeJsonFile(file, jsonObj.toString());
                        event.getUser().send().notice(args[2] + " is no longer a Moderator for channel " + event.getChannel().getName());
                        Main.map.put(event.getChannel().getName(), JsonUtils.getStringFromFile(Jsonfile));
                        event.getUser().send().notice("Reloaded Permissions");
                        temp.clear();
                        if (inChan) {
                            event.getBot().getUserChannelDao().getUser(args[2]).send().notice("You are no longer a " + Colors.BOLD + "MODERATOR" +  Colors.NORMAL+" for channel " + event.getChannel().getName());
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
                    JSONObject jsonObj = new JSONObject(strFileJson);
                    if (jsonObj.getJSONObject("Perms").getString("ModPerms").contains(command)) {
                        for (int i = 0, length = jsonObj.getJSONObject("Perms").getJSONArray("ModPerms").length(); i < length; i++) {
                            temp.add(jsonObj.getJSONObject("Perms").getJSONArray("ModPerms").get(i).toString());
                        }
                        temp.remove(command);
                        jsonObj.getJSONObject("Perms").remove("ModPerms");
                        jsonObj.getJSONObject("Perms").put("ModPerms", temp);
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
                    JSONObject jsonObj = new JSONObject(strFileJson);
                    if (jsonObj.getJSONObject("Perms").getString("Admins").contains(mod)) {
                        for (int i = 0, length = jsonObj.getJSONObject("Perms").getJSONArray("Admins").length(); i < length; i++) {
                            temp.add(jsonObj.getJSONObject("Perms").getJSONArray("Admins").get(i).toString());
                        }
                        temp.remove(mod);
                        jsonObj.getJSONObject("Perms").remove("Admins");
                        jsonObj.getJSONObject("Perms").put("Admins", temp);

                        JsonUtils.writeJsonFile(file, jsonObj.toString());
                        event.getUser().send().notice(args[2] + " is no longer an Admin for channel " + event.getChannel().getName());
                        Main.map.put(event.getChannel().getName(), JsonUtils.getStringFromFile(Jsonfile));
                        event.getUser().send().notice("Reloaded Permissions");
                        if (inChan) {
                            event.getBot().getUserChannelDao().getUser(args[2]).send().notice("You are no longer an " + Colors.BOLD + "ADMIN" +  Colors.NORMAL+" for channel " + event.getChannel().getName());
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
                    JSONObject jsonObj = new JSONObject(strFileJson);
                    if (jsonObj.getJSONObject("Perms").getString("Everyone").contains(command)) {
                        for (int i = 0, length = jsonObj.getJSONObject("Perms").getJSONArray("Everyone").length(); i < length; i++) {
                            temp.add(jsonObj.getJSONObject("Perms").getJSONArray("Everyone").get(i).toString());
                        }
                        temp.remove(command);
                        jsonObj.getJSONObject("Perms").remove("Everyone");
                        jsonObj.getJSONObject("Perms").put("Everyone", temp);

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
                    JSONObject jsonObj = new JSONObject(strFileJson);
                    if (jsonObj.getString("Permissions").contains(command)) {
                        for (int i = 0, length = jsonObj.getJSONArray("Permissions").length(); i < length; i++) {
                            temp.add(jsonObj.getJSONArray("Permissions").get(i).toString());
                        }
                        temp.remove(command);
                        jsonObj.remove("Permissions");
                        jsonObj.put("Permissions", temp);

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
    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public void setManager(PermissionManager manager) {
        this.manager = manager;
    }

}
