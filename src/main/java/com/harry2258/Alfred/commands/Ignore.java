/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;


import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import java.util.ArrayList;
import java.util.List;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

public class Ignore extends Command {
    
    public static List<String> ignored = new ArrayList<String>();
    
    private Config config;
    private PermissionManager manager;

    public Ignore() {
        super("Ignore", "Any commands from that user will be ignore!");
    }
    
    @Override
     public boolean execute(MessageEvent event) {
        String[] args = event.getMessage().split(" ");
        if (config.isAdmin(event.getUser().getNick(), event.getUser().getHostmask())) {
            if (args.length == 2) {
                String test = args[1];
                User user = event.getBot().getUserChannelDao().getUser(args[1]);
                if (!ignored.contains(user.getHostmask())) {
                    ignored.add(user.getHostmask());
                    event.respond(user.getNick() + " was added to the ignore list.");
                    return true;
                } else {
                    event.respond(user.getNick() + " is already in the ignore list");
                    return true;
                }
            } else {
            }
        } else {
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
