/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.harry2258.Alfred.api;

import com.harry2258.Alfred.Main;
import org.json.JSONObject;
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
        JSONObject jsonObj = new JSONObject(perms);

        String Geveryone = JsonUtils.getStringFromFile(Main.globalperm.toString());
        JSONObject everyone = new JSONObject(Geveryone);

        if (everyone.getString("Permissions").contains(permission)) {
            return true;
        }

        if (jsonObj.getJSONObject("Perms").getString("Everyone").contains(permission)) {
            return true;
        }

        if (jsonObj.getJSONObject("Perms").getString("ModPerms").contains(permission)) {
            if (jsonObj.getJSONObject("Perms").getString("Mods").contains(sender) && user.isVerified() || channel.hasVoice(user)) {
                return true;
            }
        }

        if (jsonObj.getJSONObject("Perms").getString("Admins").contains(sender)) {
            return true;
        }

        String Exec = JsonUtils.getStringFromFile(JsonUtils.Jsonfile);
        JSONObject exec = new JSONObject(Exec);

        if (exec.getJSONObject("Perms").getString("Exec").contains(sender)) {
            return true;
        }

        return false;
    }

    public static boolean hasExec(User user, org.pircbotx.hooks.events.MessageEvent event) throws Exception {
        System.out.println("Checking for Exec perms!");
        String Exec = JsonUtils.getStringFromFile(JsonUtils.Jsonfile);
        JSONObject exec = new JSONObject(Exec);

        if (exec.getJSONObject("Perms").getString("Exec").contains(Utils.getAccount(user, event))) {
            if (user.isVerified()) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasAdmin(User user, org.pircbotx.hooks.events.MessageEvent event) throws Exception {

        String sender = Main.Login.get(user.getNick());
        String Admin = Main.map.get(event.getChannel().getName());
        JSONObject admin = new JSONObject(Admin);

        if (admin.getJSONObject("Perms").getString("Admins").contains(sender)) {
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
