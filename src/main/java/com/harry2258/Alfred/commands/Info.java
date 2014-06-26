package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.JsonUtils;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.json.Permission;
import com.harry2258.Alfred.json.Perms;
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
        String filename = "";
        String URL = "None";

        if (Main.URL.containsKey(event.getChannel().getName())) {
            URL = Main.URL.get(event.getChannel().getName());
        }

        if (args.length == 2 && args[1].contains("command")) {
            //java.io.File folder = new File("commands/#ftb/"); java.io.File[] ListOfFiles = folder.listFiles(); for (java.io.File ListOfFile : ListOfFiles) {if (ListOfFile.isFile()) {filename += ListOfFile.getName() + " | \t";}}
            if (new File("commands/" + event.getChannel().getName() + "/").exists()) {
                File folder = new File("commands/" + event.getChannel().getName() + "/");
                File[] listOfFiles = folder.listFiles();
                for (File listOfFile : listOfFiles) {
                    if (listOfFile.isFile()) {
                        filename += listOfFile.getName() + " | \t";
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

        Perms perms = Main.map.get(event.getChannel().getName());
        Permission p = perms.getPermission();

        String exe = JsonUtils.getStringFromFile(System.getProperty("user.dir") + "/exec.json");
        Permission exec = JsonUtils.getPermsFromString(exe).getPermission();

        if (args.length == 2 && args[1].equalsIgnoreCase("full") | args[1].equalsIgnoreCase("all")) {
            int NumberofCommand = 0;
            if (new File("commands/" + event.getChannel().getName() + "/").exists()) {
                NumberofCommand = new File("commands/" + event.getChannel().getName() + "/").listFiles().length;
            }

            event.getUser().send().notice("Permission everyone has: " + JsonUtils.prettyPrint(p.getEveryone()).replaceAll("command.", "")); //Everyone Perms
            event.getUser().send().notice("Mod Permissions: " + JsonUtils.prettyPrint(p.getModPerms()).replaceAll("command.", "")); //Mod Permissions
            event.getUser().send().notice("Moderators: " + JsonUtils.prettyPrint(p.getMods())); //Mod List
            event.getUser().send().notice("Admins: " + JsonUtils.prettyPrint(p.getAdmins())); //Admin List
            event.getUser().send().notice("Global Exec: " + JsonUtils.prettyPrint(exec.getExec()));  //Global Exec!!
            event.getUser().send().notice("Number of custom command: " + NumberofCommand + ". For a full list, type " + config.getTrigger() + "info commands");
            event.getUser().send().notice("URL scanning: " + URL);
            return true;
        }


        String Usergroup = Login.Group(Main.Login.get(event.getUser().getNick()), event.getChannel().getName());

        if (Usergroup.equalsIgnoreCase("None :<")) {
            event.getUser().send().notice("You are in the default group and have access to: " + JsonUtils.prettyPrint(p.getEveryone()));
        }
        if (Usergroup.equalsIgnoreCase("Moderator")) {
            event.getUser().send().notice("You are in the Moderator group and have access to: " + JsonUtils.prettyPrint(p.getModPerms()) + JsonUtils.prettyPrint(p.getEveryone()));
        }
        if (Usergroup.equalsIgnoreCase("Admin")) {
            event.getUser().send().notice("You are a Admin! You have access to all commands " + Colors.BOLD + "except" + Colors.NORMAL + " a few critical commands such as Kill, Exec, Etc.");
        }
        if (Usergroup.equalsIgnoreCase("Exec")) {
            event.getUser().send().notice("You own this town!");
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
