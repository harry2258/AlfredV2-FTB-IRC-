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

    //TODO FIX THIS SHIT!
    @Override
    public boolean execute(MessageEvent event) throws Exception {
        File file = new File(System.getProperty("user.dir") + "/perms/" + event.getChannel().getName().toLowerCase() + "/" + "perms.json");
        if (!file.exists()) {
            JsonUtils.createJsonStructure(file);
        }
        String Jsonfile = file.toString();
        String[] args = event.getMessage().split(" ");
        String type = args[1];
        String temp;

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
                    JsonObject jsonObj = JsonUtils.getJsonObject(strFileJson);
                    if (!isAdded(event.getUser(), event, args)) {
                        temp = jsonObj.getAsJsonObject("Perms").get("Mods").toString();
                        String[] tempArray = (temp.replaceAll("[\\[\\]\"]", "") + "," + newuser + "").split(",");
                        jsonObj.getAsJsonObject("Perms").add("Mods", Json(tempArray));
                        JsonUtils.writeJsonFile(file, jsonObj.toString());
                        event.getUser().send().notice(newuser + " is now a Moderator for channel " + event.getChannel().getName());
                        String perms = JsonUtils.getStringFromFile(Jsonfile);
                        Perms p = JsonUtils.getPermsFromString(perms);
                        Main.map.put(event.getChannel().getName(), p);
                        event.getUser().send().notice("Reloaded Permissions");
                        event.getBot().getUserChannelDao().getUser(args[2]).send().notice("You are now a " + Colors.BOLD + "MODERATOR" + Colors.NORMAL + " for channel " + event.getChannel().getName());
                        return true;
                    } else {
                        event.getChannel().send().message(newuser + " is already on the list!");
                        return true;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    event.getChannel().send().message("Please enter a valid username!");
                    return true;
                }
            }
        }

        if (type.equalsIgnoreCase("modperms")) {
            if (args.length == 3) {
                if (commands.containsKey(Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase()) || new File("plugins/" + Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase() + ".bsh").exists()) {
                    try {
                        String check = args[2];
                        String command = null;
                        if (!check.contains("command.")) {
                            command = "command." + check;
                        } else {
                            command = check;
                        }
                        String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                        JsonObject jsonObj = JsonUtils.getJsonObject(strFileJson);
                        System.out.println(jsonObj.getAsJsonObject("Perms").get("ModPerms").toString());
                        if (!jsonObj.getAsJsonObject("Perms").get("ModPerms").toString().contains(command)) {
                            temp = jsonObj.getAsJsonObject("Perms").get("ModPerms").toString();
                            String[] tempArray = (temp.replaceAll("[\\[\\]\"]", "") + "," + command + "").split(",");
                            jsonObj.getAsJsonObject("Perms").add("ModPerms", Json(tempArray));
                            JsonUtils.writeJsonFile(file, jsonObj.toString());
                            event.getUser().send().notice("Moderators are now able to use the command '" + args[2] + "'");
                            String perms = JsonUtils.getStringFromFile(Jsonfile);
                            Perms p = JsonUtils.getPermsFromString(perms);
                            Main.map.put(event.getChannel().getName(), p);
                            event.getUser().send().notice("Reloaded Permissions");
                            return true;
                        } else {
                            event.getChannel().send().message(args[2] + " is already on the list!");
                            return true;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    event.getChannel().send().message("There is no command by that name!");
                    return true;
                }
            }
        }

        if (type.equalsIgnoreCase("admin")) {
            if (args.length == 3) {
                try {
                    if (!event.getChannel().getUsers().contains(event.getBot().getUserChannelDao().getUser(args[2])) || new File("plugins/" + Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase() + ".bsh").exists()) {
                        event.getChannel().send().message("There is no user by that name!");
                        return false;
                    }
                    String newuser = Utils.getAccount(event.getBot().getUserChannelDao().getUser(args[2]), event);
                    if (newuser.isEmpty()) {
                        return false;
                    }
                    String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                    JsonObject jsonObj = JsonUtils.getJsonObject(strFileJson);
                    if (!isAdded(event.getUser(), event, args)) {
                        temp = jsonObj.getAsJsonObject("Perms").get("Admins").toString();
                        String[] tempArray = (temp.replaceAll("[\\[\\]\"]", "") + "," + newuser + "").split(",");
                        jsonObj.getAsJsonObject("Perms").add("Admins", Json(tempArray));
                        JsonUtils.writeJsonFile(file, jsonObj.toString());
                        event.getUser().send().notice(newuser + " is now an Admin for channel " + event.getChannel().getName());
                        String perms = JsonUtils.getStringFromFile(Jsonfile);
                        Perms p = JsonUtils.getPermsFromString(perms);
                        Main.map.put(event.getChannel().getName(), p);
                        event.getUser().send().notice("Reloaded Permissions");
                        event.getBot().getUserChannelDao().getUser(args[2]).send().notice("You are now an " + Colors.BOLD + "ADMIN" + Colors.NORMAL + " for channel " + event.getChannel().getName());
                        return true;
                    } else {
                        event.getChannel().send().message(newuser + " is already on the list!");
                        return true;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    event.getChannel().send().message("Please enter a valid username!");
                    return true;
                }
            }
        }

        if (type.equalsIgnoreCase("everyone")) {
            if (args.length == 3) {
                if (commands.containsKey(Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase()) || new File("plugins/" + Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase() + ".bsh").exists()) {
                    try {
                        String check = args[2];
                        String command = null;
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
                            event.getUser().send().notice("Everyone is now able to use the command '" + args[2] + "'");
                            String perms = JsonUtils.getStringFromFile(Jsonfile);
                            Perms p = JsonUtils.getPermsFromString(perms);
                            Main.map.put(event.getChannel().getName(), p);
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
                    if (commands.containsKey(Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase()) || new File("plugins/" + Character.toUpperCase(args[2].charAt(0)) + event.getMessage().split(" ")[2].substring(1).toLowerCase() + ".bsh").exists()) {
                        try {
                            String check = args[2];
                            String command = null;
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

    private JsonElement Json(String[] list) {
        final JsonArray Array = new JsonArray();
        for (final String tmp : list) {
            final JsonPrimitive JsonList = new JsonPrimitive(tmp);
            Array.add(JsonList);
        }
        return Array;
    }

    @Override
    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public void setManager(PermissionManager manager) {
        this.manager = manager;
    }

    public static boolean isAdded(User user, org.pircbotx.hooks.events.MessageEvent event, String[] type) throws Exception {
        String sender = Main.Login.get(user.getNick());
        Perms perm = Main.map.get(event.getChannel().getName());

        if (type[1].equalsIgnoreCase("mod")) {
            for (String users : perm.getPermission().getMods()) {
                if (users.equalsIgnoreCase(sender) && user.isVerified()) {
                    return true;
                }
            }
        } else if (type[1].equalsIgnoreCase("admin")) {
            for (String users : perm.getPermission().getAdmins()) {
                if (users.equalsIgnoreCase(sender) && user.isVerified()) {
                    return true;
                }
            }
        }

        for (String users : perm.getPermission().getMods()) {
            if (users.equalsIgnoreCase(sender) && user.isVerified()) {
                return true;
            }
        }
        return false;
    }

}
