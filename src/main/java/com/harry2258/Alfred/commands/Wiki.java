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

        String y = "http://wiki.feed-the-beast.com/" + test;
        String x = y.replaceAll(" ", "_");
        String finalurl = null;
        String tmp = null;
        String info = null;
        try {
            URL read;
            read = new URL(x);
            BufferedReader xx = new BufferedReader(new InputStreamReader(read.openStream()));
            while ((tmp = xx.readLine()) != null) {
                if (tmp.contains(" This block is part of vanilla Minecraft. More information can be found at the ")) {
                    String Vanilla = ("http://minecraft.gamepedia.com/" + test).replaceAll(" ", "_");
                    event.getChannel().send().message(test + "(Vanilla): " + Vanilla);
                    return true;
                }
            }
            xx.close();

            String temp = "http://wiki.feed-the-beast.com/api.php?action=wikilinkquery&query=" + message + "&format=json";
            URL u = new URL(temp);
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17");
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            String xy = in.readLine();
            String json = xy.replaceAll("\n", " ");
            JSONObject jsonObj = new JSONObject(json);
            String APItest = jsonObj.getJSONObject("pages").getString(test);
            String df = APItest.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|\\[\\[|\\]\\]|^\\s+|\\s+$|<[^>]+>", "").trim().replaceAll("\\r?\\n.*", "");
            String fd = df.replaceAll("'", "").trim();
            int maxLength = (fd.length() < 150)?fd.length():150;

            info = fd.substring(0, maxLength);

        } catch (Exception e) {
            //event.getChannel().send().message("http://youtu.be/gvdf5n-zI14  |  Please check your spelling!  |  CAPS MATTER e.g POTATO, Potato & PoTaTo are three different things!  | Since the Wiki is being updated, That block might not be added yet.");
            try {
                String sear = ("http://wiki.feed-the-beast.com/index.php?search=" + test).replaceAll(" ", "_");
                URL search;
                search = new URL(sear);
                BufferedReader zz = new BufferedReader(new InputStreamReader(search.openStream()));
                while ((tmp = zz.readLine()) != null) {
                    if (tmp.contains("There were no results matching the query.")) {
                        event.getChannel().send().message("http://youtu.be/gvdf5n-zI14  |  Please check your spelling!  |  CAPS MATTER e.g POTATO, Potato & PoTaTo are three different things!  | Since the Wiki is being updated, That page might not be added yet.");
                        return true;
                    }
                }

                zz.close();
                finalurl = Utils.shortenUrl(sear);
                event.getChannel().send().message("Search result: " + finalurl);
                return true;
            } catch (Exception ex) {
                System.out.println(ex);
            }
            return true;
        }

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
