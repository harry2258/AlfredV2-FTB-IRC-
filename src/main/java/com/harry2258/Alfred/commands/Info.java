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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by Hardik on 1/8/14.
 */

public class Info extends Command {

    private Config config;
    private PermissionManager manager;

    public Info() {
        super("Info", "List Permissions that people have", "Info");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        String filename = "";
        String URL = "None";

        if (!config.useDatabase) {
            if (Main.URL.containsKey(event.getChannel().getName())) {
                URL = Main.URL.get(event.getChannel().getName());
            }

            if (args.length == 2 && args[1].contains("command")) {
                //java.io.File folder = new File("commands/#ftb/"); java.io.File[] ListOfFiles = folder.listFiles(); for (java.io.File ListOfFile : ListOfFiles) {if (ListOfFile.isFile()) {filename += ListOfFile.getName() + " | \t";}}
                if (new File("commands/" + event.getChannel().getName() + "/").exists()) {
                    File folder = new File("commands/" + event.getChannel().getName() + "/");
                    File[] listOfFiles = folder.listFiles();
                    for (File listOfFile : listOfFiles) {
                        if (listOfFile.isFile())
                            filename += listOfFile.getName() + " | \t";
                    }
                    if (!filename.isEmpty())
                        event.getUser().send().notice(filename.replaceAll(".cmd", ""));
                    else
                        event.getUser().send().notice("There are no custom command for this channel yet!");
                }
                return true;
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
                event.getUser().send().notice("Executive Users: " + JsonUtils.prettyPrint(exec.getExec()));  //Global Exec!!
                event.getUser().send().notice("Number of custom command: " + NumberofCommand + ". For a full list, type " + config.getTrigger() + "info commands");
                event.getUser().send().notice("URL scanning: " + URL);
                return true;
            }


            String Usergroup = Login.Group(Main.Login.get(event.getUser().getNick()), event.getChannel().getName());

            if (Usergroup.equalsIgnoreCase("None :<")) {
                event.getUser().send().notice("You are in the default group and have access to: " + JsonUtils.prettyPrint(p.getEveryone()).replaceAll("command.", ""));
            }
            if (Usergroup.equalsIgnoreCase("Moderator")) {
                event.getUser().send().notice("You are in the Moderator group and have access to: " + JsonUtils.prettyPrint(p.getModPerms()).replaceAll("command.", "") + JsonUtils.prettyPrint(p.getEveryone()).replaceAll("command.", ""));
            }
            if (Usergroup.equalsIgnoreCase("Admin")) {
                event.getUser().send().notice("You are a Admin! You have access to all commands " + Colors.BOLD + "except" + Colors.NORMAL + " a few critical commands such as Kill, Exec, Etc.");
            }
            if (Usergroup.equalsIgnoreCase("Exec")) {
                event.getUser().send().notice("You own this town!");
            }
            return true;
        } else {
            try {
                if (args.length == 2 && args[1].equalsIgnoreCase("rejoin")) {
                    PreparedStatement stmt = Main.database.prepareStatement("SELECT Channel  FROM `Rejoin_Channels`");
                    ResultSet rs = stmt.executeQuery();
                    ArrayList<String> channels = new ArrayList<>();
                    while (rs.next()) {
                        channels.add(rs.getString("Channel"));
                    }
                    event.getUser().send().message("Channels to join when restarting: " + channels.toString());
                    return true;
                }

                if (args.length == 2 && args[1].equalsIgnoreCase("full") | args[1].equalsIgnoreCase("all")) {
                    String exe = JsonUtils.getStringFromFile(System.getProperty("user.dir") + "/exec.json");
                    Permission exec = JsonUtils.getPermsFromString(exe).getPermission();

                    int NumberofCommand = 0;
                    if (new File("commands/" + event.getChannel().getName() + "/").exists()) {
                        NumberofCommand = new File("commands/" + event.getChannel().getName() + "/").listFiles().length;
                    }

                    try {
                        PreparedStatement stmt = Main.database.prepareStatement("SELECT `URL` FROM `Channel_Permissions` WHERE `Channel` = '" + event.getChannel().getName() + "'");
                        ResultSet rs = stmt.executeQuery();
                        rs.next();
                        URL = rs.getString("URL");
                    } catch (Exception url) {
                        url.printStackTrace();
                    }

                    Perms perms = Main.map.get(event.getChannel().getName());
                    Permission p = perms.getPermission();

                    event.getUser().send().notice("Permission everyone has: " + JsonUtils.prettyPrint(p.getEveryone()).replaceAll("command.", "")); //Everyone Perms
                    event.getUser().send().notice("Mod Permissions: " + JsonUtils.prettyPrint(p.getModPerms()).replaceAll("command.", "")); //Mod Permissions
                    event.getUser().send().notice("Moderators: " + JsonUtils.prettyPrint(p.getMods())); //Mod List
                    event.getUser().send().notice("Admins: " + JsonUtils.prettyPrint(p.getAdmins())); //Admin List
                    event.getUser().send().notice("Executive Users: " + JsonUtils.prettyPrint(exec.getExec()));  //Global Exec!!
                    event.getUser().send().notice("Number of custom command: " + NumberofCommand + ". For a full list, type " + config.getTrigger() + "info commands");
                    event.getUser().send().notice("URL scanning: " + URL);
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
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
