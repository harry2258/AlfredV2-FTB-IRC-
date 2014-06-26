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

import java.io.File;
import java.util.Properties;

public class PermissionManager {

    public Properties properties;

    public PermissionManager(Config conf) {
    }

    public boolean hasPermission(String permission, User user, Channel channel, org.pircbotx.hooks.events.MessageEvent event) throws Exception {
        try {
            File file = new File(System.getProperty("user.dir") + "/perms/" + channel.getName().toLowerCase() + "/" + "perms.json");
            if (!file.exists()) {
                JsonUtils.createJsonStructure(file);
            }

            if (!Main.Login.containsKey(user.getNick())) {
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
                if (hasAdmin(user, event) || hasMod(user, event) && user.isVerified()) {
                    return true;
                }
            }

            return hasAdmin(user, event) || hasExec(user, event);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasExec(User user, org.pircbotx.hooks.events.MessageEvent event) {
        try {
            String Exec = JsonUtils.getStringFromFile(Main.jsonFilePath.toString());
            JsonObject exec = JsonUtils.getJsonObject(Exec);
            String account = Utils.getAccount(user, event);
            for (String users : exec.getAsJsonObject("Perms").get("Exec").toString().replaceAll("[\\[\\]\"]", "").split(",")) {
                if (users.equalsIgnoreCase(account) && user.isVerified()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasAdmin(User user, org.pircbotx.hooks.events.MessageEvent event) throws Exception {

        String sender = Main.Login.get(user.getNick());
        Perms perm = Main.map.get(event.getChannel().getName());
        for (String users : perm.getPermission().getAdmins()) {
            if (users.equalsIgnoreCase(sender) && user.isVerified()) {
                return true;
            }
        }
        return false;
    }

    public PermissionManager getPermissionsManager() {
        return this;
    }

    public static boolean hasMod(User user, org.pircbotx.hooks.events.MessageEvent event) throws Exception {
        String sender = Main.Login.get(user.getNick());
        Perms perm = Main.map.get(event.getChannel().getName());
        for (String users : perm.getPermission().getMods()) {
            if (users.equalsIgnoreCase(sender) && user.isVerified()) {
                return true;
            }
        }
        return false;
    }
}
