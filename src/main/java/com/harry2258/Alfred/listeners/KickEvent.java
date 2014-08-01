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
        System.out.println(event.getRecipient().getNick() + " | " + event.getBot().getNick());
        if (config.isAutoRejoinChannel() && event.getRecipient().getNick().equalsIgnoreCase(event.getBot().getNick())) {
            event.getBot().sendIRC().joinChannel(event.getChannel().getName());
        }

        if (Main.Login.containsKey(event.getRecipient().getNick())) {
            Main.Login.remove(event.getRecipient().getNick());
        }

        if (Main.relay.containsKey(event.getChannel())) {
            Main.relay.get(event.getChannel()).send().message("[" + event.getChannel().getName() + "] " + event.getRecipient().getNick() + " was kicked from the channel by " + event.getUser().getNick());
        }
    }
}
