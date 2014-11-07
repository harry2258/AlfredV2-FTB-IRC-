package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Database.Create;
import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.*;
import com.harry2258.Alfred.json.Perms;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Join extends Command {

    private Config config;
    private PermissionManager manager;

    public Join() {
        super("Join", "Tells the bot to join a channel", "join [#channel]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        if (PermissionManager.hasExec(event.getUser().getNick())) {
            String[] args = event.getMessage().split(" ");
            Channel target = event.getBot().getUserChannelDao().getChannel(args[1]);

            if (target.getName().equalsIgnoreCase("#dragonweyr") || target.getName().equalsIgnoreCase("#help") || target.getName().equalsIgnoreCase("#lobby") || target.getName().equalsIgnoreCase("#coders") || target.getName().equalsIgnoreCase("#esper") || target.getName().equalsIgnoreCase("#helper")) {
                MessageUtils.sendChannel(event, "YOU CRAZY SENDIN' ME OUT THERE?! AWW HELL NAW!!");
                return true;
            }

            if (target.isInviteOnly()) {
                event.getBot().sendRaw().rawLineNow("KNOCK " + target.getName() + " :Asked to join this channel by user " + event.getUser().getNick() + " in channel " + event.getChannel().getName());
            }
            event.getBot().sendIRC().joinChannel(target.getName());
            if (config.useDatabase) {
                try {
                    Create.AddChannel(target.getName().toLowerCase(), Main.database);
                    PreparedStatement stmt = Main.database.prepareStatement("SELECT Permission FROM `Channel_Permissions` WHERE Channel = ?");
                    stmt.setString(1, target.getName());
                    ResultSet rs = stmt.executeQuery();
                    rs.next();
                    Perms p = JsonUtils.getPermsFromString(rs.getString("Permission"));
                    Main.map.put(target.getName().toLowerCase(), p);
                    System.out.println("Loaded perms for " + target.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                String channel = target.getName();
                File file = new File(System.getProperty("user.dir") + "/perms/" + channel.toLowerCase() + "/" + "perms.json");
                String Jsonfile = System.getProperty("user.dir") + "/perms/" + channel.toLowerCase() + "/" + "perms.json";
                if (!file.exists()) {
                    System.out.println("Creating perms.json for " + channel);
                    JsonUtils.createJsonStructure(file);
                }
                String perms = JsonUtils.getStringFromFile(Jsonfile);
                Perms p = JsonUtils.getPermsFromString(perms);
                Main.map.put(channel.toLowerCase(), p);
                System.out.println("Loaded perms for " + channel);
            }
            return true;
        }
        return false;
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
