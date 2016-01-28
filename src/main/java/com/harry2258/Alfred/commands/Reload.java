package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.*;
import com.harry2258.Alfred.json.Perms;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Hardik on 1/15/14.
 */
public class Reload extends Command {
    private Config config;
    private PermissionManager manager;

    public Reload() {
        super("Reload", "Reload all channel permissions!", "Reload [Channel]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        if (!config.useDatabase) {
            Main.map.remove(event.getChannel().getName().toLowerCase());
            File file = new File(System.getProperty("user.dir") + "/perms/" + event.getChannel().getName().toLowerCase() + "/" + "perms.json");
            if (!file.exists()) {
                System.out.println("Creating perms.json for " + event.getChannel());
                JsonUtils.createJsonStructure(file);
            }
            String perms = JsonUtils.getStringFromFile(file.toString());
            Perms p = JsonUtils.getPermsFromString(perms);
            Main.map.put(event.getChannel().getName(), p);

            MessageUtils.sendUserNotice(event, "Permissions were reloaded for " + event.getChannel().getName().toLowerCase() + "!");
            return true;
        } else {
            PreparedStatement stmt3 = Main.database.prepareStatement("SELECT a.Channel, a.Permission, a.URL FROM `Channel_Permissions` a, `Rejoin_Channels` b WHERE a.Channel = b.Channel;");
            ResultSet rs3 = stmt3.executeQuery();
            while (rs3.next()) {
                String channel = rs3.getString("Channel");
                Main.URL.put(channel, rs3.getString("URL"));
                Perms p = JsonUtils.getPermsFromString(rs3.getString("Permission"));
                Main.map.remove(channel.toLowerCase());
                Main.map.put(channel.toLowerCase(), p);
                System.out.println("Loaded setting for channel: " + channel);
            }

            stmt3 = Main.database.prepareStatement("SELECT * FROM `Ignored_Users`");
            rs3 = stmt3.executeQuery();

            while (rs3.next()) {
                Ignore.ignored.add(rs3.getString("User"));
            }

            MessageUtils.sendChannel(event, "Reloaded settings for currently connected channel.");
            return true;
        }
     return false; //Line 37 and Line 58 will return true if it reloaded. Else return false.
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
