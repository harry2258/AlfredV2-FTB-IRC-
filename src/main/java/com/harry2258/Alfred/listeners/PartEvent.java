package com.harry2258.Alfred.listeners;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.QuitEvent;

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

        //if (Main.Login.containsKey(event.getUser().getNick())) { Main.Login.remove(event.getUser().getNick()); }

        if (Main.relay.containsKey(event.getChannel())) {
            Main.relay.get(event.getChannel()).send().message("[" + event.getChannel().getName() + "] " + event.getUser().getNick() + " left the channel.");
        }

        //if (Main.NotLoggedIn.contains(event.getUser().getNick())) Main.NotLoggedIn.remove(event.getUser().getNick());
    }

    public void onQuit(QuitEvent event) throws Exception {

        if (Main.Login.containsKey(event.getUser().getNick())) {
            Main.Login.remove(event.getUser().getNick());
        }

        if (Main.NotLoggedIn.contains(event.getUser().getNick())) Main.NotLoggedIn.remove(event.getUser().getNick());
    }

}
