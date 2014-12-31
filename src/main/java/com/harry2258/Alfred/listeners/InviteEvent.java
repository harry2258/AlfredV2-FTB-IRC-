package com.harry2258.Alfred.listeners;

import com.harry2258.Alfred.Database.Create;
import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.WhoisEvent;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InviteEvent extends ListenerAdapter {

    private Config config;
    private PermissionManager manager;

    public InviteEvent(Config conf, PermissionManager man) {
        this.config = conf;
        this.manager = man;
    }

    public static String getAccount(User u, org.pircbotx.hooks.events.InviteEvent event) {
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

    @Override
    public void onInvite(org.pircbotx.hooks.events.InviteEvent event) {
        if (PrivateMessageEvent.waiting) {
            return;
        }
        User user = event.getBot().getUserChannelDao().getUser(event.getUser());
        if (!Main.Login.containsKey(event.getUser().getNick()) && !Main.NotLoggedIn.contains(event.getUser().getNick())) {
            if (user.isVerified()) {
                String account = getAccount(user, event);
                Main.Login.put(event.getUser().getNick(), account);
            } else {
                Main.NotLoggedIn.add(event.getUser().getNick());
            }
        }
        if (config.isAutoAcceptInvite()) {
            String level = event.getBot().getUserChannelDao().getChannel("#batbot").getUserLevels(event.getBot().getUserChannelDao().getUser(event.getUser())).toString().replaceAll("]|\\[", "");
            System.out.println(event.getUser());
            System.out.println(PermissionManager.hasExec(event.getUser().getNick()));
            if (level.equalsIgnoreCase("op") || level.equalsIgnoreCase("voice") || PermissionManager.hasExec(event.getUser().getNick())) {
                event.getBot().sendRaw().rawLineNow("PRIVMSG #batbot :Invited to " + event.getChannel() + " by " + event.getUser());
                event.getBot().sendIRC().joinChannel(event.getChannel());
            } else {
                event.getBot().getUserChannelDao().getUser(event.getUser()).send().message("You need to be Voice or higher in #batbot to invite Alfred to other channels!");
                return;
            }

            if (config.useDatabase) {
                try {
                    Create.AddChannel(event.getChannel(), Main.database);
                } catch (SQLException s) {
                    s.printStackTrace();
                }
            }
        }
    }
}