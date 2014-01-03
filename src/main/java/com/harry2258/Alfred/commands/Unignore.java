/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;


import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.User;
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
        if (config.isAdmin(event.getUser().getNick(), event.getUser().getHostmask())) {
            if (args.length == 2) {
                String test = args[1];
                User user = event.getBot().getUserChannelDao().getUser(args[1]);
                if (Ignore.ignored.contains(user.getHostmask())) {
                    Ignore.ignored.remove(user.getHostmask());
                    event.respond(user.getNick() + " was removed to the ignore list.");
                    return true;
                } else {
                    event.respond(user.getNick() + " isn't on the ignore list");
                    return true;
                }
            } else {}
        } else {}
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
