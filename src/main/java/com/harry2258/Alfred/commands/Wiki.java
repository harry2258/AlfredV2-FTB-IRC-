package com.harry2258.Alfred.commands;
//TODO Fix Regex

import com.google.gson.JsonObject;
import com.harry2258.Alfred.api.*;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Hardik on 1/17/14.
 */
public class Wiki extends Command {

    private Config config;
    private PermissionManager manager;

    private static int ReadTimeout = 10; // in Seconds

    private static String QueryAllPages = "api.php?format=json&action=query&list=search&srsearch=";
    private static String titlesr = "api.php?&action=query&prop=revisions&rvprop=content&format=json&titles=";

    public Wiki() {
        super("Wiki", "Wiki Minecraft stuff!", "Wiki [Query]");
    }

    private static String matcher(String text) {

        String pattern = "(\\{\\{L\\|)(.*?)(\\}\\})";
        String pattern2 = "(\\[\\[#.*?\\|)(.*?)(]])";
        String pattern3 = "(\\{\\{U\\|)(.*?)(\\}\\})";

        Pattern p = Pattern.compile(pattern);
        Pattern p2 = Pattern.compile(pattern2);
        Pattern p3 = Pattern.compile(pattern3);
        Matcher m;

        StringBuffer buffer = new StringBuffer();
        StringBuffer buffer2 = new StringBuffer();
        StringBuffer buffer3 = new StringBuffer();

        try {
            m = p.matcher(text);
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

            m = p3.matcher(buffer2.toString());
            while (m.find()) {
                GroupName = m.group(2);
                m.appendReplacement(buffer3, "!!!" + GroupName + "!!!");
            }
            m.appendTail(buffer3);

        } catch (Exception buffered) {
            buffered.printStackTrace();
        }

        return buffer3.toString();
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        StringBuilder sb = new StringBuilder();
        String[] args = event.getMessage().split(" ");

        if (args.length == 1) {
            MessageUtils.sendChannel(event, "Official FTB Wiki: http://ftb.gamepedia.com/");
            return true;
        }

        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }

        String test = sb.toString().trim();

        if (args[1].equalsIgnoreCase("bc")) {
            test = "Buildcraft";
        }
        String message;
        if (test.contains("User:")) {
            message = test;
        } else {
            message = URLEncoder.encode(test, "UTF-8").replaceAll("\\+", "_");
        }

        String Text;


        if ((Text = MinecraftWiki(message)) != null) {
            MessageUtils.sendChannel(event, Text);
            return true;
        }

        if ((Text = FTBGamepedia(message)) != null) {
            MessageUtils.sendChannel(event, Text);
            return true;
        }

        if ((Text = FTBwiki(message)) != null) {
            MessageUtils.sendChannel(event, Text);
            return true;
        }

        MessageUtils.sendChannel(event, "The item/block was not found on The Minecraft Wiki, The Official FTB, or on ftbwiki.org | Are you sure you spelled it right?");

        return true;
    }

    public static String MinecraftWiki(String message) {
        String Result;


        try {
            URL vanilla = new URL("http://minecraft.gamepedia.com/api.php?&action=query&titles=" + message + "&prop=revisions&rvprop=content&format=json");
            URLConnection c = vanilla.openConnection();
            c.setReadTimeout((int) TimeUnit.SECONDS.toMillis(ReadTimeout));
            c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17");
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            String xy = in.readLine();

            if (xy.contains("#REDIRECT")) {
                String redirect = xy.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|.*\\[\\[|\\]\\].*|^\\s+|\\s+$|<[^>]+>", "");
                String newtemp = ("http://minecraft.gamepedia.com/api.php?&action=query&titles=" + redirect + "&prop=revisions&rvprop=content&format=json").replaceAll(" ", "%20");
                URL url = new URL(newtemp);
                URLConnection x = url.openConnection();
                x.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17");
                BufferedReader br = new BufferedReader(new InputStreamReader(x.getInputStream()));
                xy = br.readLine();
            }

            String json1 = xy.replaceAll("\n", " ");
            //TODO Fix this regex!!
            String id = json1.replaceAll("[{\"/>}{\\\\']", "").replaceAll(".*(?:pages:)|(?::pageid.*)", "");
            JsonObject jsonObj = JsonUtils.getJsonObject(json1);

            String APItest = jsonObj.getAsJsonObject("query").getAsJsonObject("pages").getAsJsonObject(id).getAsJsonArray("revisions").get(0).getAsJsonObject().get("*").toString();


            String working = matcher(APItest);

            String df = working.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|\\[\\[|\\]\\]|^\\s+|\\s+$|<[^>]+>|\\\\n", "").trim()
                    .replaceAll("\\r?\\n.*|(?:==.*?==).*", "")
                    .replaceAll("\\S+\\|(\\S+)", "$1")
                    .replaceAll("(?:\\{\\{.*?\\}\\})", "").replaceAll("&&&", "");

            String fd = df.replaceAll("'''", Colors.BOLD).replaceAll("''", Colors.UNDERLINE).replaceAll("\"", "").replaceAll("===", Colors.BOLD + " ").replaceAll("==", Colors.BOLD + " ");
            String info = fd;
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

            Result = info + " [ " + URL + " ]";

        } catch (Exception derp) {
            //derp.printStackTrace();
            Result = null;
            System.out.println("Could not find " + message + " on Official Minecraft Gamepedia Wiki");
        }

        return Result;
    }

    public static String FTBGamepedia(String message) {
        String Result = null;

        String name;

        String FTBWiki = "http://ftb.gamepedia.com/";

        boolean exist = true;
        boolean timeout = false;

        try {
            URL z = new URL("http://ftb.gamepedia.com/" + message);
            URLConnection y = z.openConnection();;
            y.setReadTimeout((int) TimeUnit.SECONDS.toMillis(ReadTimeout));
            y.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17");
            System.out.println(y.getURL());
            BufferedReader first = new BufferedReader(new InputStreamReader(y.getInputStream()));
            String tmp;

            while ((tmp = first.readLine()) != null) {
                if (tmp.contains("There is currently no text in this page")) {
                    exist = false;
                }
            }

            if (exist) {
                String temp = (FTBWiki + titlesr + message);
                System.out.println(FTBWiki + titlesr + message);
                URL u = new URL(temp);
                URLConnection c = u.openConnection();
                c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17");
                BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                String xy = in.readLine();

                if (xy.contains("#REDIRECT")) {
                    String redirect = xy.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|.*\\[\\[|\\]\\].*|^\\s+|\\s+$|<[^>]+>", "");
                    String newtemp = (FTBWiki + titlesr + redirect).replaceAll(" ", "%20");
                    URL url = new URL(newtemp);
                    URLConnection x = url.openConnection();
                    x.setReadTimeout((int) TimeUnit.SECONDS.toMillis(ReadTimeout));
                    x.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17");
                    BufferedReader br = new BufferedReader(new InputStreamReader(x.getInputStream()));
                    xy = br.readLine();
                }

                String json1 = xy.replaceAll("\n", " ");
                String id = json1.replaceAll("[{\"/>}{\\\\']", "").replaceAll(".*(?:pages:)|(?::pageid.*)", "");
                JsonObject jsonObj = JsonUtils.getJsonObject(json1);

                if (jsonObj.getAsJsonObject("query").get("pages").toString().contains("Vanilla|type=")) {
                    return jsonObj.getAsJsonObject("query").getAsJsonObject("pages").getAsJsonObject(id).get("title").getAsString().replaceAll("\"","") + " [Vanilla] " + ("http://minecraft.gamepedia.com/" + message);
                }

                String APItest = jsonObj.getAsJsonObject("query").getAsJsonObject("pages").getAsJsonObject(id).getAsJsonArray("revisions").get(0).getAsJsonObject().get("*").toString();
                System.out.println(APItest);

                String working = matcher(APItest);
                String df = working.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|\\[\\[|\\]\\]|^\\s+|\\s+$|<[^>]+>|\\\\n", "").trim()
                        .replaceAll("\\r?\\n.*|(?:==.*?==).*", "")
                        .replaceAll("\\S+\\|(\\S+)", "$1")
                        .replaceAll("(?:\\{\\{.*?\\}\\})", "").replaceAll("&&&", "").replaceAll("!!!", Colors.UNDERLINE);

                String fd = df.replaceAll("'''", Colors.BOLD).replaceAll("''", Colors.UNDERLINE).replaceAll("\"", "").replaceAll("===", Colors.BOLD + " ").replaceAll("==", Colors.BOLD + " ");
                String info = fd;
                if (fd.length() > 220) {
                    int maxLength = (fd.length() < 220) ? fd.length() : 220;
                    info = fd.substring(0, maxLength) + "...";
                }

                String x;
                if (message.contains("User:")) {
                    x = "http://ftb.gamepedia.com/" + message;
                } else {
                    x = "http://ftb.gamepedia.com/" + URLEncoder.encode(message.replaceAll(" ", "_"), "UTF-8");
                }
                String URL;
                if (x.length() > 50) {
                    URL = Utils.shortenUrl(x);
                } else {
                    URL = x;
                }

                Result = info + " [ " + URL + " ]";
            }
        } catch (Exception x) {
            x.printStackTrace();
            if (x.getMessage().contains("Read timed out")) {
                //MessageUtils.sendUserNotice(event, "Connection timed-out while connecting to the Official Wiki");
                timeout = true;
            }
            System.out.println("[Try 1] Could not find " + message + " on Official FTB Wiki");
        }

        if (timeout) {
            try {
                URL read;
                read = new URL(FTBWiki + QueryAllPages + message);
                URLConnection c = read.openConnection();
                c.setReadTimeout((int) TimeUnit.SECONDS.toMillis(ReadTimeout));
                c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17");
                BufferedReader xx = new BufferedReader(new InputStreamReader(c.getInputStream()));

                String search = xx.readLine();
                JsonObject json = JsonUtils.getJsonObject(search);
                String searchJson = json.getAsJsonObject("query").getAsJsonArray("search").get(0).getAsJsonObject().get("title").toString();
                if (searchJson.contains("Getting Started")) {
                    searchJson = json.getAsJsonObject("query").getAsJsonArray("search").get(1).getAsJsonObject().get("title").toString();
                }
                if (json.getAsJsonObject("query").getAsJsonArray("search").get(0).getAsJsonObject().get("snippet").toString().contains("#REDIRECT")) {
                    name = (json.getAsJsonObject("query").getAsJsonArray("search").get(0).getAsJsonObject().get("snippet").toString()).replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|\\[\\[|\\]\\]|^\\s+|\\s+$|<[^>]+>", "").trim().replaceAll("\\r?\\n.*", "").replaceAll("\\S+\\|(\\S+)", "$1").replaceAll("#REDIRECT ", "").replace("/ko", "").replaceAll("/ru", "").replaceAll("/fr", "").replaceAll("/zh", "").replaceAll("/pl", "");
                } else {
                    name = (searchJson).replace("/ko", "").replaceAll("/ru", "").replaceAll("/fr", "").replaceAll("/zh", "").replaceAll("/pl", "");
                }
                String temp = (FTBWiki + titlesr + name.replaceAll("\"", "")).replaceAll(" ", "%20");
                URL u = new URL(temp);
                URLConnection d = u.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(d.getInputStream()));
                String xy = in.readLine();

                if (xy.contains("#REDIRECT")) {
                    String redirect = xy.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|.*\\[\\[|\\]\\].*|^\\s+|\\s+$|<[^>]+>", "");
                    String newtemp = (FTBWiki + titlesr + redirect).replaceAll(" ", "%20");
                    URL url = new URL(newtemp);
                    url.openConnection().setReadTimeout((int) TimeUnit.SECONDS.toMillis(ReadTimeout));
                    URLConnection x = url.openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(x.getInputStream()));
                    xy = br.readLine();
                    System.out.println("WIKI REDIRECT -- \n" + br.readLine());
                }

                JsonObject jsonObj;
                String id;
                try {
                    name = json.getAsJsonObject("query").getAsJsonArray("search").get(0).getAsJsonObject().get("title").toString().replaceAll("\"", "");
                    URL newTitle = new URL(FTBWiki + titlesr + name);
                    newTitle.openConnection().setReadTimeout((int) TimeUnit.SECONDS.toMillis(ReadTimeout));
                    URLConnection x = newTitle.openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(x.getInputStream()));
                    xy = br.readLine();
                    jsonObj = JsonUtils.getJsonObject(xy.replaceAll("\n", " "));
                    id = (xy.replaceAll("\n", " ")).replaceAll("[{\"/>}{\\\\']", "").replaceAll(".*(?:pages:)|(?::pageid.*)", "");
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

                String APItest = jsonObj.getAsJsonObject("query").getAsJsonObject("pages").getAsJsonObject(id).getAsJsonArray("revisions").get(0).getAsJsonObject().get("*").toString();

                String working = matcher(APItest);

                String df = working.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|\\[\\[|\\]\\]|^\\s+|\\s+$|<[^>]+>|\\\\n", "").trim()
                        .replaceAll("\\r?\\n.*|(?:==.*?==).*", "")
                        .replaceAll("\\S+\\|(\\S+)", "$1")
                        .replaceAll("(?:\\{\\{.*?\\}\\})", "").replaceAll("&&&", "").replaceAll("!!!", Colors.UNDERLINE);

                String fd = df.replaceAll("'''", Colors.BOLD).replaceAll("''", Colors.UNDERLINE).replaceAll("\"", "").replaceAll("===", Colors.BOLD + " ").replaceAll("==", Colors.BOLD + " ");
                String info = fd;
                if (fd.length() > 220) {
                    int maxLength = (fd.length() < 220) ? fd.length() : 220;
                    info = fd.substring(0, maxLength) + "...";
                }

                String x;
                if (message.contains("User:")) {
                    x = "http://ftb.gamepedia.com/" + name;
                } else {
                    x = "http://ftb.gamepedia.com/" + URLEncoder.encode(name.replaceAll(" ", "_"), "UTF-8");
                }
                String URL;
                if (x.length() > 50) {
                    URL = Utils.shortenUrl(x);
                } else {
                    URL = x;
                }

                Result = info + " [ " + URL + " ]";
            } catch (Exception e) {
                e.printStackTrace();
                //event.respond(e.getLocalizedMessage());
                System.out.println("[Try 2] Could not find " + message + " on Official FTB Wiki");
                return null;
            }
        }

        return Result;
    }

    public static String FTBwiki(String message) throws UnsupportedEncodingException {
        String Result;

        String ftborg = "http://ftbwiki.org/";

        String name;
        String id;
        String info;

        try {
            URL read;
            read = new URL(ftborg + QueryAllPages + message);
            read.openConnection().setReadTimeout((int) TimeUnit.SECONDS.toMillis(ReadTimeout));
            BufferedReader xx = new BufferedReader(new InputStreamReader(read.openStream()));
            String search = xx.readLine();
            JsonObject json = JsonUtils.getJsonObject(search);
            String searchJson = json.getAsJsonObject("query").getAsJsonArray("search").get(0).getAsJsonObject().get("title").toString();

            if (searchJson.contains("Getting Started")) {
                searchJson = json.getAsJsonObject("query").getAsJsonArray("search").get(1).getAsJsonObject().get("title").toString();
            }

            if (json.getAsJsonObject("query").getAsJsonArray("search").get(0).getAsJsonObject().get("snippet").toString().contains("#REDIRECT")) {
                name = json.getAsJsonObject("query").getAsJsonArray("search").get(0).getAsJsonObject().get("snippet").toString()
                        .replaceAll("(.*?\\[\\[)|(<.*?>)|(\\]\\].*)", "").trim()
                        .replaceAll("\\r?\\n.*", "")
                        .replaceAll("\\S+\\|(\\S+)", "$1")
                        .replaceAll("/ko", "")
                        .replaceAll("/ru", "")
                        .replaceAll("/fr", "")
                        .replaceAll("/zh", "")
                        .replaceAll("/pl", "");
            } else {
                name = (searchJson).replace("/ko", "").replaceAll("/ru", "").replaceAll("/fr", "").replaceAll("/zh", "").replaceAll("/pl", "");
            }

            String temp = (ftborg + titlesr + name).replaceAll(" ", "%20").replaceAll("\"", "");
            URL u = new URL(temp);
            u.openConnection().setReadTimeout((int) TimeUnit.SECONDS.toMillis(ReadTimeout));
            URLConnection c = u.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            String xy = in.readLine();

            String json1 = xy.replaceAll("\n", " ");
            id = json1.replaceAll("[{\"/>}{\\\\']", "").replaceAll(".*(?:pages:)|(?::pageid.*)", "");
            JsonObject jsonObj = JsonUtils.getJsonObject(json1);

            if (jsonObj.getAsJsonObject("query").get("pages").toString().contains("Vanilla|type=")) {
                return name + " (Vanilla):" + ("http://minecraft.gamepedia.com/" + name).replaceAll(" ", "_");
            }

            String APItest = jsonObj.getAsJsonObject("query").getAsJsonObject("pages").getAsJsonObject(id).getAsJsonArray("revisions").get(0).getAsJsonObject().get("*").toString();

            String working = matcher(APItest);

            String df = working.replaceAll("\\{\\{[^}]+\\}\\}|\\[\\[Category:[^\\]]+\\]\\]|\\[\\[|\\]\\]|^\\s+|\\s+$|<[^>]+>|\\\\n", "").trim()
                    .replaceAll("\\r?\\n.*|(?:==.*?==).*", "")
                    .replaceAll("\\S+\\|(\\S+)", "$1")
                    .replaceAll(".*(?:}})", "").replaceAll("&&&", "").replaceAll("!!!", Colors.UNDERLINE);
            String fd = df.replaceAll("'''", Colors.BOLD).replaceAll("''", Colors.UNDERLINE).replaceAll("\"", "").replaceAll("===", Colors.BOLD + " ").replaceAll("==", Colors.BOLD + " ");
            info = fd;
            if (fd.length() > 220) {
                int maxLength = (fd.length() < 220) ? fd.length() : 220;
                info = fd.substring(0, maxLength) + "...";
            }

        } catch (Exception x) {
            x.printStackTrace();
            System.out.println("Could not find " + message + " on ftbwiki.org");
            return null;
        }

        String x;
        if (message.contains("User:")) {
            x = "http://ftbwiki.org/" + name;
        } else {
            x = "http://ftbwiki.org/" + URLEncoder.encode(name.replaceAll(" ", "_").replaceAll("\"", ""), "UTF-8");
        }

        String URL;
        if (x.length() > 50) {
            URL = Utils.shortenUrl(x);
        } else {
            URL = x;
        }

        Result = info + " [ " + URL + " ]";

        return Result;
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
