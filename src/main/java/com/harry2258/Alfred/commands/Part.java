package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;

public class Part extends Command {
    public Part() {
        super("Part", "Removes the bot from a channel", "part #channel");
    }

    private Config config;
    private PermissionManager manager;

    @Override
    public boolean execute(MessageEvent event) {
        String[] args = event.getMessage().split(" ");
        if (manager.hasExec(event.getUser(), event)) {
            if (args.length == 2) {
                Channel target = event.getBot().getUserChannelDao().getChannel(args[1]);
                event.getBot().getUserChannelDao().getAllChannels();
                if (event.getBot().getUserChannelDao().getAllChannels().contains(target)) {
                    target.send().part();
                    return true;
                } else {
                    event.getUser().send().notice("I'm not in the channel " + args[1] + "!");
                    return true;
                }
            } else {
                event.getChannel().send().part();
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
