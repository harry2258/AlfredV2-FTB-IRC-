package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.JsonUtils;
import com.harry2258.Alfred.api.PermissionManager;
import org.json.JSONObject;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.File;

/**
 * Created by Hardik on 1/8/14.
 */

public class Info extends Command {

    private Config config;
    private PermissionManager manager;

    public Info() {
        super("Info", "List Permissions that people have", "List");
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

        if (args.length == 2 && args[1].contains("command")) {
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

        String perms = Main.map.get(event.getChannel().getName());
        JSONObject jsonObj = new JSONObject(perms);

        String exe = JsonUtils.getStringFromFile(System.getProperty("user.dir") + "/exec.json");
        JSONObject exec = new JSONObject(exe);

        if (args.length == 2 && args[1].equalsIgnoreCase("full") | args[1].equalsIgnoreCase("all")) {

            temp = jsonObj.getJSONObject("Perms").getString("Everyone");
            everyone = temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ").replaceAll("command.", "");

            temp = jsonObj.getJSONObject("Perms").getString("ModPerms");
            modpermissions = temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ").replaceAll("command.", "");

            temp = jsonObj.getJSONObject("Perms").getString("Mods");
            mod = temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ");

            temp = jsonObj.getJSONObject("Perms").getString("Admins");
            Admins = temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ");

            temp = exec.getJSONObject("Perms").getString("Exec");
            Exec = temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ");

            int NumberofCommand = 0;
            if (new File("commands/" + event.getChannel().getName() + "/").exists()) {
                NumberofCommand = new File("commands/" + event.getChannel().getName() + "/").listFiles().length;
            }

            event.getUser().send().notice("Permission everyone has: " + everyone); //Everyone Perms
            event.getUser().send().notice("Mod Permissions: " + modpermissions); //Mod Permissions
            event.getUser().send().notice("Moderators: " + mod); //Mod List
            event.getUser().send().notice("Admins: " + Admins); //Admin List
            event.getUser().send().notice("Global Exec: " + Exec);  //Global Exec!!
            event.getUser().send().notice("Number of custom command: " + NumberofCommand + ". For a full list, type " + config.getTrigger() + "info commands");
            event.getUser().send().notice("URL scanning: " + URL);
            return true;
        }


        String Usergroup = Login.Group(Main.Login.get(event.getUser().getNick()), event.getChannel().getName().toLowerCase());

        if (Usergroup.equalsIgnoreCase("None :<")) {
            temp = jsonObj.getJSONObject("Perms").getString("Everyone");
            everyone = temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ").replaceAll("command.", "");
            event.getUser().send().notice("You are in the default group and have access to: " + everyone);

        }
        if (Usergroup.equalsIgnoreCase("Moderator")) {
            temp = jsonObj.getJSONObject("Perms").getString("ModPerms");
            modpermissions = temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ").replaceAll("command.", "");
            temp = jsonObj.getJSONObject("Perms").getString("Everyone");
            everyone = temp.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ").replaceAll("command.", "");
            event.getUser().send().notice("You are in the Moderator group and have access to: " + modpermissions + everyone);

        }
        if (Usergroup.equalsIgnoreCase("Admin")) {
            event.getUser().send().notice("You are a Admin! You have access to all commands " + Colors.BOLD + "except" + Colors.NORMAL + " a few critical commands such as Kill, Exec, Etc.");

        }
        if (Usergroup.equalsIgnoreCase("Exec")) {
            event.getUser().send().notice("Yea, you're pretty much a badass.");
        }
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
