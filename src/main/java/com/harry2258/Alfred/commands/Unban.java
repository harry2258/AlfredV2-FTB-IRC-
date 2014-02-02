package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

public class Unban extends Command {

    private Config config;
    private PermissionManager manager;

    public Unban() {
        super("Unban", "Removes a ban on a hostmask", "Unban hostmask or Unban #channel hostmask");
    }

    //huehuehue le copypasta faec
    @Override
    public boolean execute(MessageEvent event) {
        String[] args = event.getMessage().split(" ");
        User sender = event.getUser();
        if (args.length == 3) {
            Channel target = event.getBot().getUserChannelDao().getChannel(args[1]);
            //target.send().unBan();
            target.send().unBan(args[2]);
            return true;
        }
        if (args.length == 2) {
            event.getChannel().send().unBan(args[1]);
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
