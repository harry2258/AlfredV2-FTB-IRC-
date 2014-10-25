package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Hardik on 3/9/14.
 */
public class Update extends Command {

    private Config config;
    private PermissionManager manager;

    public Update() {
        super("Update", "Check for new updates!","Update");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");

        if (args.length == 2 && args[1].equalsIgnoreCase("info")) {
            try {
                URL url;
                url = new URL("http://harry2258.com/alfred/version.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                String result;
                while ((result = br.readLine()) != null) {
                    event.getChannel().send().message(result);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        try {
            URL url;
            url = new URL("http://harry2258.com/alfred/version.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String result;
            result = br.readLine();
            int remote = Integer.valueOf(result.replaceAll("\\.", ""));
            int current = Integer.valueOf(Main.version.replaceAll("\\.", ""));
            if (remote > current) {
                event.getChannel().send().message("New version of Alfred (" + result + ") is available now!");
            } else {
                event.getChannel().send().message("Alfred is up-to-date!");
            }
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
