package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.*;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/14/14.
 */
public class Skin extends Command {

    private Config config;
    private PermissionManager manager;

    public Skin() {
        super("Skin", "Gets the link to the player Minecraft Skin! Thanks TNTUP!", "Skin [user]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        if (args.length == 2 && Utils.checkAccount(args[1])) {
            event.respond("https://tntup.me/player/" + args[1] + "/128  |  " + "http://api.bionikcraft.com/ftb-launcher/?u=" + args[1] + "&p=d1-p1-r&s=256&f=1");
            return true;
        } else {
            MessageUtils.sendChannel(event, "Please enter a valid MineCraft name!");
        }
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
