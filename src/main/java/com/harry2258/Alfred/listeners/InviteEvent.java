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
            String level = event.getBot().getUserChannelDao().getChannel("#batbot").getUserLevels(event.getBot().getUserChannelDao().getUser(event.getUser())).toString().replaceAll("]|\\[","");
            if (level.equalsIgnoreCase("op") || level.equalsIgnoreCase("voice")) {
                event.getBot().sendRaw().rawLineNow("PRIVMSG #batbot :Invited to " + event.getChannel() + " by " + event.getUser());
                event.getBot().sendIRC().joinChannel(event.getChannel());
            } else {
                event.getBot().getUserChannelDao().getUser(event.getUser()).send().message("You need to be Voice or higher in #batbot to invite Alfred to other channels!");
                return;
            }

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