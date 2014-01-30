package com.harry2258.Alfred.Misc;

import com.harry2258.Alfred.api.Utils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;

/**
 * Created by Hardik on 1/27/14.
 */
@SuppressWarnings("InfiniteLoopStatement")
public class McStatusChecker extends Thread {
    PircBotX bot;
    private String status = "";

    public McStatusChecker(PircBotX bot) {

        this.bot = bot;
    }

    public void run() {
        try {
            System.out.println("Sleeping for 2 minutes. Waiting for bot to start up.");
            Thread.sleep(120000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            String temp = Utils.checkMojangServers();
            if (!status.equals(temp)) {
                status = temp;
                for (Channel chan : bot.getUserBot().getChannels()) {
                    chan.send().message(temp);
                }
            }
            try {
                Thread.sleep(120000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
