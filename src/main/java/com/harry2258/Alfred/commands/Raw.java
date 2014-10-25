package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/14/14.
 */
public class Raw extends Command {
    private Config config;
    private PermissionManager manager;

    public Raw() {
        super("Raw", "Send a RAW line!", "Raw [Raw IRC line]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        if (PermissionManager.hasExec(event.getUser().getNick())) {
            StringBuilder sb = new StringBuilder();
            String[] args = event.getMessage().split(" ");
            for (int i = 1; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            event.getBot().sendRaw().rawLineNow(sb.toString().trim());
            return true;
        } else {

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
