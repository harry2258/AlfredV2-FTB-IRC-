package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.*;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/21/14.
 */
public class Fishbans extends Command {

    private Config config;
    private PermissionManager manager;

    public Fishbans() {
        super("Fishbans", "Check if the user has any McBans.", "Fishbans [user]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        MessageUtils.sendChannel(event, Utils.McBans(args[1]));
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
