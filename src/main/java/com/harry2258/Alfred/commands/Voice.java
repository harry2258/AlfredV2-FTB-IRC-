package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

public class Voice extends Command {

    private Config config;
    private PermissionManager manager;

    public Voice() {
        super("Voice", "Give a user voice in a channel", "Voice <username>");
    }

    @Override
    public boolean execute(MessageEvent event) {
        String[] args = event.getMessage().split(" ");
        if (args.length == 2) {
            event.getChannel().send().voice(event.getBot().getUserChannelDao().getUser(args[1]));
            return true;
        }
        if (args.length == 3) {
            event.getBot().getUserChannelDao().getChannel(args[1]).send().voice(event.getBot().getUserChannelDao().getUser(args[2]));
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
