package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;
//import org.tanukisoftware.wrapper.WrapperManager;

/**
 * Created by Hardik at 7:03 PM on 5/2/14.
 */
public class Restart extends Command {
    private Config config;
    private PermissionManager manager;

    public Restart() {
        super("Restart", "Restarts the bot!", "");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        if (PermissionManager.hasExec(event.getUser(), event)) {
            event.getChannel().send().message("Restarting bot!");
            event.getBot().sendIRC().quitServer("Restart called by " + event.getUser().getNick());
            Thread.sleep(5000);
            //WrapperManager.restart();
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
