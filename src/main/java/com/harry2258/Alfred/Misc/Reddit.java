package com.harry2258.Alfred.Misc;

import com.google.gson.JsonObject;
import com.harry2258.Alfred.api.JsonUtils;
import com.harry2258.Alfred.api.Utils;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Hardik at 11:26 PM on 2/1/14.
 */
public class Reddit extends Thread {
    PircBotX bot;
    public static HashMap<String, String> chaninfo = new HashMap<>();

    public Reddit(PircBotX bot) {
        this.bot = bot;
    }

    private static volatile boolean isRunning = true;

    public void run() {

        try {
            System.out.println("[Reddit] Sleeping for 1 minutes. Waiting for bot to start up.");
            Thread.sleep(60000);
        } catch (Exception e) {
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

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String users = JsonUtils.getStringFromFile(reddit.toString());
                JsonObject reddits = JsonUtils.getJsonObject(users);
                String[] test = reddits.get("Reddit").getAsString().replaceAll("[\\[\"\\]]", "").split(",");
                for (String hur : test) {
                    String[] args = hur.split(":");
                    Channel chan = bot.getUserChannelDao().getChannel(args[0]);
                    URL url = new URL("http://www.reddit.com/r/" + args[1] + "/new.json");
                    String ts;
                    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
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
                        title = jsonObj.getAsJsonObject("data").getAsJsonArray("children").get(0).getAsJsonObject().getAsJsonObject("data").get("title").getAsString().trim();
                        if (!jsonObj.getAsJsonObject("data").getAsJsonArray("children").get(0).getAsJsonObject().getAsJsonObject("data").get("selftext").getAsString().isEmpty()) {
                            text = jsonObj.getAsJsonObject("data").getAsJsonArray("children").get(0).getAsJsonObject().getAsJsonObject("data").get("selftext").getAsString().replaceAll("\\n", " ");
                        }
                        URL = "http://reddit.com" + jsonObj.getAsJsonObject("data").getAsJsonArray("children").get(0).getAsJsonObject().getAsJsonObject("data").get("permalink").getAsString();
                        author = jsonObj.getAsJsonObject("data").getAsJsonArray("children").get(0).getAsJsonObject().getAsJsonObject("data").get("author").getAsString();
                        CreateTime = jsonObj.getAsJsonObject("data").getAsJsonArray("children").get(0).getAsJsonObject().getAsJsonObject("data").get("created_utc").getAsLong();
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

                    try {
                        if (chaninfo.containsKey(hur)) {

                            if (chaninfo.get(hur).isEmpty()) {
                                chaninfo.put(hur, "Nothing!!");
                            }

                            if (!chaninfo.get(hur).equalsIgnoreCase(result)) {

                                System.out.println("[Reddit] :O Found a new post! sending to " + chan.getName());
                                System.out.println(chaninfo.get(hur));
                                System.out.println(result);
                                chan.send().message("[" + Colors.RED + "Reddit" + Colors.NORMAL + "] " + " [ " + Utils.shortenUrl(URL) + " ] [" + Utils.getTime(CreateTime) + " ago] " + Colors.BOLD + author + Colors.NORMAL + ": " + infotitle + " " + infotext);
                            } else {
                                System.out.println("[Reddit] No new post found.");
                            }

                        } else {
                            chan.send().message("[" + Colors.RED + "Reddit" + Colors.NORMAL + "] " + " [ " + Utils.shortenUrl(URL) + " ] [" + Utils.getTime(CreateTime) + " ago ] " + Colors.BOLD + author + Colors.NORMAL + ": " + infotitle + " " + infotext);
                        }

                    } catch (Exception ex) {
                        try {
                            System.out.println("Got error 502! sleeping for 5 mins");
                            Thread.sleep(300000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                    chaninfo.put(hur, result);
                }
                Thread.sleep(60000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void kill() {
        isRunning = false;
    }

}
