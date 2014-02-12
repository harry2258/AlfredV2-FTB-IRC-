package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
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

        return false;
    }

    @Override
    public void setConfig(Config config) {

    }

    @Override
    public void setManager(PermissionManager manager) {

    }
}
