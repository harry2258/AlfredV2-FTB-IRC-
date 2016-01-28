package com.harry2258.Alfred.Misc;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Config;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Hardik on 3/9/14.
 */
public class Update extends Thread {

    private static volatile boolean isRunning = true;
    private PircBotX bot;
    private Config config;
    private int time;

    public Update(PircBotX bot, Config config) {
        this.bot = bot;
        this.config = config;
        this.time = config.getUpdateInterval() * 1000;
    }

    public static void kill() {

        Thread.currentThread().interrupt();
        isRunning = false;
    }

    public void run() {
        isRunning = true;

        try {
            System.out.println("Updater started. Waiting 1 minute for bot to start up.");
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (isRunning) {
            try {
                Channel chan = bot.getUserChannelDao().getChannel(config.Updaterchannel());
                URL url;
                url = new URL("http://harry2258.com/alfred/version.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                String result = br.readLine();
                int remote = Integer.valueOf(result.replaceAll("\\.", ""));
                int current = Integer.valueOf(Main.version.replaceAll("\\.", ""));
                if (remote > current) {
                    chan.send().message("New version of Alfred (" + result + ") is available now!");
                    Thread.sleep(10800000);
                }
                Thread.sleep(time);
            } catch (InterruptedException|IOException e) {
                e.printStackTrace();
            }
        }
    }
}