package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Misc.CreeperHost;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;


public class Chstatus extends Command {

    private Config config;
    private PermissionManager manager;

    public Chstatus() {
        super("ChStatus", "Shows status of CreeperHost repos", ":3");
    }

    @Override
    public boolean execute(MessageEvent event) {
        new Thread(new CreeperHost(event)).start();
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