package com.harry2258.Alfred.Misc;

import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import com.harry2258.Alfred.commands.Chat;
import org.pircbotx.PircBotX;

/**
 * Created by Hardik at 10:25 PM on 8/1/2014.
 */
public class ChatterBot extends Thread {
    public static String s = "";
    public static boolean UserAnswered = false;
    private static volatile boolean isRunning = true;
    private PircBotX bot;

    public ChatterBot(PircBotX bot) {
        this.bot = bot;
    }

    public void run() {
        try {
            ChatterBotFactory factory = new ChatterBotFactory();
            com.google.code.chatterbotapi.ChatterBot bot1 = factory.create(ChatterBotType.CLEVERBOT);
            ChatterBotSession bot1session = bot1.createSession();
            while (isRunning) while (UserAnswered) {
                s = bot1session.think(s);
                UserAnswered = false;
                Chat.unlockWaiter();
            }
        // Shoutout to ChatterBot for stupidly throwing Exception.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
