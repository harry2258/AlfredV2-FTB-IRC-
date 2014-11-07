package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.MessageUtils;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.File;

/**
 * Created by Hardik on 1/15/14.
 */
public class Delcmd extends Command {
    private Config config;
    private PermissionManager manager;

    public Delcmd() {
        super("Delcmd", "Deletes a custom command!", "Delcmd [command]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        if (args.length == 2) {

            String commandname = args[1];
            File command = new File("commands/" + event.getChannel().getName() + "/" + commandname + ".cmd");
            if (command.exists()) {
                command.delete();
                MessageUtils.sendUserNotice(event, "deleted command \"" + commandname + "\"");
                return true;
            } else {
                MessageUtils.sendUserNotice(event, "There is no custom command by that name!");
                return true;
            }
        }
        return false;
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
