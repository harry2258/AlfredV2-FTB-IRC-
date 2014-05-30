package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik at 12:44 PM on 5/28/14.
 */
public class Stats extends Command {

    private Config config;
    private PermissionManager manager;

    public Stats() {
        super("Stats", "Gets stats on modpack that use YAMPST!", "Stats [Author] [Modpack Name]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        event.getChannel().send().setMode("+q", event.getUser().getNick());

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
