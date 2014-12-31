package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.Misc.RecentChanges;
import com.harry2258.Alfred.Misc.Reddit;
import com.harry2258.Alfred.Misc.Twitter;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.runnables.ChatSocketListener;
import org.pircbotx.Colors;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Kill extends Command {

    private Config config;
    private PermissionManager manager;

    public Kill() {
        super("Kill", "Shuts the bot down.", "Kill");
    }

    @Override
    public boolean execute(final MessageEvent event) {
        try {
            if (PermissionManager.hasExec(event.getUser().getNick())) {
                final String user = event.getUser().getNick();
                final WaitForQueue queue = new WaitForQueue(event.getBot());
                event.respond("Please type " + Colors.BOLD + config.getTrigger() + "kill" + Colors.NORMAL + " again in the next 10 seconds to kill the bot!");
                ExecutorService service = Executors.newSingleThreadExecutor();

                try {
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                MessageEvent currentEvent = null;
                                com.harry2258.Alfred.listeners.MessageEvent.waiting = true;
                                try {
                                    currentEvent = queue.waitFor(MessageEvent.class);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                if (currentEvent != null) {
                                    if (currentEvent.getMessage().equalsIgnoreCase(config.getTrigger() + "kill") && currentEvent.getUser().getNick().equals(user)) {
                                        System.out.println("Shutting down");
                                        event.getBot().stopBotReconnect();
                                        event.getBot().sendIRC().quitServer("Killed by " + event.getUser().getNick());
                                        Main.SafeStop = true;
                                        if (config.isEnableChatSocket()) {
                                            ChatSocketListener.kill();
                                        }
                                        if (config.isTwitterEnabled()) {
                                            Twitter.kill();
                                        }
                                        if (config.isRedditEnabled()) {
                                            Reddit.kill();
                                        }
                                        if (config.UpdaterChecker()) {
                                            com.harry2258.Alfred.Misc.Update.kill();
                                        }
                                        RecentChanges.kill();
                                        System.exit(2258);
                                    }
                                }
                            }
                        }

                    };

                    if (!com.harry2258.Alfred.listeners.MessageEvent.waiting) {
                        Future<?> f = service.submit(r);
                        f.get(10, TimeUnit.SECONDS);
                    }

                } catch (Exception ignored) {
                }
                service.shutdown();
                queue.close();
                com.harry2258.Alfred.listeners.MessageEvent.waiting = false;
                event.respond("Bot shutdown aborted!");
                return true;
            }
            event.respond("You need to be an Exec. user to be able to kill the bot!");
        } catch (Exception ex) {
            Logger.getLogger(Kill.class.getName()).log(Level.SEVERE, null, ex);
            return false;
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
