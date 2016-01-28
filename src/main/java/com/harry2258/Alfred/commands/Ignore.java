/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;


import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.*;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        if (args.length == 3 && PermissionManager.hasExec(event.getUser().getNick()) && args[1].equals("Nuke") && (args[2].equalsIgnoreCase("DB") || args[2].equalsIgnoreCase("database"))) {
            try {
                Main.database.prepareStatement("DROP TABLE `Ignored_Users`").execute();
                Main.database.prepareStatement("CREATE TABLE IF NOT EXISTS Ignored_Users (`User` VARCHAR(255) NOT NULL PRIMARY KEY, `Ignored_By` VARCHAR(255), `User_Nick` VARCHAR(255), `Date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)").execute();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if (args.length == 2) {

            User target;
            String user;

            try {
                target = event.getBot().getUserChannelDao().getUser(args[1]);

                if (!event.getChannel().getUsers().contains(target)) {
                    MessageUtils.sendChannel(event, "User \"" + args[1] + "\" is not in the channel!");
                    return false;
                }

                if (target.getNick().equals(event.getUser().getNick())) {
                    MessageUtils.sendChannel(event, event.getUser().getNick() + " hurt itself in confusion!");
                    return true;
                }

                user = Utils.getAccount(target, event);

                if (!PermissionManager.hasExec(target.getNick())) {
                    if (!PermissionManager.hasAdmin(target.getNick(), event.getChannel())) {

                        if (!target.isIrcop()) {

                            if (!ignored.contains(user)) {

                                if (user != null) {
                                    if (user.equalsIgnoreCase(Main.Login.get(event.getUser().getNick()))) {
                                        MessageUtils.sendChannel(event, event.getUser().getNick() + " hurt itself in confusion!");
                                        return true;
                                    }
                                }

                                ignored.add(user);
                                event.respond(user + " was added to the ignore list.");

                                if (config.useDatabase) {
                                    PreparedStatement stmt = Main.database.prepareStatement("INSERT IGNORE INTO `Ignored_Users` (`User`, `Ignored_By`, `User_Nick`, `Date`) VALUES (?, ?, ?, UTC_TIMESTAMP())");
                                    stmt.setString(1, user);
                                    stmt.setString(2, event.getUser().getNick());
                                    stmt.setString(3, args[1]);
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
                            if (config.useDatabase) {
                                PreparedStatement stmt = Main.database.prepareStatement("INSERT IGNORE INTO `Ignored_Users` (`User`, `Ignored_By`, `User_Nick`, `Date`) VALUES (?, ?, ?, UTC_TIMESTAMP())");
                                stmt.setString(1, user);
                                stmt.setString(2, event.getUser().getNick());
                                stmt.setString(3, args[1]);
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

                } else {
                    event.respond("You cannot add that person to the list!");
                    return true;
                }

            } catch (SQLException ex) {
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
