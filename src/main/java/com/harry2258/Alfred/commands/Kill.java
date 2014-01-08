package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Kill extends Command {

    private Config config;
    private PermissionManager manager;

    public Kill() {
        super("Kill", "Shuts the bot down.", "Kill");
    }

    @Override
    public boolean execute(MessageEvent event) {
        try {
            if (PermissionManager.hasExec(event.getUser(), event.getChannel(), event)) {
            event.getBot().stopBotReconnect();
            event.getBot().sendIRC().quitServer("Shutting down...");
            return true;
            }
        } catch (Exception ex) {
            Logger.getLogger(Kill.class.getName()).log(Level.SEVERE, null, ex);
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
