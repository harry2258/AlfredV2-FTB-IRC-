package com.harry2258.Alfred.commands;

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
                                Long start = System.currentTimeMillis();
                                MessageEvent currentEvent = null;

                                try {
                                    currentEvent = queue.waitFor(MessageEvent.class);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                if (currentEvent != null) {
                                    if (currentEvent.getMessage().equalsIgnoreCase(config.getTrigger() + "kill") && currentEvent.getUser().getNick().equals(user)) {
                                        event.getBot().stopBotReconnect();
                                        event.getBot().sendIRC().quitServer("Killed by " + event.getUser().getNick());
                                        if (config.isEnableChatSocket()) {
                                            ChatSocketListener.kill();
                                        }
                                        if (config.isEnabledTwitter()) {
                                            Twitter.kill();
                                        }
                                        if (config.isRedditEnabled()) {
                                            Reddit.kill();
                                        }
                                        if (config.UpdaterChecker()) {
                                            com.harry2258.Alfred.Misc.Update.kill();
                                        }
                                        System.out.println("Shutting down");
                                        System.exit(1);
                                    }
                                }
                            }
                        }
                    };

                    Future<?> f = service.submit(r);
                    f.get(10, TimeUnit.SECONDS);     // attempt the task for two minutes

                } catch (Exception e) {
                    e.printStackTrace();
                }

                service.shutdown();
                queue.close();
                event.respond("Bot shutdown aborted!");
                return true;
            }
            event.respond("You need to be Exec to kill the bot!");
        } catch (Exception ex) {
            Logger.getLogger(Kill.class.getName()).log(Level.SEVERE, null, ex);
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
