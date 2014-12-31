package com.harry2258.Alfred.listeners;

import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.DisconnectEvent;

import java.io.IOException;

/**
 * Created by Hardik on 4/1/14.
 */

//IGNORE THIS!
//YOU ALREADY FAILED

public class Disconnect extends ListenerAdapter {
    private PircBotX bot;
    private Config config;
    private PermissionManager manager;

    public Disconnect(Config conf, PermissionManager man) {
        this.config = conf;
        this.manager = man;
    }

    public void onDisconnect(DisconnectEvent event) throws IOException, IrcException {
        if (!event.getBot().getConfiguration().isAutoReconnect()) {
            event.getBot().stopBotReconnect();
        }
    }
}