package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/14/14.
 */

public class Cinsult extends Command {
    private Config config;
    private PermissionManager manager;

    public Cinsult() {
        super("Cinsult", "SHAKESPEAREAN (CHANNEL) INSULTS!", "Cinsult [#Channel]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        Channel chan = event.getBot().getUserChannelDao().getChannel(args[1]);
        String insult1 = Utils.getInsult();
        System.out.println(chan.isInviteOnly());
        if (chan.isInviteOnly() || chan.isSecret() || chan.isChannelPrivate()) {
            event.respond("I cannot join " + chan.getName() + "!");
        } else {
            event.getBot().sendIRC().joinChannel(chan.getName());
            chan.send().message(insult1);
            chan.send().part();
            return true;
        }
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
