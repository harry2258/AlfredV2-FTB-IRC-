package com.harry2258.Alfred.commands;

import com.google.gson.JsonObject;
import com.harry2258.Alfred.api.*;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Hardik on 1/17/14.
 */
public class Wiki extends Command {

    private Config config;
    private PermissionManager manager;

    public Wiki() {
        super("Wiki", "Wiki Minecraft stuff!", "Wiki [Query]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        StringBuilder sb = new StringBuilder();
        String[] args = event.getMessage().split(" ");

        String pattern = "(\\{\\{L\\|)(.*?)(\\}\\})";
        String pattern2 = "(\\[\\[#.*?\\|)(.*?)(]])";

        Pattern p = Pattern.compile(pattern);
        Pattern p2 = Pattern.compile(pattern2);
        Matcher m;


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
        String message = URLEncoder.encode(test, "UTF-8").replaceAll("\\+", "_");


        String name;
        String info;
        String xy;
        String searchJson;
        String id;

        boolean exist = true;
        try {
            URL vanilla = new URL("http://minecraft.gamepedia.com/api.php?format=xml&action=query&titles=" + message +"&prop=revisions&rvprop=content&format=json");
            URLConnection c = vanilla.openConnection();
            c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17");
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            xy = in.readLine();

            if (xy.contains("#REDIRECT")) {
                String redirect = xy.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|.*\\[\\[|\\]\\].*|^\\s+|\\s+$|<[^>]+>", "");
                String newtemp = ("http://minecraft.gamepedia.com/api.php?format=xml&action=query&titles=" + redirect + "&prop=revisions&rvprop=content&format=json").replaceAll(" ", "%20");
                URL url = new URL(newtemp);
                URLConnection x = url.openConnection();
                x.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17");
                BufferedReader br = new BufferedReader(new InputStreamReader(x.getInputStream()));
                xy = br.readLine();
            }

            String json1 = xy.replaceAll("\n", " ");
            id = json1.replaceAll("[{\"/>}{\\\\']", "").replaceAll(".*(?:pages:)|(?::pageid.*)", "");
            JsonObject jsonObj = JsonUtils.getJsonObject(json1);

            String APItest = jsonObj.getAsJsonObject("query").getAsJsonObject("pages").getAsJsonObject(id).getAsJsonArray("revisions").get(0).getAsJsonObject().get("*").toString();

            StringBuffer buffer = new StringBuffer();
            StringBuffer buffer2 = new StringBuffer();

            try {
                m = p.matcher(APItest);
                String GroupName;

                while (m.find()) {
                    GroupName = m.group(2);
                    m.appendReplacement(buffer, "&&&" + GroupName + "&&&");
                }
                m.appendTail(buffer);

                m = p2.matcher(buffer.toString());

                while (m.find()) {
                    GroupName = m.group(2);
                    m.appendReplacement(buffer2, "&&&" + GroupName + "&&&");
                }

                m.appendTail(buffer2);
            } catch (Exception buffered) {
                buffered.printStackTrace();
            }

            String working = buffer2.toString();

            String df = working.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|\\[\\[|\\]\\]|^\\s+|\\s+$|<[^>]+>|\\\\n", "").trim()
                    .replaceAll("\\r?\\n.*|(?:==.*?==).*", "")
                    .replaceAll("\\S+\\|(\\S+)", "$1")
                    .replaceAll(".*(?:}})", "").replaceAll("&&&", "");

            String fd = df.replaceAll("'''", Colors.BOLD).replaceAll("''", Colors.UNDERLINE).replaceAll("\"", "").replaceAll("===", Colors.BOLD + " ").replaceAll("==", Colors.BOLD + " ");
            info = fd;
            if (fd.length() > 220) {
                int maxLength = (fd.length() < 220) ? fd.length() : 220;
                info = fd.substring(0, maxLength) + "...";
            }

            String x = ("http://minecraft.gamepedia.com/" + URLEncoder.encode(message.replaceAll(" ", "_"), "UTF-8"));
            String URL;
            if (x.length() > 50) {
                URL = Utils.shortenUrl(x);
            } else {
                URL = x;
            }

            event.getChannel().send().message(info + " [ " + URL + " ]");
            return true;
        } catch (Exception derp) {
            derp.printStackTrace();
        }

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

            if (exist) {
                String temp = ("http://wiki.feed-the-beast.com/api.php?format=xml&action=query&titles=" + message + "&prop=revisions&rvprop=content&format=json").replaceAll(" ", "%20");
                URL u = new URL(temp);
                URLConnection c = u.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                xy = in.readLine();

                if (xy.contains("#REDIRECT")) {
                    String redirect = xy.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|.*\\[\\[|\\]\\].*|^\\s+|\\s+$|<[^>]+>", "");
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

                StringBuffer buffer = new StringBuffer();
                StringBuffer buffer2 = new StringBuffer();

                try {
                    m = p.matcher(APItest);
                    String GroupName;

                    while (m.find()) {
                        GroupName = m.group(2);
                        m.appendReplacement(buffer, "&&&" + GroupName + "&&&");
                    }
                    m.appendTail(buffer);

                    m = p2.matcher(buffer.toString());

                    while (m.find()) {
                        GroupName = m.group(2);
                        m.appendReplacement(buffer2, "&&&" + GroupName + "&&&");
                    }

                    m.appendTail(buffer2);
                } catch (Exception buffered) {
                    buffered.printStackTrace();
                }

                String working = buffer2.toString();

                String df = working.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|\\[\\[|\\]\\]|^\\s+|\\s+$|<[^>]+>|\\\\n", "").trim()
                        .replaceAll("\\r?\\n.*|(?:==.*?==).*", "")
                        .replaceAll("\\S+\\|(\\S+)", "$1")
                        .replaceAll(".*(?:}})", "").replaceAll("&&&", "");

                String fd = df.replaceAll("'''", Colors.BOLD).replaceAll("''", Colors.UNDERLINE).replaceAll("\"", "").replaceAll("===", Colors.BOLD + " ").replaceAll("==", Colors.BOLD + " ");
                info = fd;
                if (fd.length() > 220) {
                int maxLength = (fd.length() < 220) ? fd.length() : 220;
                info = fd.substring(0, maxLength) + "...";
                }
                String x = ("http://wiki.feed-the-beast.com/" + URLEncoder.encode(message.replaceAll(" ", "_"), "UTF-8"));
                String URL;
                if (x.length() > 50) {
                    URL = Utils.shortenUrl(x);
                } else {
                    URL = x;
                }

                event.getChannel().send().message(info + " [ " + URL + " ]");
                return true;
            }
        } catch (Exception x) {
            x.printStackTrace();
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
            JsonObject jsonObj = JsonUtils.getJsonObject(json1);

            if (jsonObj.getAsJsonObject("query").get("pages").toString().contains("Vanilla|type=")) {
                event.getChannel().send().message(name + " (Vanilla):" + ("http://minecraft.gamepedia.com/" + name).replaceAll(" ", "_"));
                return true;
            }

            try {
                name = json.getAsJsonObject("query").getAsJsonArray("search").get(0).getAsJsonObject().get("title").toString().replaceAll("\"", "");
                URL newTitle = new URL("http://wiki.feed-the-beast.com/api.php?format=xml&action=query&titles=" + name + "&prop=revisions&rvprop=content&format=json");
                System.out.println("http://wiki.feed-the-beast.com/api.php?format=xml&action=query&titles=" + name + "&prop=revisions&rvprop=content&format=json");
                URLConnection x = newTitle.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(x.getInputStream()));
                xy = br.readLine();
                jsonObj = JsonUtils.getJsonObject(xy.replaceAll("\n", " "));
                id = (xy.replaceAll("\n", " ")).replaceAll("[{\"/>}{\\\\']", "").replaceAll(".*(?:pages:)|(?::pageid.*)", "");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            String APItest = jsonObj.getAsJsonObject("query").getAsJsonObject("pages").getAsJsonObject(id).getAsJsonArray("revisions").get(0).getAsJsonObject().get("*").toString();

            StringBuffer buffer = new StringBuffer();
            StringBuffer buffer2 = new StringBuffer();

            try {
                m = p.matcher(APItest);
                String GroupName;

                while (m.find()) {
                    GroupName = m.group(2);
                    m.appendReplacement(buffer, "&&&" + GroupName + "&&&");
                }
                m.appendTail(buffer);

                m = p2.matcher(buffer.toString());

                while (m.find()) {
                    GroupName = m.group(2);
                    m.appendReplacement(buffer2, "&&&" + GroupName + "&&&");
                }

                m.appendTail(buffer2);
            } catch (Exception buffered) {
                buffered.printStackTrace();
            }

            String working = buffer2.toString();

            String df = working.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|\\[\\[|\\]\\]|^\\s+|\\s+$|<[^>]+>|\\\\n", "").trim()
                    .replaceAll("\\r?\\n.*|(?:==.*?==).*", "")
                    .replaceAll("\\S+\\|(\\S+)", "$1")
                    .replaceAll(".*(?:}})", "").replaceAll("&&&", "");

            String fd = df.replaceAll("'''", Colors.BOLD).replaceAll("''", Colors.UNDERLINE).replaceAll("\"", "").replaceAll("===", Colors.BOLD + " ").replaceAll("==", Colors.BOLD + " ");
            info = fd;
            if (fd.length() > 220) {
                int maxLength = (fd.length() < 220) ? fd.length() : 220;
                info = fd.substring(0, maxLength) + "...";
            }

            String x = ("http://wiki.feed-the-beast.com/" + URLEncoder.encode(name.replaceAll(" ", "_"), "UTF-8"));
            String URL;
            if (x.length() > 50) {
                URL = Utils.shortenUrl(x);
            } else {
                URL = x;
            }

            event.getChannel().send().message(info + " [ " + URL + " ]");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            URL read;
            read = new URL("http://ftbwiki.org/api.php?format=json&action=query&list=search&srsearch=" + URLEncoder.encode(sb.toString().trim(), "UTF-8") + "&srwhat=title");
            BufferedReader xx = new BufferedReader(new InputStreamReader(read.openStream()));
            String search = xx.readLine();
            System.out.println(search);
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

            String temp = ("http://ftbwiki.org/api.php?format=xml&action=query&titles=" + name + "&prop=revisions&rvprop=content&format=json").replaceAll(" ", "%20").replaceAll("\"","");
            URL u = new URL(temp);
            URLConnection c = u.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            xy = in.readLine();


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

            StringBuffer buffer = new StringBuffer();
            StringBuffer buffer2 = new StringBuffer();

            try {
                m = p.matcher(APItest);
                String GroupName;

                while (m.find()) {
                    GroupName = m.group(2);
                    m.appendReplacement(buffer, "&&&" + GroupName + "&&&");
                }
                m.appendTail(buffer);

                m = p2.matcher(buffer.toString());

                while (m.find()) {
                    GroupName = m.group(2);
                    m.appendReplacement(buffer2, "&&&" + GroupName + "&&&");
                }

                m.appendTail(buffer2);
            } catch (Exception buffered) {
                buffered.printStackTrace();
            }

            String working = buffer2.toString();

            String df = working.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|\\[\\[|\\]\\]|^\\s+|\\s+$|<[^>]+>|\\\\n", "").trim()
                    .replaceAll("\\r?\\n.*|(?:==.*?==).*", "")
                    .replaceAll("\\S+\\|(\\S+)", "$1")
                    .replaceAll(".*(?:}})", "").replaceAll("&&&", "");

            String fd = df.replaceAll("'''", Colors.BOLD).replaceAll("''", Colors.UNDERLINE).replaceAll("\"", "").replaceAll("===", Colors.BOLD + " ").replaceAll("==", Colors.BOLD + " ");
            info = fd;
            if (fd.length() > 220) {
                int maxLength = (fd.length() < 220) ? fd.length() : 220;
                info = fd.substring(0, maxLength) + "...";
            }

        } catch (Exception x) {
            x.printStackTrace();
            event.getChannel().send().message("http://youtu.be/gvdf5n-zI14  |  Please check your spelling!  | The item/block you are looking could not be found on official FTB and ftbwiki.org");
            return true;
        }

        String x = ("http://ftbwiki.org/" + URLEncoder.encode(name.replaceAll(" ", "_"), "UTF-8"));
        String URL;
        if (x.length() > 50) {
            URL = Utils.shortenUrl(x);
        } else {
            URL = x;
        }

        event.getChannel().send().message(info + " [ " + URL + " ]");

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
