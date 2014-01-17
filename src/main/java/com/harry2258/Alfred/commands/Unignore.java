/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;


import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;
import org.pircbotx.hooks.events.MessageEvent;

public class Unignore extends Command {

    private Config config;
    private PermissionManager manager;

    public Unignore() {
        super("Ignore", "Any commands from that user will be ignore!");
    }

    @Override
    public boolean execute(MessageEvent event) {
        String[] args = event.getMessage().split(" ");
        if (args.length == 2) {
            String user = Utils.getAccount(event.getBot().getUserChannelDao().getUser(args[1]), event);
            if (Ignore.ignored.contains(user)) {
                Ignore.ignored.remove(user);
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
