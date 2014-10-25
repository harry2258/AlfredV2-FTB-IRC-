/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;


import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.PreparedStatement;

public class Unignore extends Command {

    private Config config;
    private PermissionManager manager;

    public Unignore() {
        super("Unignore", "Remove the user from the Ignore list", "Unignore [user]");
    }

    @Override
    public boolean execute(MessageEvent event) {
        String[] args = event.getMessage().split(" ");
        if (args.length == 2) {
            String user = null;
            try {
                user = Utils.getAccount(event.getBot().getUserChannelDao().getUser(args[1]), event);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            if (Ignore.ignored.contains(user)) {
                Ignore.ignored.remove(user);
                if (config.useDatabase) {
                    try {
                        PreparedStatement stmt = Main.database.prepareStatement("DELETE FROM `Ignored_Users` WHERE User = ?");
                        stmt.setString(1, user);
                        stmt.execute();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return false;
                    }
                }
                event.respond(user + " was removed from ignore list.");
                return true;
            } else {
                event.respond(user + " isn't on the ignore list");
                return true;
            }
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
