package com.harry2258.Alfred.listeners;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.Misc.CreeperHost;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;
import com.harry2258.Alfred.commands.Ignore;
import org.pircbotx.Colors;
import org.pircbotx.hooks.ListenerAdapter;

/**
 * Created by Hardik at 10:52 AM on 8/1/2014.
 */
public class PrivateMessageEvent extends ListenerAdapter {
    private Config config;
    private PermissionManager manager;

    public PrivateMessageEvent(Config conf, PermissionManager man) {
        this.config = conf;
        this.manager = man;
    }

    public void onPrivateMessage(org.pircbotx.hooks.events.PrivateMessageEvent event) throws Exception {
        String EventUser;

        if (Main.Login.containsKey(event.getUser().getNick())) {
            EventUser = Main.Login.get(event.getUser().getNick());
        } else {
            EventUser = event.getUser().getNick();
        }

        if (event.getMessage().startsWith(config.getTrigger()) && !Ignore.ignored.contains(EventUser)) {
            String commandname = event.getMessage().split(" ")[0].substring(1).toLowerCase();
            String[] args = event.getMessage().split(" ");
            if (commandname.equalsIgnoreCase("paid")) {
                Boolean b = Utils.checkAccount(args[1]);
                if (b) {
                    event.getUser().send().message(args[1] + Colors.DARK_GREEN + " has " + Colors.NORMAL + "paid for minecraft");
                } else {
                    event.getUser().send().message(args[1] + Colors.RED + " has not " + Colors.NORMAL + "paid for minecraft");
                }
            }
            if (commandname.equalsIgnoreCase("chstatus")) {
            }
        }
    }
}
