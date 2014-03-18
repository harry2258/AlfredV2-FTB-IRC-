package com.harry2258.Alfred.listeners;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.SetChannelBanEvent;

/**
 * Created by Hardik on 3/14/14.
 */

public class BanEvent extends ListenerAdapter {
    private Config config;
    private PermissionManager manager;

    public BanEvent(Config conf, PermissionManager man) {
        this.config = conf;
        this.manager = man;
    }

    public void onSetChannelBan(SetChannelBanEvent event) throws Exception {

        if (Main.relay.containsKey(event.getChannel())) {
            Main.relay.get(event.getChannel()).send().message("[" + event.getChannel().getName() + "] " + event.getUser().getNick() + " sets mode +b for " + event.getHostmask());
        }
    }

}
