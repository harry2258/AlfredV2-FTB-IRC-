package com.harry2258.Alfred.listeners;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;
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
        if (PrivateMessageEvent.waiting) {
            return;
        }
        if (config.isAutoRejoinChannel() && event.getRecipient().getNick().equalsIgnoreCase(event.getBot().getNick())) {
            event.getBot().sendIRC().joinChannel(event.getChannel().getName());
        }

        if (Main.NotLoggedIn.contains(event.getUser().getNick())) Main.NotLoggedIn.remove(event.getUser().getNick());

        if (Main.relay.containsKey(event.getChannel())) {
            Main.relay.get(event.getChannel()).send().message("[" + event.getChannel().getName() + "] " + event.getRecipient().getNick() + " was kicked from the channel by " + event.getUser().getNick());
        }

        if (event.getRecipient().getNick().contains("batman") && event.getUser().getNick().contains("Matakor"))
            event.getChannel().send().message("Matakor, " + Utils.getInsult());
    }
}
