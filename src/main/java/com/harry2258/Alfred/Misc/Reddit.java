package com.harry2258.Alfred.Misc;

import com.harry2258.Alfred.api.JsonUtils;
import com.harry2258.Alfred.api.Utils;
import org.json.JSONObject;
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
                JSONObject reddits = new JSONObject(users);
                String[] test = reddits.getString("Reddit").replaceAll("[\\[\"\\]]", "").split(",");
                for (int i = 0; i < test.length; i++) {
                    String hur = test[i];
                    String[] args = hur.split(":");
                    Channel chan = bot.getUserChannelDao().getChannel(args[0]);
                    URL url = new URL("http://www.reddit.com/r/" + args[1] + "/new.json");
                    String ts;
                    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                    String result = "";
                    String title = "";
                    String text = "";
                    String URL = "";
                    String author = "";
                    String infotext = "";
                    String infotitle = "";
                    long CreateTime = 0;
                    while ((ts = br.readLine()) != null) {
                        JSONObject jsonObj = new JSONObject(ts);
                        title = jsonObj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("title").trim();
                        if (!jsonObj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("selftext").isEmpty()) {
                            text = jsonObj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("selftext").replaceAll("\\n", " ");
                        }
                        URL = "http://reddit.com" + jsonObj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("permalink");
                        author = jsonObj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("author");
                        CreateTime = jsonObj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getLong("created_utc");
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
