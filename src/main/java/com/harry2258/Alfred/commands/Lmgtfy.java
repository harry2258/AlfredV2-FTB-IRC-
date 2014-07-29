package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/14/14.
 */

public class Lmgtfy extends Command {
    private Config config;
    private PermissionManager manager;

    public Lmgtfy() {
        super("Lmgtfy", "Let me Google that for you", "Do you really need help for this?");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] arg = event.getMessage().split(" ");
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < arg.length; i++) {
            sb.append(arg[i]).append(" ");
        }
        String finalurl = null;
        String message = sb.toString().trim();
        String y = "http://lmgtfy.com/?q=" + message;
        String x = y.replaceAll(" ", "+");
        try {
            finalurl = Utils.shortenUrl(x);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        event.getChannel().send().message(finalurl);
        return true;
    }

    @Override
    public void setConfig(Config config) {

    }

    @Override
    public void setManager(PermissionManager manager) {

    }
}
