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
 * Created by Hardik on 2/1/14.
 */
public class Reddit extends Thread {
    //http://www.reddit.com/r/TestPackPleaseIgnore/new.json
    PircBotX bot;
    public static HashMap<String, String> chaninfo = new HashMap<>();

    public Reddit(PircBotX bot) {
        this.bot = bot;
    }

    public void run() {
        File reddit = new File(System.getProperty("user.dir") + "/Reddit/" + "Reddit.json");
        try {
            System.out.println("[Reddit] Sleeping for 2 minutes. Waiting for bot to start up.");
            Thread.sleep(120000);

            if (!reddit.exists()) {
                reddit.getParentFile().mkdir();
                reddit.createNewFile();
                String test = "{\"Reddit\":[\"#TestPackPleaseIgnore:testpackpleaseignore\"]}";
                JsonUtils.writeJsonFile(reddit, test);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                String users = JsonUtils.getStringFromFile(reddit.toString());
                JSONObject reddits = new JSONObject(users);
                String[] test = reddits.getString("Reddit").replaceAll("[\\[\"\\]]", "").split(",");
                for (int i = 0; i < test.length; i++) {
                    String hur = test[i];
                    String[] args = hur.split(":");
                    if (bot.getUserBot().getChannels().contains(args[0])) {
                    Channel chan = bot.getUserChannelDao().getChannel(args[0]);
                    URL url;
                    url = new URL("http://www.reddit.com/r/" + args[1] + "/new.json");
                    String ts;
                    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                    String result = null;
                    String title = null;
                    String text = null;
                    String URL = null;
                    String author = null;
                    String info;
                    while ((ts = br.readLine()) != null) {
                        JSONObject jsonObj = new JSONObject(ts);
                        title = jsonObj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("title");
                        if (!jsonObj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("selftext").isEmpty()) {
                            text = jsonObj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("selftext");
                        }
                        URL = Utils.shortenUrl(jsonObj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("url"));
                        author = jsonObj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("author");
                    }
                    int maxLength = (text.length() < 220) ? text.length() : 220;
                    info = text.substring(0, maxLength) + " ...";
                    result = "[" + Colors.RED + "Reddit" + Colors.NORMAL + "] " + Colors.BOLD + author + Colors.NORMAL + ": " + title + " " + info + " " + URL;
                    if (!chaninfo.containsValue(result) && !chaninfo.containsKey(hur)) {
                        chan.send().message(result);
                        chaninfo.put(hur, result);
                    }
                    }
                }
                Thread.sleep(120000);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
