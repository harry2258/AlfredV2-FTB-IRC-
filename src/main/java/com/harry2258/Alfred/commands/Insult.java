package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;
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

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String insult1 = Utils.getInsult();
        String[] args = event.getMessage().split(" ");
        if (args.length == 2 && args[1].contains("batman") || args[1].equalsIgnoreCase("alfred")  || args[1].contains("progwml6")){
            event.getChannel().send().message(event.getUser().getNick()+ ", " + insult1);
            return true;
        }
        if (args.length == 2) {
            event.getChannel().send().message(args[1] + ", " + insult1);
        } else {
            event.getChannel().send().message(insult1);
        }

        return true;
    }

    @Override
    public void setConfig(Config config) {

    }

    @Override
    public void setManager(PermissionManager manager) {

    }
}
