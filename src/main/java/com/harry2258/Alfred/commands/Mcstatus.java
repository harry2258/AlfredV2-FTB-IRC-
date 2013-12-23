package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;
import org.pircbotx.hooks.events.MessageEvent;


public class Mcstatus extends Command {
    private Config config;
    private PermissionManager manager;

    public Mcstatus() {
        super("Mcstatus", "Shows the status of various minecraft servers");
    }

    @Override
    public boolean execute(MessageEvent event) {
        event.getChannel().send().message(Utils.checkMojangServers());
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
