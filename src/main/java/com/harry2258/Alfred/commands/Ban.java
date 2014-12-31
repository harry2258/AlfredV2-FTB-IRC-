/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * @author Zack
 */
public class Ban extends Command {

    private Config config;
    private PermissionManager manager;
    public Ban() {
        super("Ban", "Bans a user from a channel", "Ban <hostmask> (optional: reason) | Ban <#channel> <hostmask> (optional: reason)");
    }

    @Override
    public boolean execute(MessageEvent event) {
        String[] args = event.getMessage().split(" ");
        User sender = event.getUser();
        if (args.length == 3) {
            Channel chan = event.getBot().getUserChannelDao().getChannel(args[1]);
            User target = event.getBot().getUserChannelDao().getUser(args[2]);
            int senderrank = Utils.getRank(chan, sender);
            int targetrank = Utils.getRank(chan, target);
            if (senderrank > targetrank) {
                chan.send().ban("*!*@" + target.getHostmask());
                chan.send().kick(target);
                return true;
            } else {
                sender.send().message("You cant kick someone with a higher rank than you!");
                return false;
            }
        }
        if (args.length == 2) {
            Channel chan = event.getChannel();
            int senderrank = Utils.getRank(event.getChannel(), event.getUser());
            User target = event.getBot().getUserChannelDao().getUser(args[1]);
            int targetrank = Utils.getRank(event.getChannel(), target);
            if (senderrank > targetrank) {
                chan.send().ban("*!*@" + target.getHostmask());
                chan.send().kick(target, "Ban requested by " + sender.getNick());
                return true;
            } else {
                sender.send().message("You cant kick someone with a higher rank than you!");
                return false;
            }
        }
        if (args.length > 3) {
            if (args[1].startsWith("#")) {
                StringBuilder sb = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }
                String reason = sb.toString().trim();
                Channel chan = event.getBot().getUserChannelDao().getChannel(args[1]);
                User target = event.getBot().getUserChannelDao().getUser(args[2]);
                chan.send().ban("*!*@" + target.getHostmask());
                chan.send().kick(target, reason);
                return true;
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }
                Channel chan = event.getChannel();
                User target = event.getBot().getUserChannelDao().getUser(args[1]);
                chan.send().ban("*!*@" + target.getHostmask());
                chan.send().kick(target, sb.toString().trim());
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
