package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik at 12:46 AM on 9/29/2014.
 */
public class Identify extends Command{

    private Config config;
    private PermissionManager manager;

    public Identify() {
        super("Identify", "Have the bot identify itself with NickServ!");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        try {
            event.getBot().sendRaw().rawLineNow("NICKSERV IDENTIFY "  + config.getBotIdent() + " " + config.getBotPassword());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
