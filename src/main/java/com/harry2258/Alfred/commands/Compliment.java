package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.*;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 3/20/14.
 */
public class Compliment extends Command {
    private Config config;
    private PermissionManager manager;
    private String last = "";

    public Compliment() {
        super("Compliment", "Make someones day a little brighter!", "Compliment [user]");
    }

    private static void sendCompliment(MessageEvent event, String Compliment, Channel chan) {
        String[] args = event.getMessage().split(" ");

        if (chan.getName().equalsIgnoreCase("#dragonweyr") || chan.getName().equalsIgnoreCase("#help") || chan.getName().equalsIgnoreCase("#lobby") || chan.getName().equalsIgnoreCase("#coders") || chan.getName().equalsIgnoreCase("#esper") || chan.getName().equalsIgnoreCase("#helper")) {
            MessageUtils.sendChannel(event, "YOU CRAZY SENDIN' ME OUT THERE?! AWW HELL NAW!!");
            return;
        }
        if (chan.isInviteOnly() || chan.isSecret() || chan.isChannelPrivate()) {
            event.respond("I cannot join " + chan.getName() + "!");
        } else {
            if (event.getBot().getUserBot().getChannels().contains(chan)) {
                chan.send().message(Compliment);
            } else {
                event.getBot().sendIRC().joinChannel(chan.getName());
                chan.send().message(Compliment);
                chan.send().part();
            }
        }
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String compliment = "";
        do {
            compliment = Utils.getCompliment();
        } while (last.equalsIgnoreCase(compliment));

        last = compliment;
        String[] args = event.getMessage().split(" ");
        if (args.length == 2) {
            System.out.println(args[1]);
            MessageUtils.sendChannel(event, args[1].trim() + ", " + compliment);
            return true;
        }
        if (args.length >= 2) {
            String channel = args[1];
            if (args[1].startsWith("#")) {
                channel = args[1];
            } else {
                channel = "#" + args[1];
            }

            Channel chan = event.getBot().getUserChannelDao().getChannel(channel);
            String user = "";
            if (args.length == 3) {
                user = args[2].trim() + ", ";
            }

            try {
                sendCompliment(event, user + compliment, chan);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        MessageUtils.sendChannel(event, compliment);
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
