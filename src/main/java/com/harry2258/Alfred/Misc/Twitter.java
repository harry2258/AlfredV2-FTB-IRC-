package com.harry2258.Alfred.Misc;

import com.google.gson.JsonObject;
import com.harry2258.Alfred.api.JsonUtils;
import com.harry2258.Alfred.api.Utils;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Hardik on 1/26/14.
 */
@SuppressWarnings("InfiniteLoopStatement")
public class Twitter extends Thread {
    public static HashMap<String, String> tweets = new HashMap<>();
    private static volatile boolean isRunning = true;
    private PircBotX bot;

    public Twitter(PircBotX bot) {
        this.bot = bot;
    }

    public static void kill() {

        Thread.currentThread().interrupt();
        isRunning = false;
    }

    public void run() {
        Thread.currentThread().setName("Twitter");
        isRunning = true;

        try {
            System.out.println("[Twitter] Sleeping for 1 minutes. Waiting for bot to start up.");
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (isRunning) {
            try {

                String test;

                File TweetUsers = new File(System.getProperty("user.dir") + "/Twitter/" + "tweetuser.json");
                File auth = new File(System.getProperty("user.dir") + "/Twitter/" + "oauth.json");

                if (!TweetUsers.exists()) {
                    TweetUsers.getParentFile().mkdir();
                    TweetUsers.createNewFile();
                    Utils.TweetUser(TweetUsers);
                }

                if (!auth.exists()) {
                    auth.getParentFile().mkdir();
                    auth.createNewFile();
                    Utils.TweetAuth(auth);
                }

                String OAuth = JsonUtils.getStringFromFile(auth.toString());
                JsonObject Auth = JsonUtils.getJsonObject(OAuth);

                String users = JsonUtils.getStringFromFile(TweetUsers.toString());
                JsonObject tweetuser = JsonUtils.getJsonObject(users);
                if (Auth.get("OAuthConsumerKey").getAsString().isEmpty() | Auth.get("OAuthConsumerSecret").getAsString().isEmpty() | Auth.get("OAuthAccessToken").getAsString().isEmpty() | Auth.get("OAuthAccessTokenSecret").getAsString().isEmpty()) {
                    for (Channel chan : bot.getUserBot().getChannels()) {
                        chan.send().message("UH-OH!! This is something wrong with your \"oauth.json\" file in Twitter folder.");
                        break;
                    }
                }

                TwitterFactory twitterFactory;
                twitter4j.Twitter twitter;
                ConfigurationBuilder cb = new ConfigurationBuilder();
                cb.setDebugEnabled(true)
                        //.setUseSSL(true)
                        .setOAuthConsumerKey(Auth.get("OAuthConsumerKey").getAsString())
                        .setOAuthConsumerSecret(Auth.get("OAuthConsumerSecret").getAsString())
                        .setOAuthAccessToken(Auth.get("OAuthAccessToken").getAsString())
                        .setOAuthAccessTokenSecret(Auth.get("OAuthAccessTokenSecret").getAsString())
                        .setHttpConnectionTimeout(100000);

                twitterFactory = new TwitterFactory(cb.build());
                twitter = twitterFactory.getInstance();

                String[] args = tweetuser.get("Users").getAsString().replaceAll("[\\[\"\\]]", "").split(",");

                for (String arg : args) {
                    try {
                        List<Status> statuses;
                        statuses = twitter.getUserTimeline(arg);
                        test = "[" + Colors.RED + "Twitter" + Colors.NORMAL + "] [" + Colors.BOLD + statuses.get(0).getUser().getName() + Colors.NORMAL + "] " + statuses.get(0).getText();
                        if (!tweets.containsValue(test)) {
                            tweets.put(arg, test);
                            for (Channel chan : bot.getUserBot().getChannels()) {
                                if (statuses.get(0).getUser().getName().equals("TPPIModPack") && chan.getName().equals("TestPackPleaseIgnore")) {
                                    chan.send().message(test);
                                }
                            }
                        }

                    } catch (TwitterException te) {
                        te.printStackTrace();
                        System.out.println(Thread.currentThread().getName() + " ->> " + te.toString());
                        System.out.println("Failed to get timeline: " + te.getMessage());
                    }
                }
                Thread.sleep(60000);
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + " ->> " + e.toString());
                e.printStackTrace();
            }
        }
    }
}
