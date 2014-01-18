package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
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
        String Exec;

        //String Jsonfile = System.getProperty("user.dir") + "/perms/" + event.getChannel().getName().toLowerCase() + "/" + "perms.json";
        String perms = Main.map.get(event.getChannel().getName());
        JSONObject jsonObj = new JSONObject(perms);

        String exece = System.getProperty("user.dir") + "/perms.json";
        String exe = JsonUtils.getStringFromFile(exece);
        JSONObject exec = new JSONObject(exe);

        temp = jsonObj.getJSONObject("Perms").getString("Everyone");
        everyone = "Permission everyone has: " + temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ");

        temp = jsonObj.getJSONObject("Perms").getString("ModPerms");
        modpermissions = "Mod Permissions: " + temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ");

        temp = jsonObj.getJSONObject("Perms").getString("Mods");
        mod = "Mods: " + temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ");

        temp = jsonObj.getJSONObject("Perms").getString("Admins");
        Admins = "Admins: " + temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ");

        temp = exec.getJSONObject("Perms").getString("Exec");
        Exec = "God of Alfred: " + temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ");

        event.getUser().send().notice(everyone); //Everyone Perms
        event.getUser().send().notice(modpermissions); //Mod Permissions
        event.getUser().send().notice(mod); //Mod List
        event.getUser().send().notice(Admins); //Admin List
        event.getUser().send().notice(Exec);  //God of the bot!
        return true;
    }

    @Override
    public void setConfig(Config config) {

    }

    @Override
    public void setManager(PermissionManager manager) {

    }
}
