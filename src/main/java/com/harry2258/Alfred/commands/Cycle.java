package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 2/5/14.
 */
public class Cycle extends Command {
    public Cycle() {
        super("Cycle", "Part and rejoin the channel");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        if (args.length == 1) {
            event.getChannel().send().part();
            event.getBot().sendIRC().joinChannel(event.getChannel().getName());
            return true;
        }
        if (PermissionManager.hasExec(event.getUser(), event)) {
            if (args.length == 2) {
                if (args[1].equalsIgnoreCase("all")) {
                    for (Channel chan : event.getBot().getUserBot().getChannels()) {
                        chan.send().part();
                        event.getBot().sendIRC().joinChannel(chan.getName());
                    }
                    return true;
                }
                Channel chan = event.getBot().getUserChannelDao().getChannel(args[1]);
                if (event.getBot().getUserBot().getChannels().contains(chan)) {
                    chan.send().part();
                    chan.getBot().sendIRC().joinChannel(chan.getName());
                } else {
                    event.getUser().send().notice("I am not in that channel!");
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public void setConfig(Config config) {

    }

    @Override
    public void setManager(PermissionManager manager) {

    }
}
