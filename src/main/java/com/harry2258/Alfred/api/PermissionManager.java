/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.harry2258.Alfred.api;

import com.google.gson.JsonObject;
import com.harry2258.Alfred.Main;
import org.pircbotx.Channel;
import org.pircbotx.User;

import java.io.File;
import java.util.Properties;

public class PermissionManager {

    public Properties modproperties;
    public Properties properties;
    private Config configs;

    public PermissionManager(Config conf) {
        this.configs = conf;
    }

    public boolean hasPermission(String permission, User user, Channel channel, org.pircbotx.hooks.events.MessageEvent event) throws Exception {
        File file = new File(System.getProperty("user.dir") + "/perms/" + event.getChannel().getName().toLowerCase() + "/" + "perms.json");
        if (!file.exists()) {
            JsonUtils.createJsonStructure(file);
        }

        if (!Main.Login.containsKey(user.getNick())) {
            return false;
        }

        String nick;
        String hostname;

        if (!Main.map.containsKey(event.getChannel().getName())) {
            System.out.println("Perms are not inside HashMap!\nAdding!");
            String Jsonfile = System.getProperty("user.dir") + "/perms/" + event.getChannel().getName().toLowerCase() + "/" + "perms.json";
            String perms = JsonUtils.getStringFromFile(Jsonfile);
            Main.map.put(event.getChannel().getName(), perms);
        }

        String sender = Main.Login.get(user.getNick());
        String perms = Main.map.get(event.getChannel().getName());
        JsonObject jsonObj = JsonUtils.getJsonObject(perms);

        String Geveryone = JsonUtils.getStringFromFile(Main.globalperm.toString());
        JsonObject everyone = JsonUtils.getJsonObject(Geveryone);

        if (everyone.get("Permissions").getAsString().contains(permission)) {
            return true;
        }

        if (jsonObj.getAsJsonObject("Perms").get("Everyone").getAsString().contains(permission)) {
            return true;
        }

        if (jsonObj.getAsJsonObject("Perms").get("ModPerms").getAsString().contains(permission)) {
            if (jsonObj.getAsJsonObject("Perms").get("Admins").getAsString().contains(sender) || jsonObj.getAsJsonObject("Perms").get("Mods").getAsString().contains(sender) && user.isVerified()) {
                return true;
            }
        }

        if (jsonObj.getAsJsonObject("Perms").get("Admins").getAsString().contains(sender)) {
            return true;
        }

        String Exec = JsonUtils.getStringFromFile(JsonUtils.Jsonfile);
        JsonObject exec = JsonUtils.getJsonObject(Exec);

        return exec.getAsJsonObject("Perms").get("Exec").getAsString().contains(sender);

    }

    public static boolean hasExec(User user, org.pircbotx.hooks.events.MessageEvent event) {
        System.out.println("Checking for Exec perms!");
        String Exec = null;
        try {
            Exec = JsonUtils.getStringFromFile(Main.jsonFilePath.toString());

            JsonObject exec = JsonUtils.getJsonObject(Exec);

            if (exec.getAsJsonObject("Perms").get("Exec").getAsString().contains(Utils.getAccount(user, event))) {
                if (user.isVerified()) {
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
        String Admin = Main.map.get(event.getChannel().getName());
        JsonObject admin = JsonUtils.getJsonObject(Admin);

        if (admin.getAsJsonObject("Perms").get("Admins").getAsString().contains(sender)) {
            if (user.isVerified()) {
                return true;
            }
        }

        return false;
    }


    public PermissionManager getPermissionsManager() {
        return this;
    }
}
