package com.harry2258.Alfred.Misc;

import com.google.code.chatterbotapi.*;

import com.harry2258.Alfred.commands.Chat;
import org.pircbotx.PircBotX;

/**
 * Created by Hardik at 10:25 PM on 8/1/2014.
 */
public class ChatterBot extends Thread {
    PircBotX bot;
    public ChatterBot(PircBotX bot) {
        this.bot = bot;
    }

    private static volatile boolean isRunning = true;
    public static String s = "";
    public static boolean UserAnswered = false;

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
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
