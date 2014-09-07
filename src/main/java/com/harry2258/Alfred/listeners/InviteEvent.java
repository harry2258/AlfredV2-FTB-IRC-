package com.harry2258.Alfred.listeners;

import com.harry2258.Alfred.Database.Create;
import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.ListenerAdapter;

import java.sql.SQLException;

public class InviteEvent extends ListenerAdapter {

    private Config config;
    private PermissionManager manager;

    public InviteEvent(Config conf, PermissionManager man) {
        this.config = conf;
        this.manager = man;
    }

    @Override
    public void onInvite(org.pircbotx.hooks.events.InviteEvent event) {
        if (PrivateMessageEvent.waiting) {
            return;
        }
        if (config.isAutoAcceptInvite()) {
            event.getBot().sendRaw().rawLineNow("PRIVMSG #batbot :Invited to " + event.getChannel() + " by " + event.getUser());
            event.getBot().sendIRC().joinChannel(event.getChannel());
            if (config.useDatabase) {
                try {
                    Create.AddChannel(event.getChannel(), Main.database);
                } catch (SQLException s) {
                    s.printStackTrace();
                }
            }
        }
    }
}