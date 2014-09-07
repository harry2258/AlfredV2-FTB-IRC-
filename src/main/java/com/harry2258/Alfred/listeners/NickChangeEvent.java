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

public class NickChangeEvent extends ListenerAdapter {

    private Config config;
    private PermissionManager manager;

    public NickChangeEvent(Config conf, PermissionManager man) {
        this.config = conf;
        this.manager = man;
    }

    @Override
    public void onNickChange(org.pircbotx.hooks.events.NickChangeEvent event) throws Exception {
        String oldNick = event.getOldNick();
        String newNick = event.getNewNick();

        String user = getAccount(event.getUser(), event);

        File reminder = new File(System.getProperty("user.dir") + "/Reminders/" + user + ".txt");
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


        Main.Login.remove(oldNick);
        Main.Login.put(newNick, user);

    }

    public static String getAccount(User u, org.pircbotx.hooks.events.NickChangeEvent event) {
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
        waitForQueue.close();
        return user;
    }
}
