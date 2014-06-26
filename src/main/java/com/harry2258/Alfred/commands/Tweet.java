package com.harry2258.Alfred.commands;

import com.google.gson.JsonObject;
import com.harry2258.Alfred.api.*;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Hardik on 1/25/14.
 */
public class Tweet extends Command {

    public Tweet() {
        super("Tweet", "Gets the latest tweets from the users!");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        HashMap<String, String> tweets = new HashMap<>();
        String[] tests = event.getMessage().split(" ");
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
        JsonObject Auth = JsonUtils.getJsonObject(OAuth);

        String users = JsonUtils.getStringFromFile(TweetUsers.toString());
        JsonObject tweetuser = JsonUtils.getJsonObject(users);
        if (Auth.get("OAuthConsumerKey").getAsString().isEmpty() | Auth.get("OAuthConsumerSecret").getAsString().isEmpty() | Auth.get("OAuthAccessToken").getAsString().isEmpty() | Auth.get("OAuthAccessTokenSecret").getAsString().isEmpty()) {
            event.getChannel().send().message("Holy Crap! This is something wrong with your \"oauth.json\" file in Twitter folder.");
            return false;
        }

        TwitterFactory twitterFactory;
        Twitter twitter;
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

        if (tests.length >= 2) {
            int post = 0;
            String status = tests[1];
            if (tests.length == 3) {
                post = Integer.valueOf(tests[1]) - 1;
                status = tests[2];
            }
            try {
                List<Status> statuses;
                statuses = twitter.getUserTimeline(status);
                String text = "";
                text += statuses.get(post).getText().replace("\n", " ").replace("\r", " ");
                String address = "http://twitter.com/" + statuses.get(post).getUser().getName() + "/status/" + statuses.get(post).getId();
                event.getChannel().send().message("[" + Colors.RED + "Twitter" + Colors.NORMAL + "] [ " + Utils.shortenUrl(address) + " ] [" + Colors.BOLD + statuses.get(post).getUser().getName() + Colors.NORMAL + "] " + text);
                if (!com.harry2258.Alfred.Misc.Twitter.tweets.containsValue(test)) {
                    tweets.put(args[1], test);
                }
                return true;
            } catch (TwitterException te) {
                te.printStackTrace();
                System.out.println("Failed to get timeline: " + te.getMessage());
                return false;
            }
        }
        for (String arg : args) {
            try {
                List<Status> statuses;
                statuses = twitter.getUserTimeline(arg);
                test = "[" + Colors.RED + "Twitter" + Colors.NORMAL + "] [" + Colors.BOLD + statuses.get(0).getUser().getName() + Colors.NORMAL + "] " + statuses.get(0).getText();
                System.out.println(test);
                if (!tweets.containsValue(test)) {
                    tweets.put(arg, test);
                    event.getChannel().send().message(test);
                }
                if (!com.harry2258.Alfred.Misc.Twitter.tweets.containsValue(test)) {
                    tweets.put(arg, test);
                }

            } catch (TwitterException te) {
                te.printStackTrace();
                System.out.println("Failed to get timeline: " + te.getMessage());
                return false;
            }
        }

        return true;

    }


    @Override
    public void setConfig(Config config) {

    }

    @Override
    public void setManager(PermissionManager manager) {

    }
}
