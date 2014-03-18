package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;

/**
 * Created by Hardik on 1/17/14.
 */
public class Url extends Command {
    private Config config;
    private PermissionManager manager;

    public Url() {
        super("URL", "Toggle url scanning!", "ulr [on/off]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        if (args.length == 2) {
            if (args[1].startsWith("list")) {
                if (Main.URL.isEmpty()) {
                    event.getUser().send().notice("There are no channels that have URL scanning!");
                    return true;
                }

                String Message = Main.URL.toString();
                event.getUser().send().notice(Message);
                return true;
            }
            String state = args[1];

            if (state.equalsIgnoreCase("youtube") || state.equalsIgnoreCase("yt")) {
                Main.URL.put(event.getChannel().getName(), "YouTube");
                event.getUser().send().notice("Added " + event.getChannel().getName() + " to YouTube URLs only scanning!");
                return true;
            }

            if (state.equalsIgnoreCase("all") || state.equalsIgnoreCase("on") || state.equalsIgnoreCase("true") || state.equalsIgnoreCase("yes")) {
                Main.URL.put(event.getChannel().getName(), "All");
                event.getUser().send().notice("Added " + event.getChannel().getName() + " to URL scanning!");
                return true;
            }

            if (state.equalsIgnoreCase("none") || state.equalsIgnoreCase("off") || state.equalsIgnoreCase("false") || state.equalsIgnoreCase("no")) {
                Main.URL.remove(event.getChannel().getName());
                event.getUser().send().notice("Removed " + event.getChannel().getName() + " from URL scanning!");
                return true;
            }
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
