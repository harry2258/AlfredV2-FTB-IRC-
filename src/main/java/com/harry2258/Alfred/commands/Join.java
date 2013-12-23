package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;

public class Join extends Command {

    private Config config;
    private PermissionManager manager;

    public Join() {
        super("Join", "Tells the bot to join a channel", "join #channel");
    }

    @Override
    public boolean execute(MessageEvent event) {
        String[] args = event.getMessage().split(" ");
        Channel target = event.getBot().getUserChannelDao().getChannel(args[1]);
        if (target.isInviteOnly()) {
            event.getBot().sendRaw().rawLineNow("KNOCK " + target.getName() + " :Asked to join this channel by user " + event.getUser().getNick() + " in channel " + event.getChannel().getName());
        }
        event.getBot().sendIRC().joinChannel(target.getName());
        return true;
    }

    @Override
    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public void setManager(PermissionManager manager) {
        this.manager = manager;
    }
}
