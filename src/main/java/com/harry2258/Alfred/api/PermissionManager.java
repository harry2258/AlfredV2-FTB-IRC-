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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class PermissionManager {

    public Properties properties;

    public PermissionManager() {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasAdmin(String Nick, Channel channel) {

        String sender = Main.Login.get(Nick);
        Perms perm = Main.map.get(channel.getName().toLowerCase());
        Set<String> set = new HashSet<>(perm.getPermission().getAdmins());
        return set.contains(sender);
    }

    private static boolean hasMod(String Nick, Channel channel) {
        String sender = Main.Login.get(Nick);
        Perms perm = Main.map.get(channel.getName().toLowerCase());
        Set<String> set = new HashSet<>(perm.getPermission().getMods());
        return set.contains(sender);
    }

    public boolean hasPermission(String permission, String Nick, Channel channel) {
        try {
            //String Nick = user.getNick();
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

            if (hasAdmin(Nick, channel) || hasExec(Nick)) {
                return true;
            }

            //Global Permissions everyone has, Not channel based
            if (everyone.get("Permissions").toString().contains(permission)) {
                return true;
            }

            //Permissions everyone has based on current channel
            if (p.getEveryone().contains(permission)) {
                return true;
            }

            if (p.getModPerms().contains(permission) && hasMod(Nick, channel)) {
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public PermissionManager getPermissionsManager() {
        return this;
    }
}
