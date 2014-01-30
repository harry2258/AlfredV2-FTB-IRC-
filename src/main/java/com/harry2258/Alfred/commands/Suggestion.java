package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/27/14.
 */
public class Suggestion extends Command {
    public Suggestion() {
        super("Suggestion");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {

        String[] args = event.getMessage().split(" ");

        StringBuilder br= new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            br.append(args[i]).append(" ");
        }

        Error.Suggestion.put(event.getUser().getNick(), br.toString());
        event.getUser().send().notice("Added to suggestion list!");
        return true;
    }

    @Override
    public void setConfig(Config config) {

    }

    @Override
    public void setManager(PermissionManager manager) {

    }
}
