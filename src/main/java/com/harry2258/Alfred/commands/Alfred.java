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

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Hardik at 2:54 AM on 2/25/2015.
 */

public class Alfred extends Command {
    public static ArrayList<Object> answers = new ArrayList<>();

    private Config config;
    private PermissionManager manager;

    public Alfred() {
        super("Alfred", "Alfred be ballin'! (8-Ball.. ._. I'll show myself out)");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        if (event.getMessage().split(" ").length < 2) {
            event.getUser().send().notice("Try asking me something next time.");
            return true;
        }
        try {
            if (answers.isEmpty()) {
                Boolean Json = false;
                Document doc = Jsoup.connect("https://www.dropbox.com/s/dfwe23lx1ogttw7/8ball.json?raw=1").get();
                JsonObject answer = JsonUtils.getJsonObject(doc.text());
                ArrayList numbers = (ArrayList) CreeperHost.getKeysFromJson(doc.text());
                for (Object number : numbers) {
                    answers.add(answer.get(number.toString()).toString());
                }
            }
            Random Randomizer = new Random();
            event.getChannel().send().message(answers.get(Randomizer.nextInt(answers.size())).toString().replace("\"", ""));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
