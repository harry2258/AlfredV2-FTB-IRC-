package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/15/14.
 */
public class Devoice extends Command {

    private Config config;
    private PermissionManager manager;

    public Devoice() {
        super("Devoice", "Removes Voice from user", "Devoice <username>");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        if (PermissionManager.hasExec(event.getUser(), event)) {
            if (args.length == 2) {
                event.getChannel().send().deVoice(event.getBot().getUserChannelDao().getUser(args[1]));
                event.getChannel().send().message("You didn't see that coming, did you now " + args[1]);
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
