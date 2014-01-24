package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.CommandRegistry;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

public class Help extends Command {

    private PermissionManager manager;
    private Config config;

    public Help() {
        super("Help", "List command names and how to use them.", "help or help <command>");
    }

    @Override
    public boolean execute(MessageEvent event) {
        String[] args = event.getMessage().split(" ");
        if (args.length == 1) {
            /*
            for (String s : CommandRegistry.commands.keySet()) {
                Command command = CommandRegistry.getCommand(s);
                event.getUser().send().notice(String.format(Colors.RED + "%s" + Colors.NORMAL + " - %s", command.getName(), command.getDescription()));
            }
            */
            event.getChannel().send().message("Please go to the http://harry2258.com/alfred !");
            event.getChannel().send().message("or you can use Help [command name]");
            return true;
        }
        if (args.length == 2) {
            Command command = CommandRegistry.getCommand(StringUtils.capitalize(args[1].toLowerCase()));
            if (command != null) {
                event.getUser().send().notice(String.format("Help for command: " + Colors.RED + "%s" + Colors.NORMAL + " - %s - %s", command.getName(), command.getDescription(), command.getHelp()));
            } else {
                event.getUser().send().notice("Could not find the command " + args[1] + ", are you sure you spelled it right?");
            }
            return true;
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
