package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.*;
import org.pircbotx.hooks.events.MessageEvent;


public class Test extends Command {
    private Config config;
    private PermissionManager manager;

    public Test() {
        super("Test", "This is a test command", "Test!");
    }


    @Override
    public boolean execute(MessageEvent event) throws Exception {

        String group = event.getUser().getUserLevels(event.getChannel()).toString();
        MessageUtils.sendChannel(event, "Test!");
        MessageUtils.sendChannel(event, group.isEmpty() ? "None" : group);
        MessageUtils.sendChannel(event, ("Logged in as: " + Utils.getAccount(event.getUser(), event)));

        //Cause i'm too lazy to write the HTML
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
