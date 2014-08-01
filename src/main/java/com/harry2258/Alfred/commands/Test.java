package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;
import org.pircbotx.hooks.events.MessageEvent;

import java.net.InetAddress;


public class Test extends Command {
    private Config config;
    private PermissionManager manager;

    public Test() {
        super("Test", "This is a test command", "Test!");
    }


    @Override
    public boolean execute(MessageEvent event) throws Exception {
        /*
        event.getChannel().send().message("Test!");
        event.getChannel().send().message(event.getUser().getUserLevels(event.getChannel()).toString());
        event.getChannel().send().message(("Logged in as: " + Utils.getAccount(event.getUser(), event)));
        */
        String host = event.getMessage().split(" ")[1];
        event.getChannel().send().message(InetAddress.getByName(host).getHostAddress());
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
