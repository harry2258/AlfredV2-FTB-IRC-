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
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.PreparedStatement;
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
    public boolean execute(MessageEvent event) {
        String[] args = event.getMessage().split(" ");
        if (args.length == 2) {

            User target;
            String user;

            try {
                target = event.getBot().getUserChannelDao().getUser(args[1]);

                if (!event.getChannel().getUsers().contains(target)) {
                    event.getChannel().send().message("User \"" + args[1] + "\" is not in the channel!");
                    return false;
                }

                if (target.getNick().equals(event.getUser().getNick())) {
                    event.getChannel().send().message(event.getUser().getNick() + " hurt itself in confusion!");
                    return true;
                }

                user = Utils.getAccount(target, event);

                if (!PermissionManager.hasExec(target.getNick())) {
                    if (!PermissionManager.hasAdmin(target.getNick(), event)) {

                        if (!target.isIrcop()) {

                            if (!ignored.contains(user)) {

                                if (user != null) {
                                    if (user.equalsIgnoreCase(Main.Login.get(event.getUser().getNick()))) {
                                        event.getChannel().send().message(event.getUser().getNick() + " hurt itself in confusion!");
                                        return true;
                                    }
                                }
                                ignored.add(user);
                                event.respond(user + " was added to the ignore list.");

                                if (config.useDatabase) {
                                    PreparedStatement stmt = Main.database.prepareStatement("INSERT IGNORE INTO `Ignored_Users` (`User`) VALUES (?)");
                                    stmt.setString(1, user);
                                    stmt.execute();
                                }
                                return true;
                            } else {
                                event.respond(user + " is already in the ignore list");
                                return true;
                            }
                        } else {
                            event.respond("You cannot add that person to the list!");
                            return true;
                        }

                    } else if (PermissionManager.hasExec(event.getUser().getNick())) {

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

            } catch (Exception ex) {
                System.out.println(System.currentTimeMillis());
                event.getChannel().send().message(ex.toString());
                ex.printStackTrace();
                return false;
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
