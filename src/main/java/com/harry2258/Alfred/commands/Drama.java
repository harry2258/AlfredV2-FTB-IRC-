package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.*;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik at 5:22 PM on 9/6/2014.
 */
public class Drama extends Command {
    private Config config;
    private PermissionManager manager;

    public Drama() {
        super("Drama", "Minecraft Drama is the best kind of drama!", "Drama");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        try {
            MessageUtils.sendChannel(event, Utils.getDrama());
        } catch (Exception e) {
            return false;
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
