package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Misc.ChatterBot;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik at 10:32 PM on 8/1/2014.
 */
public class Chat extends Command {
    private Config config;
    private PermissionManager manager;
    public static final Object monitor = new Object();
    public static boolean monitorState = false;

    public Chat() {
        super("Chat", "Bored? Chat with Alfred!", "Chat [Text]");
    }

    @Override
    public boolean execute(MessageEvent event) throws InterruptedException {
        String[] args = event.getMessage().split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String LastBotAnswer = ChatterBot.s;
        String UserAnswer = sb.toString().trim();
        ChatterBot.s = UserAnswer;
        ChatterBot.UserAnswered = true;
        waitForThread();
        if (LastBotAnswer.equalsIgnoreCase(ChatterBot.s)) {
            System.out.println("Same answer as last time. Getting new answer!");
            ChatterBot.s = UserAnswer;
            ChatterBot.UserAnswered = true;
            waitForThread();
        }
        event.getChannel().send().message(ChatterBot.s);
        wait(1000);
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

    public static void waitForThread() {
        monitorState = true;
        while (monitorState) {
            synchronized (monitor) {
                try {
                    monitor.wait(); // wait until notified
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void unlockWaiter() {
        synchronized (monitor) {
            monitorState = false;
            monitor.notifyAll(); // unlock again
        }
    }
}
