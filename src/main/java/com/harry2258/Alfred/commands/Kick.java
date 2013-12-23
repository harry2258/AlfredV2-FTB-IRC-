package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

public class Kick extends Command {

    private Config config;
    private PermissionManager manager;

    public Kick() {
        super("Kick", "Remove a user from a channel", "kick #channel user (reason) or kick user (reason)");
    }

    @Override
    public boolean execute(MessageEvent event) {
        String[] args = event.getMessage().split(" ");
        User sender = event.getUser();
        if (args.length == 3) {
            Channel chan = event.getBot().getUserChannelDao().getChannel(args[1]);
            User target = event.getBot().getUserChannelDao().getUser(args[2]);
            chan.send().kick(target);
            return true;
        }
        if (args.length == 2) {
            Channel chan = event.getChannel();
            User target = event.getBot().getUserChannelDao().getUser(args[1]);
            chan.send().kick(target);
            return true;
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
                chan.send().kick(target, reason);
                return true;
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }
                Channel chan = event.getChannel();
                User target = event.getBot().getUserChannelDao().getUser(args[1]);
                chan.send().kick(target, sb.toString().trim());
                return true;
            }
        }
        return false;
    }

    public boolean isChannel(String name) {
        return name.startsWith("#");
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
