package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.HashMap;


public class Test extends Command {
    public static HashMap<String, String> chaninfo = new HashMap<>();
    private Config config;
    private PermissionManager manager;

    public Test() {
        super("Test", "This is a test command", "Test!");
    }


    @Override
    public boolean execute(MessageEvent event) throws Exception {

        event.getChannel().send().message("Test!");
        event.getChannel().send().message(event.getUser().getUserLevels(event.getChannel()).toString());
        event.getChannel().send().message(("Logged in as: " + Utils.getAccount(event.getUser(), event)));
        /*
        ImmutableSortedSet<User> user = event.getChannel().getUsers(); ImmutableList<User> hur = user.asList(); int idx = new java.util.Random().nextInt(hur.size()); event.getChannel().send().message(hur.get(idx).getNick() + " is now a Potato!");

        URL url = new URL("http://www.reddit.com/r/TestPackPleaseIgnore/new.json");
        String ts;
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String result = "";
        String title = "";
        String text = "";
        String URL = "";
        String author = "";
        String infotext = "";
        String infotitle = "";
        String hur = "testpackpleaseignore";
        while ((ts = br.readLine()) != null) {
            JSONObject jsonObj = new JSONObject(ts);
            title = jsonObj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("title").trim();
            if (!jsonObj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("selftext").isEmpty()) {
                text = jsonObj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("selftext").replaceAll("\\n", "");
            }
            URL = jsonObj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("url");
            author = jsonObj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("author");
        }

        int maxLengthtitle = (title.length() < 70) ? title.length() : 70;
        infotitle = title.substring(0, maxLengthtitle) + " |";
        int maxLengthtext = (text.length() < 220) ? text.length() : 220;
        infotext = (text.substring(0, maxLengthtext) + " |").trim();

        result = "[" + Colors.RED + "Reddit" + Colors.NORMAL + "] "  + " [ " + URL + " ] " + Colors.BOLD + author + Colors.NORMAL + ": " + infotitle + " " + infotext;
        System.out.println(chaninfo.containsKey(hur + result));
        System.out.println(hur + result);
        if (!chaninfo.containsKey(hur + result)) {
            System.out.println(":O Found a new post! sending to #TestPackPleaseIgnore");
            event.getChannel().send().message("[" + Colors.RED + "Reddit" + Colors.NORMAL + "] " + " [ " + Utils.shortenUrl(URL) + " ] " + Colors.BOLD + author + Colors.NORMAL + ": " + infotitle + " " + infotext);
            chaninfo.put(hur + result, result);
        } else {
            System.out.println("No new post found!");
        }

        chaninfo.put(hur + result, result);
        */
        return true;
    }

    @Override
    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public void setManager(PermissionManager manager) {
        this.manager = manager;
    }

}
