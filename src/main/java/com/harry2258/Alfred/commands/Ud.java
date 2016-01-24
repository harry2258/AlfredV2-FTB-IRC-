package com.harry2258.Alfred.commands;

import com.google.gson.JsonObject;
import com.harry2258.Alfred.api.*;
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
    private Config config;
    private PermissionManager manager;

    public Ud() {
        super("Ud", "Don't know what the word means in the language of the Internet? Urban Dictionary it!", "UD [word]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        StringBuilder sb = new StringBuilder();
        String[] args = event.getMessage().split(" ");
        int id = 0;
        if (event.getMessage().contains(", ")) {
            for (int i = 1; i < args.length - 1; i++) {
                sb.append(args[i].replaceAll(",", "")).append(" ");
            }
            id = Integer.parseInt(event.getMessage().replaceAll(".*(?:, )", "")) - 1;
        } else {
            for (int i = 1; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
        }

        String Word = sb.toString().trim();
        String link = "http://api.urbandictionary.com/v0/define?term=" + Word.replaceAll(" ", "%20");
        String result;
        try {
            URL Urban = new URL(link);
            URLConnection u = Urban.openConnection();
            BufferedReader first = new BufferedReader(new InputStreamReader(u.getInputStream()));
            result = first.readLine();
            String json1 = result.replaceAll("\n", " ");
            JsonObject jsonObj = JsonUtils.getJsonObject(json1);
            if (jsonObj.get("result_type").getAsString().equals("no_results")) {
                MessageUtils.sendChannel(event, "Could not find \"" + Word + "\" on Urban Dictionary :(");
                return true;
            }
            String definition;
            String example;
            String permalink;
            try {
                definition = jsonObj.getAsJsonArray("list").get(id).getAsJsonObject().get("definition").getAsString().replaceAll("\\n|\\r|\\t", " ").replaceAll("  ", " ");
                example = jsonObj.getAsJsonArray("list").get(id).getAsJsonObject().get("example").getAsString().replaceAll("\\n|\\r|\\t", " ").replaceAll("  ", " ");
                permalink = jsonObj.getAsJsonArray("list").get(id).getAsJsonObject().get("permalink").getAsString();
            } catch (Exception x) {
                MessageUtils.sendChannel(event, "Could not get #" + id + " definition for the word '" + Word + "'");
                return true;
            }
            String info = definition;
            String Example = example;
            if (definition.length() > 200) {
                int maxLengthDef = (definition.length() < 220) ? definition.length() : 220;
                info = definition.substring(0, maxLengthDef) + "...";
            }

            if (example.length() > 200) {
                int maxLengthEx = (example.length() < 200) ? example.length() : 200;
                Example = example.substring(0, maxLengthEx) + "...";
            }

            MessageUtils.sendChannel(event, Colors.BOLD + "Def: " + Colors.NORMAL + info + " | " + Colors.BOLD + "Ex: " + Colors.NORMAL + Example + " [ " + permalink + " ]");
        } catch (Exception e) {
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
