/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.*;
import org.json.JSONObject;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.File;

/**
 * @author Hardik
 */
public class Add extends Command {
    private Config config;
    private PermissionManager manager;

    public Add() {
        super("Add", "Adds the Permission/User to the list", "add [" + Colors.DARK_BLUE + "Permission" + Colors.NORMAL + "/" + Colors.DARK_GREEN + "User" + Colors.NORMAL + "] [" + Colors.DARK_BLUE + "Permission" + Colors.NORMAL + "/" + Colors.DARK_GREEN + "User" + Colors.NORMAL + "]");
    }

    @Override
    public boolean execute(MessageEvent event) {
        File file = new File(System.getProperty("user.dir") + "/Perms/" + event.getChannel().getName() + "/" + "perms.json");
        if (!file.exists()) {
            JsonUtils.createJsonStructure(file);
        }
        String Jsonfile = System.getProperty("user.dir") + "/Perms/" + event.getChannel().getName() + "/" + "perms.json";
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
                        event.getUser().send().notice("Please Reload the permissions!");
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
                    String perms = JsonUtils.getStringFromFile(Jsonfile);
                    JSONObject jsonObj = new JSONObject(perms);
                    if (!jsonObj.getJSONObject("Perms").getString("ModPerms").contains(command)) {
                        jsonObj.getJSONObject("Perms").append("ModPerms", command);
                        JsonUtils.writeJsonFile(file, jsonObj.toString());
                        event.getUser().send().notice(command + " was added to the list!");
                        event.getUser().send().notice("Please Reload the permissions!");
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
                        event.getUser().send().notice("Please Reload the permissions!");
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
