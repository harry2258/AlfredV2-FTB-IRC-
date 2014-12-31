package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.Misc.RecentChanges;
import com.harry2258.Alfred.Misc.Reddit;
import com.harry2258.Alfred.Misc.Twitter;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;
//import org.tanukisoftware.wrapper.WrapperManager;

/**
 * Created by Hardik at 7:03 PM on 5/2/14.
 */
public class Restart extends Command {
    private Config config;
    private PermissionManager manager;

    public Restart() {
        super("Restart", "[WIP] Restarts the bot!", "Restart");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {

        if (PermissionManager.hasExec(event.getUser().getNick())) {
            //Clear some stuff
            Main.Chat.clear();
            Main.URL.clear();
            Main.relay.clear();
            Main.map.clear();
            Reddit.chaninfo.clear();
            Twitter.tweets.clear();
            RecentChanges.changes.clear();
            //Keeping login names, Less work after startup.

            //The restart part. Fancy? I think so.
            event.getBot().stopBotReconnect();
            event.getBot().sendIRC().quitServer("Restarting");
            return true;
        } else {
            event.getUser().send().notice("You are not an executive user!");
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
