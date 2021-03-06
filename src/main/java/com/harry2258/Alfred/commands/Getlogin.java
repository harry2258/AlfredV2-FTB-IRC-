package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.*;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;

/**
 * Created by Hardik on 1/15/14.
 */
public class Getlogin extends Command {

    ArrayList<String> user = new ArrayList<>();
    private Config config;
    private PermissionManager manager;

    public Getlogin() {
        super("Getlogin", "Gets Getlogin names of all the users [Can take time with a lot of people!]", "Getlogin");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        if (PermissionManager.hasExec(event.getUser().getNick())) {
            int i = 0;
            final long startTime = System.currentTimeMillis();
            for (User u : event.getChannel().getUsers()) {

                if (u.isVerified()) {
                    String name = u.getNick();
                    Thread.sleep(1500);
                    if (!Main.Login.containsKey(name)) {
                        String account = Utils.getAccount(u, event);
                        Main.Login.put(name, account);
                        i++;
                    }
                }
            }
            final long endTime = System.currentTimeMillis();
            MessageUtils.sendUserNotice(event, "Got " + i + " users from " + event.getChannel().getName() + "! It took me " + ((endTime - startTime) / 1000) + " seconds.");

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