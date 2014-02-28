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

import static com.harry2258.Alfred.api.CommandRegistry.commands;

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
    public boolean execute(MessageEvent event) throws Exception {
        File file = new File(System.getProperty("user.dir") + "/perms/" + event.getChannel().getName().toLowerCase() + "/" + "perms.json");
        if (!file.exists()) {
            JsonUtils.createJsonStructure(file);
        }
        String Jsonfile = file.toString();
        String[] args = event.getMessage().split(" ");
        String type = args[1];

        if (type.equalsIgnoreCase("mod")) {
            if (args.length == 3) {
                try {
                    if (!event.getChannel().getUsers().contains(event.getBot().getUserChannelDao().getUser(args[2]))) {
                        event.getChannel().send().message("There is no user by that name!");
                        return false;
                    }
                    String newuser = Utils.getAccount(event.getBot().getUserChannelDao().getUser(args[2]), event);
                    if (newuser.isEmpty()) {
                        return false;
                    }
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
                    event.getChannel().send().message("Please enter a valid username!");
                    return true;
                }
            }
        }

        if (type.equalsIgnoreCase("modperms")) {
            if (args.length == 3) {
                if (commands.containsKey(Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase())) {
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
                            event.getUser().send().notice(args[2] + " was added to the list!");
                            String perms = JsonUtils.getStringFromFile(Jsonfile);
                            Main.map.put(event.getChannel().getName(), perms);
                            event.getUser().send().notice("Reloaded Permissions");
                            return true;
                        } else {
                            event.getChannel().send().message(args[2] + " is already on the list!");
                            return true;
                        }
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                } else {
                    event.getChannel().send().message("There is no command by that name!");
                    return true;
                }
            }
        }

        if (type.equalsIgnoreCase("admin")) {
            System.out.println("Adding user to " + Jsonfile);
            if (args.length == 3) {
                try {
                    if (!event.getChannel().getUsers().contains(event.getBot().getUserChannelDao().getUser(args[2]))) {
                        event.getChannel().send().message("There is no user by that name!");
                        return false;
                    }
                    String newuser = Utils.getAccount(event.getBot().getUserChannelDao().getUser(args[2]), event);
                    if (newuser.isEmpty()) {
                        return false;
                    }
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
                    System.out.println("NOPE!");
                    System.out.println(ex);
                    event.getChannel().send().message("Please enter a valid username!");
                    return true;
                }
            }
        }

        if (type.equalsIgnoreCase("everyone")) {
            if (args.length == 3) {
                if (commands.containsKey(Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase())) {
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
                            event.getUser().send().notice(args[2] + " was added to the list!");
                            String perms = JsonUtils.getStringFromFile(Jsonfile);
                            Main.map.put(event.getChannel().getName(), perms);
                            event.getUser().send().notice("Reloaded Permissions");
                            return true;
                        } else {
                            event.getChannel().send().message(args[2] + " is already on the list!");
                            return true;
                        }
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                } else {
                    event.getChannel().send().message("There is no command by that name!");
                    return true;
                }
            }
        }
        if (type.equalsIgnoreCase("global")) {
            if (PermissionManager.hasExec(event.getUser(), event)) {
                if (args.length == 3) {
                    if (commands.containsKey(Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase())) {
                        try {
                            String check = args[2];
                            String command = null;
                            if (!check.contains("command.")) {
                                command = "command." + check;
                            } else {
                                command = check;
                            }

                            String strFileJson = JsonUtils.getStringFromFile(Main.globalperm.toString());
                            JSONObject jsonObj = new JSONObject(strFileJson);
                            if (!jsonObj.getString("Permissions").contains(command)) {
                                jsonObj.append("Permissions", command);
                                JsonUtils.writeJsonFile(Main.globalperm, jsonObj.toString());
                                event.getUser().send().notice(args[2] + " was added to the list!");
                                event.getUser().send().notice("Reloaded Permissions");
                                return true;
                            } else {
                                event.getChannel().send().message(args[2] + " is already on the list!");
                                return true;
                            }
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                    } else {
                        event.getChannel().send().message("There is no command by that name!");
                        return true;
                    }
                }
            } else {
                event.getUser().send().notice("You need to be Exec to add permission to this group!");
                return true;
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
