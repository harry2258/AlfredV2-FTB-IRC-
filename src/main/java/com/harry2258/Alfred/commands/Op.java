package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/14/14.
 */
public class Op extends Command {

    public Op() {
        super("Op", "Gives +o to user", "Op [user]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        if (PermissionManager.hasExec(event.getUser(), event)) {
            if (event.getChannel().getOps().contains(event.getUser())) {
                if (args.length == 2) {
                    User u = event.getBot().getUserChannelDao().getUser(args[1]);
                    event.getChannel().send().op(u);
                    return true;
                }
            } else {
                event.respond("You are not an OP!");
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
