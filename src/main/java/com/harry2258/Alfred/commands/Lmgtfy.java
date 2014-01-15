package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

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
            URL wiki;
            wiki = new URL("http://is.gd/create.php?format=simple&url=" + x);
            BufferedReader br = new BufferedReader(new InputStreamReader(wiki.openStream()));
            finalurl = br.readLine();
            br.close();
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
