package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/14/14.
 */
public class Prefix extends Command {
    private Config config;
    private PermissionManager manager;

    public Prefix() {
        super("Prefix", "Changes the bot Prefix", "Prefix [New Prefix]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        if (PermissionManager.hasExec(event.getUser(), event)) {
            String[] args = event.getMessage().split(" ");
            if (args.length == 2) {
                String oldtrigger = config.getTrigger();
                String newtrigger = args[1];
                config.setTrigger(args[1]);
                event.getUser().send().notice("Bot prefix was set to " + newtrigger + " from " + oldtrigger);
                return true;
            } else {
                return false;
            }
        } else {
            event.respond("You need to be Exec to change prefix!");
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
