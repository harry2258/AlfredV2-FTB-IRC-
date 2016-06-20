package com.harry2258.Alfred.commands;

import com.google.gson.JsonObject;
import com.harry2258.Alfred.Misc.CreeperHost;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.JsonUtils;
import com.harry2258.Alfred.api.PermissionManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Hardik at 2:54 AM on 2/25/2015.
 */

public class Alfred extends Command {
    private static ArrayList<Object> answers = new ArrayList<>();

    public Alfred() {
        super("Alfred", "Alfred be ballin'! (8-Ball.. ._. I'll show myself out)");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        if (event.getMessage().split(" ").length < 2) {
            event.getUser().send().notice("Try asking me something next time.");
            return true;
        }

        String[] args = event.getMessage().split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }

        try {
            {
                URL url = new URL("https://8ball.delegator.com/magic/JSON/" + URLEncoder.encode(builder.toString().trim(), "UTF-8"));
                URLConnection conn = url.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String inputLine;
                String json = "";
                while ((inputLine = in.readLine()) != null)
                    json += inputLine.trim();
                in.close();

                 event.getChannel().send().message(JsonUtils.getJsonObject(json).getAsJsonObject("magic").get("answer").getAsString());
                return true;
            }
        } catch (MalformedURLException | NullPointerException m) {
            m.printStackTrace();
        }
        return false;
    }

    @Override
    public void setConfig(Config config) {
        Config config1 = config;
    }

    @Override
    public void setManager(PermissionManager manager) {
        PermissionManager manager1 = manager;
    }
}
