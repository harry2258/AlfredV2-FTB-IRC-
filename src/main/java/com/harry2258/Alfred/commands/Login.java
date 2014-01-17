package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/15/14.
 */
public class Login extends Command {
    private Config config;
    private PermissionManager manager;

    public Login() {
        super("Login", "Log into the bot!");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        if (Main.Login.containsKey(event.getUser().getNick())) {
            event.getUser().send().notice("You are already logged in!");
            return true;
        }
        String account = Utils.getAccount(event.getUser(), event);
        Main.Login.put(event.getUser().getNick(), account);
        event.getUser().send().notice("You are now Logged in!");
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
