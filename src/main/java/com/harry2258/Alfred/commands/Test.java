package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.*;
import org.json.JSONObject;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;


public class Test extends Command {

    private Config config;
    private PermissionManager manager;

    public Test() {
        super("Test", "This is a test command", "Test!");
    }


    @Override
    public boolean execute(MessageEvent event) throws Exception {

        event.getChannel().send().message("Test!");
        event.getChannel().send().message(event.getUser().getUserLevels(event.getChannel()).toString());
        event.getChannel().send().message(("Logged in as: " + Utils.getAccount(event.getUser(), event)));
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
