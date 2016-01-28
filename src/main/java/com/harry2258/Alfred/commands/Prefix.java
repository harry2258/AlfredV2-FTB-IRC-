package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.MessageUtils;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/14/14.
 */
public class Prefix extends Command {
    private Config config;
    private PermissionManager manager;

    public Prefix() {
        super("Prefix", "Changes the bot Prefix", "Prefix [New Prefix]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        if (event.getUser() == null) {
            return false;
        }
        if (PermissionManager.hasExec(event.getUser().getNick())) {
            String[] args = event.getMessage().split(" ");
            if (args.length == 2 && args[1].length() == 1) {
                String oldtrigger = config.getTrigger();
                config.setTrigger(args[1]);
                if (config.useDatabase)
                    Main.database.prepareStatement("UPDATE `Bot` SET `Bot_Trigger` = '" + config.getTrigger() + "' WHERE `Bot`.`Nick` = '" + event.getBot().getUserBot().getNick() + "';").execute();
                MessageUtils.sendUserNotice(event, "Bot prefix was set to " + config.getTrigger() + " from " + oldtrigger);
                return true;
            } else {
                return false;
            }
        } else {
            event.respond("You need to be Exec to change prefix!");
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
