package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.MessageUtils;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik at 12:22 PM on 10/21/2014.
 */
public class Flush extends Command {
    public Flush() {
        super("Flush", "Remove all info related an user", "Flush");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        if (PermissionManager.hasExec(event.getUser().getNick())) {
            if (args.length == 2) {
                try {
                    Main.Login.remove(args[1]);
                    Main.NotLoggedIn.remove(args[1]);
                    MessageUtils.sendUserNotice(event, "Flushed all information related to user " + args[1]);
                    if (Ignore.ignored.contains(args[1])) {
                        Ignore.ignored.remove(args[1]);
                        MessageUtils.sendUserNotice(event, Colors.BOLD + args[1] + " was on the ignored list!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                Main.Login.remove(event.getUser().getNick());
                Main.NotLoggedIn.remove(event.getUser().getNick());
            }
        } else {
            Main.Login.remove(event.getUser().getNick());
            Main.NotLoggedIn.remove(event.getUser().getNick());
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
