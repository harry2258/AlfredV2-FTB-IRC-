package com.harry2258.Alfred.listeners;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.ListenerAdapter;

/**
 * Created by Hardik on 1/19/14.
 */

public class ActionEvent extends ListenerAdapter {

    public ActionEvent(Config conf, PermissionManager man) {
        Config config = conf;
        PermissionManager manager = man;
    }

    public void onAction(org.pircbotx.hooks.events.ActionEvent event) throws Exception {
        if (PrivateMessageEvent.waiting) {
            return;
        }
        if (Main.relay.containsKey(event.getChannel())) {
            Main.relay.get(event.getChannel()).send().message("[" + event.getChannel().getName() + "] " + event.getUser().getNick() + " " + event.getAction());
        }
    }

}