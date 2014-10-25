package com.harry2258.Alfred.commands;

import com.google.common.collect.ImmutableSortedSet;
import com.harry2258.Alfred.api.*;
import org.pircbotx.hooks.events.MessageEvent;

import java.lang.reflect.Array;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;


public class Test extends Command {
    private Config config;
    private PermissionManager manager;

    public Test() {
        super("Test", "This is a test command", "Test!");
    }


    @Override
    public boolean execute(MessageEvent event) throws Exception {

        event.getChannel().send().message("Test!");
        event.getChannel().send().message(event.getUser().getUserLevels(event.getChannel()).toString());
        event.getChannel().send().message(("Logged in as: " + Utils.getAccount(event.getUser(), event)));

        /*
        ArrayList<String> help = new ArrayList<>();
        help.clear();
        for (String s : CommandRegistry.commands.keySet()) {
            Command command = CommandRegistry.getCommand(s);
            help.add(String.format("<details><summary><strong>%s</strong></summary>\n<p><strong>Summary</strong>: %s</p>\n<p><strong>Example:</strong> %s</p></details><p></p>", command.getName(), command.getDescription().replaceAll("<", "[").replaceAll(">","]"), command.getHelp().replaceAll("<", "[").replaceAll(">","]")));
            //System.out.println(String.format("%s - %s", command.getName(), command.getDescription()));
        }

        HashSet<String> hs = new HashSet<>();
        hs.addAll(help);

        help.clear();
        help.addAll(hs);

        Collections.sort(help);
        for (String a : help) {
            System.out.println(a);
        }
        */
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
