/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;


import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;

public class Ignore extends Command {

    public static List<String> ignored = new ArrayList<>();

    private Config config;
    private PermissionManager manager;

    public Ignore() {
        super("Ignore", "Any commands from that user will be ignore!", "Ignore [user]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        if (args.length == 2) {
            User target = event.getBot().getUserChannelDao().getUser(args[1]);

            if (target.getNick().equals(event.getUser().getNick())) {
                event.getChannel().send().message(event.getUser().getNick() + " hurt itself in confusion!");
                return true;
            }

            String user = Utils.getAccount(event.getBot().getUserChannelDao().getUser(args[1]), event);
            if (!manager.hasExec(target, event)) {

                if (!manager.hasAdmin(target, event)) {

                    if (!target.isIrcop()) {

                        if (!ignored.contains(user)) {
                            ignored.add(user);
                            event.respond(user + " was added to the ignore list.");
                            return true;
                        } else {
                            event.respond(user + " is already in the ignore list");
                            return true;
                        }
                    } else {
                        event.respond("You cannot add that person to the list!");
                        return true;
                    }

                } else {
                    event.respond("You cannot add that person to the list!");
                    return true;
                }

            } else {
                event.respond("You cannot add that person to the list!");
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
