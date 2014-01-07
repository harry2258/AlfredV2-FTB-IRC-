/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.api;

import org.pircbotx.User;
import org.pircbotx.Channel;

import java.util.Properties;
import org.json.JSONObject;

public class PermissionManager {

    public Properties modproperties;
    public Properties properties;
    private Config configs;
    
    public PermissionManager(Config conf) {
        this.configs = conf;
    }

    public boolean hasPermission(String permission, User user, Channel channel, org.pircbotx.hooks.events.MessageEvent event) throws Exception {
        boolean hostmatch = false;
        boolean nickmatch = false;
        boolean permmatch = false;
        String nick;
        String hostname;
        
        if (configs.isAdmin(user.getNick(), user.getHostmask())) {
            return true;
        }
        String Jsonfile = System.getProperty("user.dir") + "/Perms/" + channel.getName();
        String perms = JsonUtils.getStringFromFile(Jsonfile);
        JSONObject jsonObj = new JSONObject(perms);
        
        if (jsonObj.getJSONObject("Perms").getString("Everyone").contains(permission)) {
            if (user.isVerified()) {
            return true;
            } else { user.send().notice("You need to be logged in to use this!");}
        }
        
        if (jsonObj.getJSONObject("Perms").getString("ModPerms").contains(permission)) {
            if (jsonObj.getJSONObject("Perms").getString("Mods").contains(Utils.getAccount(user, event)) && user.isVerified() || channel.hasVoice(user) ) {
            return true;
            }
        }
        
        if (jsonObj.getJSONObject("Perms").getString("Admins").contains(Utils.getAccount(user, event))) {
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
