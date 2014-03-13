package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/14/14.
 */
public class Deop extends Command {
    private Config config;
    private PermissionManager manager;

    public Deop() {
        super("Deop", "Removes Operator status from user", "Deop [user]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        if (PermissionManager.hasExec(event.getUser(), event)) {
            if (event.getChannel().isOp(event.getBot().getUserBot())) {
                if (event.getChannel().getOps().contains(event.getUser())) {
                    User u = event.getBot().getUserChannelDao().getUser(args[1]);
                    event.getChannel().send().message("It sucks to be " + u.getNick() + " right now :/");
                    event.getChannel().send().deOp(u);
                    return true;
                } else {
                    event.respond("You are not an OP!");
                }
            } else {
                event.getUser().send().notice("I can't do that!");
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
