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
        if (args.length == 1) {
            event.getChannel().send().message("Official FTB Wiki: http://wiki.feed-the-beast.com");
            return true;
        }
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }

        String test = sb.toString().trim();

        if (args[1].equalsIgnoreCase("bc")) {
            test = "Buildcraft";
        }
        String message = test.replaceAll("User talk", "User_talk").replaceAll(" ", "%20").replace("'", "%E2%80%99");


        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }

        String name;
        String info;
        String xy;
        String searchJson;
        String id = "0";

        boolean exist = true;
        try {
            URL z = new URL("http://wiki.feed-the-beast.com/" + message);
            URLConnection y = z.openConnection();
            BufferedReader first = new BufferedReader(new InputStreamReader(y.getInputStream()));
            String tmp;
            while ((tmp = first.readLine()) != null) {
                if (tmp.contains("There is currently no text in this page")) {
                    exist = false;
                }
            }
            System.out.println(exist);
            if (exist) {
                String temp = ("http://wiki.feed-the-beast.com/api.php?format=xml&action=query&titles=" + message + "&prop=revisions&rvprop=content&format=json").replaceAll(" ", "%20");
                URL u = new URL(temp);
                URLConnection c = u.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                xy = in.readLine();

                if (xy.contains("#REDIRECT")) {
                    String redirect = xy.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|.*\\[\\[|\\]\\].*|^\\s+|\\s+$|<[^>]+>", "");
                    name = redirect;
                    String newtemp = ("http://wiki.feed-the-beast.com/api.php?format=xml&action=query&titles=" + redirect + "&prop=revisions&rvprop=content&format=json").replaceAll(" ", "%20");
                    URL url = new URL(newtemp);
                    URLConnection x = url.openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(x.getInputStream()));
                    xy = br.readLine();
                }

                String json1 = xy.replaceAll("\n", " ");
                id = json1.replaceAll("[{\"/>}{\\\\']", "").replaceAll(".*(?:pages:)|(?::pageid.*)", "");
                JsonObject jsonObj = JsonUtils.getJsonObject(json1);

                if (jsonObj.getAsJsonObject("query").get("pages").toString().contains("Vanilla|type=")) {
                    event.getChannel().send().message(test + " (Vanilla):" + ("http://minecraft.gamepedia.com/" + test).replaceAll(" ", "_"));
                    return true;
                }

                String APItest = jsonObj.getAsJsonObject("query").getAsJsonObject("pages").getAsJsonObject(id).getAsJsonArray("revisions").get(0).getAsJsonObject().get("*").toString();
                String df = APItest.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|\\[\\[|\\]\\]|^\\s+|\\s+$|<[^>]+>|\\\\n", "").trim().replaceAll("\\r?\\n.*", "").replaceAll("\\S+\\|(\\S+)", "$1");
                String fd;
                fd = df.replaceAll("'''", Colors.BOLD).replaceAll("''", Colors.UNDERLINE);
                int maxLength = (fd.length() < 220) ? fd.length() : 220;
                info = fd.substring(0, maxLength);
                String x = ("http://wiki.feed-the-beast.com/" + message).replaceAll(" ", "_");
                String URL;
                if (x.length() > 50) {
                    URL = Utils.shortenUrl(x);
                } else {
                    URL = x;
                }

                event.getChannel().send().message(info + "... [ " + URL + " ]");
                return true;
            }
        } catch (Exception p) {
            p.printStackTrace();
        }

        //---------------------------------------------------------------------------

        try {
            URL read;
            read = new URL("http://wiki.feed-the-beast.com/api.php?format=json&action=query&list=search&srsearch=" + message + "&srwhat=title");
            BufferedReader xx = new BufferedReader(new InputStreamReader(read.openStream()));
            String search = xx.readLine();
            JsonObject json = JsonUtils.getJsonObject(search);
            searchJson = json.getAsJsonObject("query").getAsJsonArray("search").get(0).getAsJsonObject().get("title").toString();
            if (searchJson.contains("Getting Started")) {
                searchJson = json.getAsJsonObject("query").getAsJsonArray("search").get(1).getAsJsonObject().get("title").toString();
            }
            if (json.getAsJsonObject("query").getAsJsonArray("search").get(0).getAsJsonObject().get("snippet").toString().contains("#REDIRECT")) {
                name = (json.getAsJsonObject("query").getAsJsonArray("search").get(0).getAsJsonObject().get("snippet").toString()).replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|\\[\\[|\\]\\]|^\\s+|\\s+$|<[^>]+>", "").trim().replaceAll("\\r?\\n.*", "").replaceAll("\\S+\\|(\\S+)", "$1").replaceAll("#REDIRECT ", "").replace("/ko", "").replaceAll("/ru", "").replaceAll("/fr", "").replaceAll("/zh", "").replaceAll("/pl", "");
            } else {
                name = (searchJson).replace("/ko", "").replaceAll("/ru", "").replaceAll("/fr", "").replaceAll("/zh", "").replaceAll("/pl", "");
            }
            String temp = ("http://wiki.feed-the-beast.com/api.php?format=xml&action=query&titles=" + name + "&prop=revisions&rvprop=content&format=json").replaceAll(" ", "%20");
            URL u = new URL(temp);
            URLConnection c = u.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            xy = in.readLine();

            //\{\{[^}]+\}\}|\[\[Category:[^\]]+\]\]|.*\[\[|\]\].*|^\s+|\s+$|<[^>]+>

            if (xy.contains("#REDIRECT")) {
                String redirect = xy.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|.*\\[\\[|\\]\\].*|^\\s+|\\s+$|<[^>]+>", "");
                name = redirect;
                String newtemp = ("http://wiki.feed-the-beast.com/api.php?format=xml&action=query&titles=" + redirect + "&prop=revisions&rvprop=content&format=json").replaceAll(" ", "%20");
                URL url = new URL(newtemp);
                URLConnection x = url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(x.getInputStream()));
                xy = br.readLine();
            }

            String json1 = xy.replaceAll("\n", " ");
            id = json1.replaceAll("[{\"/>}{\\\\']", "").replaceAll(".*(?:pages:)|(?::pageid.*)", "");
            JsonObject jsonObj = JsonUtils.getJsonObject(json1);

            if (jsonObj.getAsJsonObject("query").get("pages").toString().contains("Vanilla|type=")) {
                event.getChannel().send().message(name + " (Vanilla):" + ("http://minecraft.gamepedia.com/" + name).replaceAll(" ", "_"));
                return true;
            }

            String APItest = jsonObj.getAsJsonObject("query").getAsJsonObject("pages").getAsJsonObject(id).getAsJsonArray("revisions").get(0).getAsJsonObject().get("*").toString();
            String df = APItest.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|\\[\\[|\\]\\]|^\\s+|\\s+$|<[^>]+>|\\\\n", "").trim().replaceAll("\\r?\\n.*", "").replaceAll("\\S+\\|(\\S+)", "$1");
            String tempname = jsonObj.getAsJsonObject("query").getAsJsonObject("pages").getAsJsonObject(id).get("title").toString();
            String fd;
            fd = df.replaceAll("'''", Colors.BOLD).replaceAll("''", Colors.UNDERLINE);
            int maxLength = (fd.length() < 220) ? fd.length() : 220;
            info = fd.substring(0, maxLength);
            String x = ("http://wiki.feed-the-beast.com/" + name).replaceAll(" ", "_");
            String URL;
            if (x.length() > 50) {
                URL = Utils.shortenUrl(x);
            } else {
                URL = x;
            }

            event.getChannel().send().message(info + "... [ " + URL + " ]");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            URL read;
            read = new URL("http://ftbwiki.org/api.php?format=json&action=query&list=search&srsearch=" + message + "&srwhat=title");
            BufferedReader xx = new BufferedReader(new InputStreamReader(read.openStream()));
            String search = xx.readLine();
            JsonObject json = JsonUtils.getJsonObject(search);
            searchJson = json.getAsJsonObject("query").getAsJsonArray("search").get(0).getAsJsonObject().get("title").toString();
            if (searchJson.contains("Getting Started")) {
                searchJson = json.getAsJsonObject("query").getAsJsonArray("search").get(1).getAsJsonObject().get("title").toString();
            }
            if (json.getAsJsonObject("query").getAsJsonArray("search").get(0).getAsJsonObject().get("snippet").toString().contains("#REDIRECT")) {
                name = (json.getAsJsonObject("query").getAsJsonArray("search").get(0).getAsJsonObject().get("snippet").toString()).replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|\\[\\[|\\]\\]|^\\s+|\\s+$|<[^>]+>", "").trim().replaceAll("\\r?\\n.*", "").replaceAll("\\S+\\|(\\S+)", "$1").replaceAll("#REDIRECT ", "").replace("/ko", "").replaceAll("/ru", "").replaceAll("/fr", "").replaceAll("/zh", "").replaceAll("/pl", "");
            } else {
                name = (searchJson).replace("/ko", "").replaceAll("/ru", "").replaceAll("/fr", "").replaceAll("/zh", "").replaceAll("/pl", "");
            }
            String temp = ("http://ftbwiki.org/api.php?format=xml&action=query&titles=" + name + "&prop=revisions&rvprop=content&format=json").replaceAll(" ", "%20");
            URL u = new URL(temp);
            URLConnection c = u.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            xy = in.readLine();

            //\{\{[^}]+\}\}|\[\[Category:[^\]]+\]\]|.*\[\[|\]\].*|^\s+|\s+$|<[^>]+>

            if (xy.contains("#REDIRECT")) {
                String redirect = xy.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|.*\\[\\[|\\]\\].*|^\\s+|\\s+$|<[^>]+>", "");
                name = redirect;
                String newtemp = ("http://ftbwiki.org//api.php?format=xml&action=query&titles=" + redirect + "&prop=revisions&rvprop=content&format=json").replaceAll(" ", "%20");
                URL url = new URL(newtemp);
                URLConnection x = url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(x.getInputStream()));
                xy = br.readLine();
            }

            String json1 = xy.replaceAll("\n", " ");
            id = json1.replaceAll("[{\"/>}{\\\\']", "").replaceAll(".*(?:pages:)|(?::pageid.*)", "");
            JsonObject jsonObj = JsonUtils.getJsonObject(json1);

            if (jsonObj.getAsJsonObject("query").get("pages").toString().contains("Vanilla|type=")) {
                event.getChannel().send().message(name + " (Vanilla):" + ("http://minecraft.gamepedia.com/" + name).replaceAll(" ", "_"));
                return true;
            }

            String APItest = jsonObj.getAsJsonObject("query").getAsJsonObject("pages").getAsJsonObject(id).getAsJsonArray("revisions").get(0).getAsJsonObject().get("*").toString();
            String df = APItest.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|\\[\\[|\\]\\]|^\\s+|\\s+$|<[^>]+>|\\\\n", "").trim().replaceAll("\\r?\\n.*", "").replaceAll("\\S+\\|(\\S+)", "$1");
            String fd;
            fd = df.replaceAll("'''", Colors.BOLD).replaceAll("''", Colors.UNDERLINE);
            int maxLength = (fd.length() < 220) ? fd.length() : 220;
            info = fd.substring(0, maxLength);
        } catch (Exception x) {
            event.getChannel().send().message("http://youtu.be/gvdf5n-zI14  |  Please check your spelling!  | The item/block you are looking could not be found on official FTB and ftbwiki.org");
            return true;
        }

        String x = ("http://ftbwiki.org/" + name).replaceAll(" ", "_");
        String URL;
        if (x.length() > 50) {
            URL = Utils.shortenUrl(x);
        } else {
            URL = x;
        }

        event.getChannel().send().message(info + "... [ " + URL + " ]");

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
