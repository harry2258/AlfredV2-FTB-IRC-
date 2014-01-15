package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.User;
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
        if (args.length == 2) {
            User user = event.getBot().getUserChannelDao().getUser(args[1]);
            event.respond("https://tntup.me/player/" + user.getNick() + "/128");
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
