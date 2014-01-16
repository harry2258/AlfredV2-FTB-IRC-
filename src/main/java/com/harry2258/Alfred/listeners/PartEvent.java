package com.harry2258.Alfred.listeners;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.ListenerAdapter;

/**
 * Created by Hardik on 1/15/14.
 */

public class PartEvent extends ListenerAdapter {
    private Config config;
    private PermissionManager manager;

    public PartEvent(Config conf, PermissionManager man) {
        this.config = conf;
        this.manager = man;
    }

    @Override
    public void onPart(org.pircbotx.hooks.events.PartEvent event) throws Exception {

        Main.Login.remove(event.getUser().getNick());
        System.out.println(event.getUser().getNick() + " was removed from the HashMap");
    }
}
