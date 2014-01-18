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
        super("URL", "Turn of url scanning!", "ulr [on/off]");
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
                ArrayList<String> url = new ArrayList<>();
                String Message = null;

                for (int i = 0; i < Main.URL.size(); i++) {
                    url.add(Main.URL.get(i));
                }

                for (String s : url) {
                    Message += s + " \t";
                }

                event.getUser().send().notice(Message);
                return true;
            }

            //boolean state = Boolean.valueOf(args[1]);
            String state = args[1];
            if (state.equalsIgnoreCase("on") || state.equalsIgnoreCase("true") || state.equalsIgnoreCase("yes")) {
                Main.URL.add(event.getChannel().getName());
                event.getUser().send().notice("Added " + event.getChannel().getName() + " to URL scanning!");
            }
            if (state.equalsIgnoreCase("off") || state.equalsIgnoreCase("false") || state.equalsIgnoreCase("no")) {
                Main.URL.remove(event.getChannel().getName());
                event.getUser().send().notice("Removed " + event.getChannel().getName() + " from URL scanning!");
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
