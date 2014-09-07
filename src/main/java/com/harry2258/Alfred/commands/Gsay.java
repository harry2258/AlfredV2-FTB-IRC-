package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/14/14.
 */
public class Gsay extends Command {
    private Config config;
    private PermissionManager manager;

    public Gsay() {
        super("Gsay", "Global say!", "Gsay [#channel] [Message]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        if (PermissionManager.hasExec(event.getUser().getNick())) {
            String[] args = event.getMessage().split(" ");
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            String all = sb.toString().trim();
            if (args.length >= 3) {
                Channel t = event.getBot().getUserChannelDao().getChannel(args[1]);
                t.send().message(all);
                return true;
            }
        }
        return false;
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
