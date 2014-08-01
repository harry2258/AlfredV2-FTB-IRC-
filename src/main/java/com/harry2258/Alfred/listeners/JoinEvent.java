package com.harry2258.Alfred.listeners;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.WhoisEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
            }
        }

        String path;
        if (Main.Login.containsKey(event.getUser().getNick())) {
            path = System.getProperty("user.dir") + "/Reminders/" + Main.Login.get(event.getUser().getNick()) + ".txt";
        } else {
            path = System.getProperty("user.dir") + "/Reminders/" + event.getUser().getNick() + ".txt";
        }

        File reminder = new File(path);

        if (reminder.exists()) {
            BufferedReader in = new BufferedReader(new FileReader(reminder));
            String tmp;
            event.getUser().send().notice("=========Reminders=========");
            while ((tmp = in.readLine()) != null) {
                event.getUser().send().notice(tmp);
            }
            in.close();
            reminder.delete();
        }

        if (Main.relay.containsKey(event.getChannel())) {
            Main.relay.get(event.getChannel()).send().message("[" + event.getChannel().getName() + "] " + event.getUser().getNick() + " joined the channel.");
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
