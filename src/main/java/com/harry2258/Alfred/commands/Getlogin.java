package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;

/**
 * Created by Hardik on 1/15/14.
 */
public class Getlogin extends Command {

    private Config config;
    private PermissionManager manager;

    public Getlogin() {
        super("Getlogin", "Gets Getlogin names of all the users [Can take time with a lot of people!]", "Getlogin");
    }

    ArrayList<String> user = new ArrayList<String>();

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        if (PermissionManager.hasExec(event.getUser(), event)) {
            int i = 0;
            final long startTime = System.currentTimeMillis();
            for (Channel channel : event.getBot().getUserBot().getChannels()) {
                for (User u : channel.getUsers()) {
                    if (u.isVerified()) {
                        Thread.sleep(1000);
                        String account = Utils.getAccount(u, event);
                        user.add(account);
                        i++;
                    }
                }
                final long endTime = System.currentTimeMillis();
                event.getUser().send().notice("Got " + user.size() + " more users! It took me " + ((endTime - startTime) / 1000) + " seconds.");
                i = 0;
            }
            final long endTime = System.currentTimeMillis();
            event.getUser().send().notice("Got " + user.size() + " users! It took me " + ((endTime - startTime) / 1000) + " seconds.");
            int x = 0;
            for (x = 0; x < user.size(); x++) {
                Main.Login.put(event.getUser().getNick(), user.get(x));
                x++;
            }

            event.getChannel().send().message("Added " + x + " users to Login HashMap!");
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