package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;
import org.json.JSONObject;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Hardik on 1/17/14.
 */
public class Wiki extends Command {

    private Config config;
    private PermissionManager manager;

    public Wiki() {
        super("Wiki", "Wiki FTB stuff", "Wiki [Query]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        StringBuilder sb = new StringBuilder();
        String[] args = event.getMessage().split(" ");

        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }

        String test = sb.toString().trim();
        String message = test.replaceAll(" ", "%20");


        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }

        String name = null;
        String finalurl = null;
        String tmp = null;
        String info = null;

        try {

            URL read;
            read = new URL("http://wiki.feed-the-beast.com/api.php?format=json&action=query&list=search&srsearch=" + message + "&srwhat=title");
            BufferedReader xx = new BufferedReader(new InputStreamReader(read.openStream()));
            String search = xx.readLine();
            JSONObject json = new JSONObject(search);
            String searchJson = json.getJSONObject("query").getJSONArray("search").getJSONObject(0).getString("title");

            name = (searchJson).replace("/ko", "");


            String temp = ("http://wiki.feed-the-beast.com/api.php?action=wikilinkquery&query=" + name + "&format=json").replaceAll(" ", "%20");
            URL u = new URL(temp);
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17");
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            String xy = in.readLine();
            String json1 = xy.replaceAll("\n", " ");
            JSONObject jsonObj = new JSONObject(json1);

            if (jsonObj.getJSONObject("pages").getString(name).contains("Vanilla|type=")) {
                event.getChannel().send().message(name + " (Vanilla):" + ("http://minecraft.gamepedia.com/" + name).replaceAll(" ", "_"));
                return true;
            }

            String APItest = jsonObj.getJSONObject("pages").getString(name);
            String df = APItest.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|\\[\\[|\\]\\]|^\\s+|\\s+$|<[^>]+>", "").trim().replaceAll("\\r?\\n.*", "").replaceAll("\\S+\\|(\\S+)", "$1");
            String fd = df.replaceAll("'", "").trim();
            int maxLength = (fd.length() < 220) ? fd.length() : 220;

            info = fd.substring(0, maxLength);

        } catch (Exception e) {
            System.out.println(e);
            event.getChannel().send().message("http://youtu.be/gvdf5n-zI14  |  Please check your spelling!  | Since the Wiki is being updated, That page might not be added yet.");
            return true;
        }

        String x = ("http://wiki.feed-the-beast.com/" + name).replaceAll(" ", "_");
        event.getChannel().send().message(info + "... [ " + Utils.shortenUrl(x) + " ]");

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
