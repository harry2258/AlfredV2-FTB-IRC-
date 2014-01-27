package com.harry2258.Alfred.Misc;

import com.harry2258.Alfred.api.JsonUtils;
import com.harry2258.Alfred.api.Utils;
import org.json.JSONObject;
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
public class Twitter extends Thread {
    PircBotX bot;

    public Twitter(PircBotX bot) {
        this.bot = bot;
    }

    public void run() {
        HashMap<String, String> tweets = new HashMap<>();
        try {
            System.out.println("Sleeping for 2 minutes. Waiting for bot to start up.");
            Thread.sleep(120000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Twitter Updates are ENABLED!");
        while (true) {
            try {

                String test = "";

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
                JSONObject Auth = new JSONObject(OAuth);

                String users = JsonUtils.getStringFromFile(TweetUsers.toString());
                JSONObject tweetuser = new JSONObject(users);
                if (Auth.getString("OAuthConsumerKey").isEmpty() | Auth.getString("OAuthConsumerSecret").isEmpty() | Auth.getString("OAuthAccessToken").isEmpty() | Auth.getString("OAuthAccessTokenSecret").isEmpty()) {
                    for (Channel chan : bot.getUserBot().getChannels()) {
                        chan.send().message("Holy Crap! This is something wrong with your \"oauth.json\" file in Twitter folder.");
                    }
                }

                TwitterFactory twitterFactory;
                twitter4j.Twitter twitter;
                ConfigurationBuilder cb = new ConfigurationBuilder();
                cb.setDebugEnabled(true)
                        .setUseSSL(true)
                        .setOAuthConsumerKey(Auth.getString("OAuthConsumerKey"))
                        .setOAuthConsumerSecret(Auth.getString("OAuthConsumerSecret"))
                        .setOAuthAccessToken(Auth.getString("OAuthAccessToken"))
                        .setOAuthAccessTokenSecret(Auth.getString("OAuthAccessTokenSecret"))
                        .setHttpConnectionTimeout(100000);

                twitterFactory = new TwitterFactory(cb.build());
                twitter = twitterFactory.getInstance();

                String[] args = tweetuser.getString("Users").replaceAll("[\\[\"\\]]", "").split(",");

                for (int i = 0; i < args.length; i++) {
                    try {
                        List<Status> statuses;
                        statuses = twitter.getUserTimeline(args[i]);
                        test = "[" + Colors.RED + "Twitter" + Colors.NORMAL + "] [" + Colors.BOLD + statuses.get(0).getUser().getName() + Colors.NORMAL + "] " + statuses.get(0).getText();
                        System.out.println(test);
                        if (!tweets.containsValue(test)) {
                            tweets.put(args[i], test);
                            for (Channel chan : bot.getUserBot().getChannels()) {
                                chan.send().message(test);
                            }
                        }

                    } catch (TwitterException te) {
                        te.printStackTrace();
                        System.out.println("Failed to get timeline: " + te.getMessage());
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
