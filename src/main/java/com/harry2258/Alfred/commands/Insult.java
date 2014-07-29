package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/14/14.
 */
public class Insult extends Command {
    private Config config;
    private PermissionManager manager;

    public Insult() {
        super("Insult", "SHAKESPEAREAN INSULTS!", "Insult");
    }

    private String last = "";

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String insult1 = "";
        do {
            insult1 = Utils.getInsult();
        } while (last.equalsIgnoreCase(insult1));

        String[] args = event.getMessage().split(" ");
        if (args.length == 2) {
            if (args[1].contains("batman") || args[1].equalsIgnoreCase("alfred") || args[1].contains("progwml6")) {
                event.getChannel().send().message(event.getUser().getNick() + ", " + insult1);
            } else {
                event.getChannel().send().message(args[1] + ", " + insult1);
            }
            return true;
        }

        if (args.length >= 2) {
            String channel;
            if (args[1].startsWith("#")) {
                channel = args[1];
                Channel chan = event.getBot().getUserChannelDao().getChannel(channel);
                String user = "";

                if (args.length == 3) {
                    if (args[2].contains("batman") || args[2].equalsIgnoreCase("alfred") || args[2].contains("progwml6")) {
                        user = event.getUser().getNick();
                    } else {
                        user = args[2] + ", ";
                    }
                }

                while (insult1.isEmpty()) {
                    wait();
                }
                try {
                    sendInsult(event, user + insult1);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                builder.append(args[i]).append(" ");
            }
            event.getChannel().send().message(builder.toString().trim() + ": " + insult1);
            return true;
        }

        event.getChannel().send().message(insult1);
        return true;
    }

    private static void sendInsult(MessageEvent event, String insult) {
        String[] args = event.getMessage().split(" ");
        String channel = args[1];
        if (args[1].startsWith("#")) {
            channel = args[1];
        } else {
            channel = "#" + args[1];
        }
        Channel chan = event.getBot().getUserChannelDao().getChannel(channel);
        if (chan.isInviteOnly() || chan.isSecret() || chan.isChannelPrivate()) {
            event.respond("I cannot join " + chan.getName() + "!");
        } else {
            if (event.getBot().getUserBot().getChannels().contains(chan)) {
                chan.send().message(insult);
            } else {
                event.getBot().sendIRC().joinChannel(chan.getName());
                chan.send().message(insult);
                chan.send().part();
            }
        }
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
