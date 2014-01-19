package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;

/**
 * Created by Hardik on 1/17/14.
 */
public class Spy extends Command {
    private Config config;
    private PermissionManager manager;

    public Spy() {
        super("Spy", "Relays all channel from the spy channel.", "Spy [channel]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        if (PermissionManager.hasExec(event.getUser(), event)) {
            String[] args = event.getMessage().split(" ");
            if (args[1].startsWith("list")) {
                ArrayList<String> Channels = new ArrayList<>();
                String Message = null;

                for (int i = 0; i < Main.relay.size(); i++) {
                    Channels.add(Main.relay.get(i).getName());
                }

                for (String s : Channels) {
                    Message += s + "\t";
                }

                event.getUser().send().notice(Message);
                return true;
            }

            Channel target = event.getBot().getUserChannelDao().getChannel(args[1]);
            Channel chan = event.getChannel();
            if (Main.relay.containsKey(target)) {
                Main.relay.remove(target);
                event.getChannel().send().message("no longer spying on channel " + target.getName());
                return true;
            }
            Main.relay.put(target, chan);
            event.getChannel().send().message("now spying on channel " + target.getName());
            return true;
        } else {
            event.getUser().send().notice("You need to have Exec to use this command!");
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
