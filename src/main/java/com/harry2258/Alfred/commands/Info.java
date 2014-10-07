package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.*;
import com.harry2258.Alfred.json.Permission;
import com.harry2258.Alfred.json.Perms;
import org.pircbotx.Colors;
import org.pircbotx.User;
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
                    event.getUser().send().notice("Channels to join when restarting: " + channels.toString());
                    return true;
                }

                if (args.length == 2 && args[1].equalsIgnoreCase("ignored")) {
                    int i = 0;
                    PreparedStatement stmt3 = Main.database.prepareStatement("SELECT * FROM `Ignored_Users`");
                    ResultSet rs3 = stmt3.executeQuery();
                    if (rs3.next()) {
                        while (rs3.next()) {
                            i = i + 1;
                        }
                    }
                    event.getUser().send().notice(String.valueOf(i) + " users are ignored by the bot. ");
                    if (i > 0)
                        event.getUser().send().notice("Type \"" + config.getTrigger() + "info ignored list \" for a full list!");
                }

                if (args.length == 3 && args[2].equalsIgnoreCase("list")) {
                    ArrayList<String> IgnoredUsers = new ArrayList<>();
                    PreparedStatement stmt3 = Main.database.prepareStatement("SELECT * FROM `Ignored_Users`");
                    ResultSet rs3 = stmt3.executeQuery();
                    if (rs3.next()) {
                        while (rs3.next()) {
                            rs3.getString("User");
                            try {
                                IgnoredUsers.add(rs3.getString("User"));
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                    }
                    event.getUser().send().notice("Ignored Users: " + JsonUtils.prettyPrint(IgnoredUsers));
                    return true;
                }
                if (args.length == 4 && args[2].equalsIgnoreCase("user")) {
                    String info;
                    String account = "";
                    try {
                        User ignored = event.getBot().getUserChannelDao().getUser(args[3]);
                        if (event.getChannel().getUsers().contains(ignored)) {
                            account = Utils.getAccount(ignored, event);
                        }
                        PreparedStatement stmt = Main.database.prepareStatement("SELECT * FROM `Ignored_Users` WHERE User_Nick = ? OR User = ?");
                        stmt.setString(1, args[3]);
                        stmt.setString(2, account);
                        ResultSet rs = stmt.executeQuery();
                        while (rs.next()) {
                            info = "User " + args[3] + " was ignored by " + rs.getString("Ignored_By") + " on " + rs.getDate("Date") + " at " + rs.getTime("Date") + " UTC";
                            event.getChannel().send().message(info);
                        }
                        return true;
                    } catch (Exception user) {
                        user.printStackTrace();
                        return false;
                    }
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
