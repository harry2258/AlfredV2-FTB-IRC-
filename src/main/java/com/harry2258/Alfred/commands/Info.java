package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.JsonUtils;
import com.harry2258.Alfred.api.PermissionManager;
import org.json.JSONObject;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.File;

/**
 * Created by Hardik on 1/8/14.
 */

public class Info extends Command {
    private Config config;
    private PermissionManager manager;

    public Info() {
        super("List", "List Permissions that people have", "List");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        String temp;
        String mod;
        String modpermissions;
        String everyone;
        String Admins;
        String Exec;
        String filename = "";
        String URL = "None";

        if (Main.URL.containsKey(event.getChannel().getName())) {
            URL = Main.URL.get(event.getChannel().getName());
        }

        if (args.length == 2 && args[1].equalsIgnoreCase("commands")) {
            if (new File("commands/" + event.getChannel().getName() + "/").exists()) {
                File folder = new File("commands/" + event.getChannel().getName() + "/");
                File[] listOfFiles = folder.listFiles();

                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        filename += listOfFiles[i].getName() + " | \t";
                    }
                }
                if (!filename.isEmpty()) {
                    event.getUser().send().notice(filename.replaceAll(".cmd", ""));
                } else {
                    event.getUser().send().notice("There are no custom command for this channel yet!");
                }
                return true;
            }
        }

        //String Jsonfile = System.getProperty("user.dir") + "/perms/" + event.getChannel().getName().toLowerCase() + "/" + "perms.json";
        String perms = Main.map.get(event.getChannel().getName());
        JSONObject jsonObj = new JSONObject(perms);

        String exece = System.getProperty("user.dir") + "/exec.json";
        String exe = JsonUtils.getStringFromFile(exece);
        JSONObject exec = new JSONObject(exe);

        temp = jsonObj.getJSONObject("Perms").getString("Everyone");
        everyone = "Permission everyone has: " + temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ").replaceAll("command.","");

        temp = jsonObj.getJSONObject("Perms").getString("ModPerms");
        modpermissions = "Mod Permissions: " + temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ").replaceAll("command.","");

        temp = jsonObj.getJSONObject("Perms").getString("Mods");
        mod = "Mods: " + temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ");

        temp = jsonObj.getJSONObject("Perms").getString("Admins");
        Admins = "Admins: " + temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ");

        temp = exec.getJSONObject("Perms").getString("Exec");
        Exec = "Global Exec: " + temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ");

        int NumberofCommand = 0;
        if (new File("commands/" + event.getChannel().getName() + "/").exists()) {
            NumberofCommand = new File("commands/" + event.getChannel().getName() + "/").listFiles().length;
        }
        event.getUser().send().notice(everyone); //Everyone Perms
        event.getUser().send().notice(modpermissions); //Mod Permissions
        event.getUser().send().notice(mod); //Mod List
        event.getUser().send().notice(Admins); //Admin List
        event.getUser().send().notice(Exec);  //Global Exec!!
        event.getUser().send().notice("Number of custom command: " + NumberofCommand + ". For a full list, type " + config.getTrigger() + "info commands");
        event.getUser().send().notice("URL scanning: " + URL);
        return true;
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
