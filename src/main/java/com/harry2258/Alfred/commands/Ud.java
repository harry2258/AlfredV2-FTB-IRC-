package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.json.JSONObject;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Hardik at 10:07 PM on 4/19/14.
 */
public class Ud extends Command {
    public Ud() {
        super("Ud", "Don't know what the word means in the language of the Internet? Urban Dictionary it!", "UD [word]");
    }

    private Config config;
    private PermissionManager manager;

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        StringBuilder sb = new StringBuilder();
        String[] args = event.getMessage().split(" ");

        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }

        String Word = sb.toString().trim();
        String link = "http://api.urbandictionary.com/v0/define?term=" + Word.replaceAll(" ","%20");
        String result = "";
        try {
            URL Urban = new URL(link);
            URLConnection u = Urban.openConnection();
            BufferedReader first = new BufferedReader(new InputStreamReader(u.getInputStream()));
            result = first.readLine();
            String json1 = result.replaceAll("\n", " ");
            JSONObject jsonObj = new JSONObject(json1);
            if (jsonObj.get("result_type").equals("no_results")){
                event.getChannel().send().message("Could not find \"" + Word + "\" on Urban Dictionary :(");
                return true;
            }
            String definition = jsonObj.getJSONArray("list").getJSONObject(0).getString("definition").replaceAll("\\n|\\r|\\t", " ");
            String example = jsonObj.getJSONArray("list").getJSONObject(0).getString("example").replaceAll("\\n|\\r|\\t", " ");
            String permalink = jsonObj.getJSONArray("list").getJSONObject(0).getString("permalink");
            String info = "";
            String Example;
            if (example.length() > 220) {
                int maxLengthDef = (definition.length() < 220) ? definition.length() : 220;
                info = definition.substring(0, maxLengthDef) + "...";
            } else {
                info = definition;
            }
            if (example.length() > 220) {
                int maxLengthEx = (example.length() < 220) ? example.length() : 220;
                Example = example.substring(0, maxLengthEx) + "...";
            } else {
                Example = example;
            }
            event.getChannel().send().message(Colors.BOLD + "Def: " + Colors.NORMAL + info + " | " + Colors.BOLD + "Ex: " + Colors.NORMAL + example + " [ " + permalink + " ]");
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
