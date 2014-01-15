package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/14/14.
 */
public class Paid extends Command {

    private Config config;
    private PermissionManager manager;

    public Paid() {
        super("Paid", "Checks if the user has a paid Minecraft account!", "Paid [user]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        Boolean b = Utils.checkAccount(args[1]);
        if (b) {
            event.getChannel().send().message(args[1] + Colors.DARK_GREEN + " has " + Colors.NORMAL + "paid for minecraft");
            return true;
        } else {
            event.getChannel().send().message(args[1] + Colors.RED + " has not " + Colors.NORMAL + "paid for minecraft");
            return true;
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