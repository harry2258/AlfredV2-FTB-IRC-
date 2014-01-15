package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/14/14.
 */
public class Remind extends Command {

    private Config config;
    private PermissionManager manager;

    public Remind() {
        super("Remind", "Adds a reminder for the user!", "Remind [user] [reminder]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
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
