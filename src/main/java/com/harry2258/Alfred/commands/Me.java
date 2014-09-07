package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/14/14.
 */
public class Me extends Command {
    private Config config;
    private PermissionManager manager;

    public Me() {
        super("Me", "Send an action to the channel", "Action [action]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        if (PermissionManager.hasExec(event.getUser().getNick())) {
            String[] args = event.getMessage().split(" ");

            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            event.getChannel().send().action(sb.toString().trim());
        }
        return true;
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
