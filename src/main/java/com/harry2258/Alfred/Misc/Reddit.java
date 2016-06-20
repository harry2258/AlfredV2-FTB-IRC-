package com.harry2258.Alfred.Misc;

import com.google.gson.JsonObject;
import com.harry2258.Alfred.api.JsonUtils;
import com.harry2258.Alfred.api.Utils;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class Reddit extends Thread {
    public static HashMap<String, String> chaninfo = new HashMap<>();
    private static volatile boolean isRunning = true;
    private PircBotX bot;

    public Reddit(PircBotX bot) {
        this.bot = bot;
    }

    public static void kill() {

        Thread.currentThread().interrupt();
        isRunning = false;
    }

    public void run() {
        Thread.currentThread().setName("Reddit");
        isRunning = true;

        try {
            System.out.println("[Reddit] Sleeping for 1 minutes. Waiting for bot to start up.");
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (isRunning) {
            File reddit = new File(System.getProperty("user.dir") + "/Reddit/" + "Reddit.json");

            try {

                if (!reddit.exists()) {
                    reddit.getParentFile().mkdir();
                    reddit.createNewFile();
                    String test = "{\"Reddit\":[\"#TestPackPleaseIgnore:testpackpleaseignore\"]}";
                    JsonUtils.writeJsonFile(reddit, test);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String users = JsonUtils.getStringFromFile(reddit.toString());
                JsonObject reddits = JsonUtils.getJsonObject(users);
                if (reddits == null) {
                    return;
                }
                String[] test = reddits.get("Reddit").getAsString().replaceAll("[\\[\"\\]]", "").split(",");
                for (String hur : test) {
                    String[] args = hur.split(":");
                    Channel chan = bot.getUserChannelDao().getChannel(args[0]);
                    URL u = new URL("http://www.reddit.com/r/" + args[1] + "/new.json");
                    URLConnection c = u.openConnection();
                    c.setRequestProperty("User-Agent", "AlfredV2 by /u/harry2258"); //Who knew Reddit wanted User Agents like that.
                    String ts;
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    String result;
                    String title = "";
                    String text = "";
                    String URL = "";
                    String author = "";
                    String infotext;
                    String infotitle;
                    long CreateTime = 0;
                    while ((ts = br.readLine()) != null) {
                        JsonObject jsonObj = JsonUtils.getJsonObject(ts);
                        if (jsonObj == null) {
                            continue;
                        }
                        title = jsonObj.getAsJsonObject("data").getAsJsonArray("children").get(0).getAsJsonObject().getAsJsonObject("data").get("title").getAsString().trim();
                        if (!jsonObj.getAsJsonObject("data").getAsJsonArray("children").get(0).getAsJsonObject().getAsJsonObject("data").get("selftext").getAsString().isEmpty()) {
                            text = jsonObj.getAsJsonObject("data").getAsJsonArray("children").get(0).getAsJsonObject().getAsJsonObject("data").get("selftext").getAsString().replaceAll("\\n", " ");
                        }
                        URL = "http://reddit.com" + jsonObj.getAsJsonObject("data").getAsJsonArray("children").get(0).getAsJsonObject().getAsJsonObject("data").get("permalink").getAsString();
                        author = jsonObj.getAsJsonObject("data").getAsJsonArray("children").get(0).getAsJsonObject().getAsJsonObject("data").get("author").getAsString();
                        CreateTime = Long.parseLong(jsonObj.getAsJsonObject("data").getAsJsonArray("children").get(0).getAsJsonObject().getAsJsonObject("data").get("created_utc").toString().replace(".0", ""));
                    }

                    //---------------
                    int maxLengthtitle = (title.length() < 70) ? title.length() : 70;
                    if (title.length() > 69) {
                        infotitle = title.substring(0, maxLengthtitle) + "... |";
                    } else {
                        infotitle = title + " |";
                    }

                    int maxLengthtext = (text.length() < 220) ? text.length() : 220;

                    if (text.length() > 219) {
                        infotext = (text.substring(0, maxLengthtext) + "... |").trim();
                    } else {
                        infotext = (text + " |").trim();
                    }
                    //---------------

                    result = "[" + Colors.RED + "Reddit" + Colors.NORMAL + "] " + " [ " + URL + " ] " + Colors.BOLD + author + Colors.NORMAL + ": " + infotitle + " " + infotext;

                    if (chaninfo.containsKey(hur)) {

                        if (chaninfo.get(hur).isEmpty()) {
                            chaninfo.put(hur, "Nothing!!");
                        }

                        if (!chaninfo.get(hur).equalsIgnoreCase(result)) {
                            chan.send().message("[" + Colors.RED + "Reddit" + Colors.NORMAL + "] " + " [ " + Utils.shortenUrl(URL) + " ] [" + Utils.getTimeDifference(CreateTime) + " ago] " + Colors.BOLD + author + Colors.NORMAL + ": " + infotitle + " " + infotext);
                        }

                    } else {
                        chan.send().message("[" + Colors.RED + "Reddit" + Colors.NORMAL + "] " + " [ " + Utils.shortenUrl(URL) + " ] [" + Utils.getTimeDifference(CreateTime) + " ago ] " + Colors.BOLD + author + Colors.NORMAL + ": " + infotitle + " " + infotext);
                    }
                    chaninfo.put(hur, result);
                }
                Thread.sleep(60000);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                System.out.println(Thread.currentThread().getName() + " ->> " + e.toString());
                bot.getUserChannelDao().getUser("batman").send().message("[Reddit] " + e.toString());
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
