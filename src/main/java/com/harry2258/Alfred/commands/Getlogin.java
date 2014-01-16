package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/15/14.
 */
public class Getlogin extends Command {

    private Config config;
    private PermissionManager manager;

    public Getlogin() {
        super("Getlogin", "Gets Getlogin names of all the users [Can take time with a lot of people!]", "Getlogin");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        if (PermissionManager.hasExec(event.getUser(), event)) {
            int i = 0;
            for (Channel channel : event.getBot().getUserBot().getChannels()) {
                for (User u : channel.getUsers()) {
                    if (u.isVerified()) {
                        if (!Main.Login.containsKey(u.getNick())) {
                            String user = Utils.getAccount(u, event);
                            Main.Login.put(u.getNick(), user);
                            i++;
                        }
                    }
                }
            }
            event.getChannel().send().message("Added " + i + " to Login HashMap!");
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
