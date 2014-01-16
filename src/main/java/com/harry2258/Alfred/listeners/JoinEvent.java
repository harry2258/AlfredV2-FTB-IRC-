package com.harry2258.Alfred.listeners;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.WhoisEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Hardik on 1/15/14.
 */

public class JoinEvent extends ListenerAdapter {

    private Config config;
    private PermissionManager manager;

    public JoinEvent(Config conf, PermissionManager man) {
        this.config = conf;
        this.manager = man;
    }

    @Override
    public void onJoin(org.pircbotx.hooks.events.JoinEvent event) throws Exception {
        Thread.sleep(1000);
        if (event.getUser().isVerified()) {
            if (!Main.Login.containsKey(event.getUser().getNick())) {
                String user = getAccount(event.getUser(), event);
                Main.Login.put(event.getUser().getNick(), user);
                System.out.println(event.getUser().getNick() + " was added to the HashMap");
            }
        } else {
            event.getUser().send().notice("You need to login to use the bot!");
        }
    }

    public static String getAccount(User u, org.pircbotx.hooks.events.JoinEvent event) {
        String user = "";
        event.getBot().sendRaw().rawLineNow("WHOIS " + u.getNick());
        WaitForQueue waitForQueue = new WaitForQueue(event.getBot());
        WhoisEvent test = null;
        try {
            test = waitForQueue.waitFor(WhoisEvent.class);
            waitForQueue.close();
            user = test.getRegisteredAs();
        } catch (InterruptedException ex) {
            Logger.getLogger(com.harry2258.Alfred.listeners.MessageEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return user;
    }
}
