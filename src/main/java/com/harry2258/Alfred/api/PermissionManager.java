/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.harry2258.Alfred.api;

import com.google.gson.JsonObject;
import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.json.Permission;
import com.harry2258.Alfred.json.Perms;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.File;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class PermissionManager {

    public Properties properties;

    public PermissionManager(Config conf) {
    }

    public boolean hasPermission(String permission, User user, Channel channel, MessageEvent event) throws Exception {
        try {
            String Nick = user.getNick();
            File file = new File(System.getProperty("user.dir") + "/perms/" + channel.getName().toLowerCase() + "/" + "perms.json");
            if (!file.exists()) {
                JsonUtils.createJsonStructure(file);
            }

            if (!Main.Login.containsKey(Nick)) {
                return false;
            }

            if (!Main.map.containsKey(channel.getName())) {
                String Jsonfile = System.getProperty("user.dir") + "/perms/" + channel.getName().toLowerCase() + "/" + "perms.json";
                String perms = JsonUtils.getStringFromFile(Jsonfile);

                Main.map.put(channel.getName(), JsonUtils.getPermsFromString(perms));
            }

            Perms perms = Main.map.get(channel.getName());
            Permission p = perms.getPermission();
            String Geveryone = JsonUtils.getStringFromFile(Main.globalperm.toString());
            JsonObject everyone = JsonUtils.getJsonObject(Geveryone);


            if (everyone.get("Permissions").toString().contains(permission)) {
                return true;
            }

            if (p.getEveryone().contains(permission)) {
                return true;
            }

            if (p.getModPerms().contains(permission)) {
                if (hasAdmin(Nick, event) || hasMod(Nick, event)) {
                    return true;
                }
            }

            return hasAdmin(Nick, event) || hasExec(Nick);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasExec(String Nick) {
        try {
            String Exec = JsonUtils.getStringFromFile(Main.jsonFilePath.toString());
            JsonObject exec = JsonUtils.getJsonObject(Exec);
            String account = Main.Login.get(Nick);
            for (String users : exec.getAsJsonObject("Perms").get("Exec").toString().replaceAll("[\\[\\]\"]", "").split(",")) {
                if (users.equalsIgnoreCase(account)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasAdmin(String Nick, MessageEvent event) throws Exception {

        String sender = Main.Login.get(Nick);
        Perms perm = Main.map.get(event.getChannel().getName().toLowerCase());
        Set<String> set = new HashSet<>(perm.getPermission().getAdmins());
        return set.contains(sender);
    }

    public PermissionManager getPermissionsManager() {
        return this;
    }

    public static boolean hasMod(String Nick, MessageEvent event) throws Exception {
        String sender = Main.Login.get(Nick);
        Perms perm = Main.map.get(event.getChannel().getName().toLowerCase());
        Set<String> set = new HashSet<>(perm.getPermission().getMods());
        return set.contains(sender);
    }
}
