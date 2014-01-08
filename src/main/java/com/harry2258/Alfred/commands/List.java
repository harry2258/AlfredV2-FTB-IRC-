package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.JsonUtils;
import com.harry2258.Alfred.api.PermissionManager;
import org.json.JSONObject;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/8/14.
 */

public class List extends Command {
    private Config config;
    private PermissionManager manager;

    public List() {
        super("List", "List Permissions that people have", "List");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String temp;
        String mod;
        String modpermissions;
        String everyone;
        String Admins;

        String Jsonfile = System.getProperty("user.dir") + "/Perms/" + event.getChannel().getName() + "/" +  "perms.json";
        String perms = JsonUtils.getStringFromFile(Jsonfile);
        JSONObject jsonObj = new JSONObject(perms);

        temp = jsonObj.getJSONObject("Perms").getString("Everyone");
        everyone = "Permissions everyone have: " + temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ");

        temp = jsonObj.getJSONObject("Perms").getString("ModPerms");
        modpermissions = "Mod Permissions: "+ temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ");

        temp = jsonObj.getJSONObject("Perms").getString("Mods");
        mod = "Mods: "+ temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ");

        temp = jsonObj.getJSONObject("Perms").getString("Admins");
        Admins = "Admins: "+ temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ");

        event.getUser().send().notice(everyone); //Everyone Perms
        event.getUser().send().notice(modpermissions); //Mod Permissions
        event.getUser().send().notice(mod); //Mod List
        event.getUser().send().notice(Admins); //Admin List
        return true;
    }

    @Override
    public void setConfig(Config config) {

    }

    @Override
    public void setManager(PermissionManager manager) {

    }
}
