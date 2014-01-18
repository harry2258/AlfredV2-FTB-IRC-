/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.*;
import org.json.JSONObject;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.File;

/**
 * @author Hardik
 */
public class Add extends Command {
    private Config config;
    private PermissionManager manager;

    public Add() {
        super("Add", "Adds the Permission/User to the list", "There a 4 groups to add people/permissions to. Everyone: If the user is logged in, they can use permission listed in here. Modperms: If the user ia a Mod, they can use permission listed in here. Mod: Add an user to Mod list to allows them to use Modperms. Admin: Have access to almost all commands, except few critical ones. | add [mod] [batman]");
    }

    @Override
    public boolean execute(MessageEvent event) {
        File file = new File(System.getProperty("user.dir") + "/perms/" + event.getChannel().getName().toLowerCase() + "/" + "perms.json");
        if (!file.exists()) {
            JsonUtils.createJsonStructure(file);
        }
        String Jsonfile = System.getProperty("user.dir") + "/perms/" + event.getChannel().getName().toLowerCase() + "/" + "perms.json";
        String[] args = event.getMessage().split(" ");
        String type = args[1];

        if (type.equalsIgnoreCase("mod")) {
            if (args.length == 3) {
                try {
                    String newuser = Utils.getAccount(event.getBot().getUserChannelDao().getUser(args[2]), event);
                    String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                    JSONObject jsonObj = new JSONObject(strFileJson);
                    if (!jsonObj.getJSONObject("Perms").getString("Mods").contains(newuser)) {
                        jsonObj.getJSONObject("Perms").append("Mods", newuser);
                        JsonUtils.writeJsonFile(file, jsonObj.toString());
                        event.getUser().send().notice(newuser + " was added to the list!");
                        String perms = JsonUtils.getStringFromFile(Jsonfile);
                        Main.map.put(event.getChannel().getName(), perms);
                        event.getUser().send().notice("Reloaded Permissions");
                        return true;
                    } else {
                        event.getChannel().send().message(newuser + " is already on the list!");
                        return true;
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }

        if (type.equalsIgnoreCase("modperms")) {
            if (args.length == 3) {
                try {
                    String check = args[2];
                    String command = null;
                    if (!check.contains("command.")) {
                        command = "command." + check;
                    } else {
                        command = check;
                    }
                    String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                    JSONObject jsonObj = new JSONObject(strFileJson);
                    if (!jsonObj.getJSONObject("Perms").getString("ModPerms").contains(command)) {
                        jsonObj.getJSONObject("Perms").append("ModPerms", command);
                        JsonUtils.writeJsonFile(file, jsonObj.toString());
                        event.getUser().send().notice(command + " was added to the list!");
                        String perms = JsonUtils.getStringFromFile(Jsonfile);
                        Main.map.put(event.getChannel().getName(), perms);
                        event.getUser().send().notice("Reloaded Permissions");
                        return true;
                    } else {
                        event.getChannel().send().message(command + " is already on the list!");
                        return true;
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }

        if (type.equalsIgnoreCase("admin")) {
            if (args.length == 3) {
                try {
                    String newuser = Utils.getAccount(event.getBot().getUserChannelDao().getUser(args[2]), event);
                    String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                    JSONObject jsonObj = new JSONObject(strFileJson);
                    if (!jsonObj.getJSONObject("Perms").getString("Admins").contains(newuser)) {
                        jsonObj.getJSONObject("Perms").append("Admins", newuser);
                        JsonUtils.writeJsonFile(file, jsonObj.toString());
                        event.getUser().send().notice(newuser + " was added to the list!");
                        String perms = JsonUtils.getStringFromFile(Jsonfile);
                        Main.map.put(event.getChannel().getName(), perms);
                        event.getUser().send().notice("Reloaded Permissions");
                        return true;
                    } else {
                        event.getChannel().send().message(newuser + " is already on the list!");
                        return true;
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }

        if (type.equalsIgnoreCase("everyone")) {
            if (args.length == 3) {
                try {
                    String check = args[2];
                    String command = null;
                    if (!check.contains("command.")) {
                        command = "command." + check;
                    } else {
                        command = check;
                    }

                    String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                    JSONObject jsonObj = new JSONObject(strFileJson);
                    if (!jsonObj.getJSONObject("Perms").getString("Everyone").contains(command)) {
                        jsonObj.getJSONObject("Perms").append("Everyone", command);
                        JsonUtils.writeJsonFile(file, jsonObj.toString());
                        event.getUser().send().notice(command + " was added to the list!");
                        String perms = JsonUtils.getStringFromFile(Jsonfile);
                        Main.map.put(event.getChannel().getName(), perms);
                        event.getUser().send().notice("Reloaded Permissions");
                        return true;
                    } else {
                        event.getChannel().send().message(command + " is already on the list!");
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
