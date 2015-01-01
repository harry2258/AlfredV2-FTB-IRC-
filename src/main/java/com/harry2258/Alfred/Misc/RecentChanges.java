package com.harry2258.Alfred.Misc;

import com.google.gson.JsonObject;
import com.harry2258.Alfred.api.JsonUtils;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hardik at 12:48 PM on 12/30/2014.
 */

public class RecentChanges extends Thread {
    public static List<String> changes = new ArrayList<>();
    private static volatile boolean isRunning = true;
    PircBotX bot;

    public RecentChanges(PircBotX bot) {
        this.bot = bot;
    }

    public static void kill() {
        isRunning = false;
    }

    public void run() {
        String ChangeUrl = "http://ftb.gamepedia.com/api.php?action=query&list=recentchanges&rcprop=title|user|ids|comment|timestamp&rclimit=3&format=json";
        try {
            System.out.println("[FTB Wiki] Sleeping for 1 minutes. Waiting for bot to start up.");
            Thread.sleep(60000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Channel chan = null;
        String tmp = null;
        boolean FoundChannel = false;
        try {
            chan = bot.getUserChannelDao().getChannel("#ftb-wiki-recentchanges");
            FoundChannel = true;
        } catch (Exception e) {
            System.out.println("The bot is not in the #ftb-wiki-recentchanges channel");
            FoundChannel = false;
        }

        while (isRunning && FoundChannel) {
            try {
                URL change = new URL(ChangeUrl);
                URLConnection c = change.openConnection();
                c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17");
                String json = JsonUtils.convertStreamToString(c.getInputStream());
                JsonObject jsonObj = JsonUtils.getJsonObject(json).getAsJsonObject("query").getAsJsonArray("recentchanges").get(0).getAsJsonObject();

                String Type = jsonObj.get("type").getAsString();
                Type = Character.toUpperCase(Type.charAt(0)) + Type.substring(1).toLowerCase();
                String Title = jsonObj.get("title").getAsString();
                String User = jsonObj.get("user").getAsString();
                String comment = jsonObj.get("comment").getAsString();
                String Time = jsonObj.get("timestamp").getAsString();

                tmp = "[" + Type + "] User " + User + " edited the page " + Title + " | Comment: " + comment + " | Time: " + Time;

                if (!changes.contains(tmp)) {
                    changes.add(tmp);

                    String temp;
                    if (Type.equalsIgnoreCase("New")) {
                        temp = "[" + Colors.RED + Colors.BOLD + Type + Colors.NORMAL + "] User " + Colors.BOLD + User + Colors.NORMAL + " created the page " + Colors.BOLD + Title + Colors.NORMAL + " | " + Colors.BOLD + "Comment: " + Colors.NORMAL + comment;
                        chan.send().message(temp);
                    } else {
                        temp = "[" + Colors.RED + Colors.BOLD + Type + Colors.NORMAL + "] User " + Colors.BOLD + User + Colors.NORMAL + " edited the page " + Colors.BOLD + Title + Colors.NORMAL + " | " + Colors.BOLD + "Comment: " + Colors.NORMAL + comment;
                        chan.send().message(temp);
                    }
                }

                Thread.sleep(30000);
            } catch (Exception e) {
                changes.remove(tmp);
                e.printStackTrace();
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
