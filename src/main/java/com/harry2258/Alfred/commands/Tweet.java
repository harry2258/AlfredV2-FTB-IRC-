package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.*;
import org.json.JSONObject;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;
import com.harry2258.Alfred.Misc.*;
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
        JSONObject Auth = new JSONObject(OAuth);

        String users = JsonUtils.getStringFromFile(TweetUsers.toString());
        JSONObject tweetuser = new JSONObject(users);
        if (Auth.getString("OAuthConsumerKey").isEmpty() | Auth.getString("OAuthConsumerSecret").isEmpty() | Auth.getString("OAuthAccessToken").isEmpty() | Auth.getString("OAuthAccessTokenSecret").isEmpty()) {
            event.getChannel().send().message("Holy Crap! This is something wrong with your \"oauth.json\" file in Twitter folder.");
            return false;
        }

        TwitterFactory twitterFactory;
        Twitter twitter;
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

        if (tests.length == 2) {
            try {
                List<Status> statuses;
                statuses = twitter.getUserTimeline(tests[1]);
                event.getChannel().send().message("[" + Colors.RED + "Twitter" + Colors.NORMAL + "] [" + Colors.BOLD + statuses.get(0).getUser().getName() + Colors.NORMAL + "] " + statuses.get(0).getText());
                if (!com.harry2258.Alfred.Misc.Twitter.tweets.containsValue(test)){
                    tweets.put(args[1], test);
                }
                return true;
            } catch (TwitterException te) {
                te.printStackTrace();
                System.out.println("Failed to get timeline: " + te.getMessage());
                return false;
            }
        }
        for (int i = 0; i < args.length; i++) {
            try {
                List<Status> statuses;
                statuses = twitter.getUserTimeline(args[i]);
                test = "[" + Colors.RED + "Twitter" + Colors.NORMAL + "] [" + Colors.BOLD + statuses.get(0).getUser().getName() + Colors.NORMAL + "] " + statuses.get(0).getText();
                System.out.println(test);
                if (!tweets.containsValue(test)) {
                    tweets.put(args[i], test);
                    event.getChannel().send().message(test);
                }
                if (!com.harry2258.Alfred.Misc.Twitter.tweets.containsValue(test)){
                    tweets.put(args[i], test);
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
