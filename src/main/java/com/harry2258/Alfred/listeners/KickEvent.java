package com.harry2258.Alfred.listeners;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.ListenerAdapter;

/**
 * Created by Hardik on 2/15/14.
 */
public class KickEvent extends ListenerAdapter {
    private Config config;
    private PermissionManager manager;

    public KickEvent(Config conf, PermissionManager man) {
        this.config = conf;
        this.manager = man;
    }

    public void onKick(org.pircbotx.hooks.events.KickEvent event) {

        if (config.isAutoRejoinChannel() && event.getUser().getNick().equalsIgnoreCase(event.getBot().getNick())) {
            String rejoin = event.getChannel().getName();
            event.getBot().sendIRC().joinChannel(rejoin);
        }

        if (Main.Login.containsKey(event.getUser().getNick())) {
            Main.Login.remove(event.getUser().getNick());
            System.out.println(event.getUser().getNick() + " was removed from the HashMap");
        }

        if (Main.relay.containsKey(event.getChannel())) {
            Main.relay.get(event.getChannel()).send().message("[" + event.getChannel().getName() + "] " + event.getUser().getNick() + " was kicked from the channel.");
        }
    }
}
