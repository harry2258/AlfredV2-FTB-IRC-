/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.harry2258.Alfred.listeners.MessageEvent;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.UserLevel;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.WhoisEvent;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17";

    public static boolean isUrl(String s) {
        String url_regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern p = Pattern.compile(url_regex);
        Matcher m = p.matcher(s);
        return m.find();
    }

    public static int getRank(Channel chan, User user) {
        ArrayList<Integer> levels = new ArrayList<>();
        int highest = -1;
        for (UserLevel level : user.getUserLevels(chan)) {
            levels.add(level.ordinal());
        }
        for (int level : levels) {
            if (highest < level) {
                highest = level;
            }
        }
        return highest;
    }

    public static String getTitle(String link) {
        String response = "";
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(link).openConnection();
            conn.addRequestProperty("User-Agent", USER_AGENT);
            String type = conn.getContentType();
            int length = conn.getContentLength() / 1024;
            response = String.format("HTTP %s: %s", conn.getResponseCode(), conn.getResponseMessage());
            String info;
            if (type.contains("text") || type.contains("application")) {
                Document doc = Jsoup.connect(link).userAgent(USER_AGENT).followRedirects(true).get();
                String title = doc.title() == null || doc.title().isEmpty() ? "No title found!" : doc.title();
                info = String.format("%s"
                        //"- Content Type: %s Size: %skb"
                        , title
                        //, type, length
                );
                return info;
            }
            info = String.format("Content Type: %s Size: %skb", type, length);
            return info;

        } catch (IOException ex) {
            if (ex.getMessage().contains("UnknownHostException")) {
                return Colors.RED + "Unknown hostname!";
            }
            return response.isEmpty() ? Colors.RED + "An error occured" : response;
        }
    }

    public static String getYoutubeInfo(String s) throws IOException {
        String info;
        String title = null;
        String likes = null;
        String dislikes = null;
        String user = null;
        String views = null;
        String publishdate = null;
        Document doc = Jsoup.connect(s).userAgent("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17").get();
        for (Element e : doc.select("a")) {
            if (e.attr("class").equalsIgnoreCase("yt-uix-sessionlink yt-user-videos")) {
                user = e.attr("href").split("/user/")[1].split("/")[0];
            }
        }
        for (Element e : doc.select("span")) {
            if (e.attr("class").equalsIgnoreCase("watch-view-count ")) {
                views = e.text();
            }
            if (e.attr("class").equalsIgnoreCase("likes-count")) {
                likes = e.text();
            }
            if (e.attr("class").equalsIgnoreCase("dislikes-count")) {
                dislikes = e.text();
            }
            if (e.attr("class").equalsIgnoreCase("watch-title  yt-uix-expander-head") || e.attr("class").equalsIgnoreCase("watch-title long-title yt-uix-expander-head")) {
                title = e.text();
            }
            if (e.attr("class").equalsIgnoreCase("watch-video-date")) {
                publishdate = e.text();
            }
        }
        info = title + "  [" + Colors.DARK_GREEN + views + Colors.NORMAL + "]  [" + Colors.DARK_GREEN + "+" + likes + Colors.NORMAL + "]  [" + Colors.RED + "-" + dislikes + Colors.NORMAL + "]  [" + Colors.MAGENTA + user + Colors.NORMAL + " - " + Colors.YELLOW + publishdate + Colors.NORMAL + "]";
        //System.out.println(info);
        return info;
    }

    public static String checkMojangServers() {
        String returns = null;
        try {
            URL url;
            url = new URL("https://status.mojang.com/check");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String result;
            while ((result = reader.readLine()) != null) {
                String a = result.replace("red", Colors.RED + "✘" + Colors.NORMAL).replace("green", Colors.DARK_GREEN + "✓" + Colors.NORMAL).replace("[", "").replace("]", "");
                String b = a.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ");
                returns = b.replace("login.minecraft.net", "Login").replace("session.minecraft.net", "Session").replace("account.mojang.com", "Account").replace("auth.mojang.com", "Auth").replace("skins.minecraft.net", "Skins").replace("authserver.mojang.com", "Auth Server").replace("sessionserver.mojang.com", "Session Server").replace("minecraft.net", "Minecraft");
            }
            reader.close();
        } catch (IOException e) {
            if (e.getMessage().contains("503")) {
                returns = "The minecraft status server is temporarily unavailable, please try again later";
            }
            if (e.getMessage().contains("404")) {
                returns = "Uhoh, it would appear as if the status page has been removed or relocated >_>";
            }

        }
        return returns;
    }

    public static String shortenUrl(String longUrl) {
        String shortened = null;
        try {
            URL url;
            url = new URL("http://is.gd/create.php?format=simple&url=" + longUrl);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
            shortened = bufferedreader.readLine();
            bufferedreader.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("502")) {
                try {
                    System.out.println("Got error 502! sleeping for 5 mins");
                    Thread.sleep(300000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return shortened;
    }

    public static String checkServerStatus(InetAddress i, int port) {
        String returns = "Error.";
        try {
            //wow...i never actually used the port argument?
            Socket s = new Socket(i, port);
            DataInputStream SS_BF = new DataInputStream(s.getInputStream());
            DataOutputStream d = new DataOutputStream(s.getOutputStream());
            d.write(new byte[]
                    {
                            (byte) 0xFE, (byte) 0x01
                    });
            SS_BF.readByte();
            short length = SS_BF.readShort();
            StringBuilder sb = new StringBuilder();
            for (int in = 0; in < length; in++) {
                char ch = SS_BF.readChar();
                sb.append(ch);
            }
            String all = sb.toString().trim();
            System.out.println(all);
            String[] args1 = all.split("\u0000");
            if (args1[3].contains("§")) {
                returns = Colors.DARK_GREEN + "MOTD" + ": " + Colors.NORMAL + args1[3].replaceAll("§[a-m]", "").replaceAll("§[1234567890]", "") + "  |" + Colors.DARK_GREEN + "  players" + Colors.NORMAL + " [" + args1[4] + "/" + args1[5] + "]";
            } else {
                returns = Colors.DARK_GREEN + "MOTD" + ": " + Colors.NORMAL + args1[3] + "  |" + Colors.DARK_GREEN + "  players" + Colors.NORMAL + " [" + args1[4] + "/" + args1[5] + "]";
            }
        } catch (UnknownHostException e1) {
            returns = "the host you specified is unknown. check your settings.";
        } catch (IOException e1) {
            returns = "sorry, we couldn't reach this server, make sure that the server is up and has query enabled.";
        }
        return returns;
    }

    public static String google(String s) {
        try {
            String temp = String.format("https://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=%s", URLEncoder.encode(s, "UTF-8"));
            URL u = new URL(temp);
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17");
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            String json = "";
            String tmp;
            while ((tmp = in.readLine()) != null) {
                json += tmp + "\n";
            }
            in.close();
            JsonElement jelement = new JsonParser().parse(json);
            JsonObject output = jelement.getAsJsonObject();
            output = output.getAsJsonObject("responseData").getAsJsonArray("results").get(0).getAsJsonObject();
            String result = String.format("Google: %s | %s | [ %s ]", StringEscapeUtils.unescapeHtml4(output.get("titleNoFormatting").toString().replaceAll("\"", "")), StringEscapeUtils.unescapeHtml4(output.get("content").toString().replaceAll("\\s+", " ").replaceAll("\\<.*?>", "").replaceAll("\"", "")), output.get("url").toString().replaceAll("\"", ""));
            if (result != null) {
                return result;
            } else {
                return "No results found for query " + s;
            }
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }


    public static boolean pingUrl(final String address) {
        try {
            final URL url = new URL("http://" + address);
            final HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(1500);
            final long startTime = System.currentTimeMillis();
            urlConn.connect();
            final long endTime = System.currentTimeMillis();
            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Time (ms) : " + (endTime - startTime));
                System.out.println("Ping to " + address + " was success");
                return true;
            }
        } catch (final MalformedURLException e1) {
            e1.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getAccount(User u, org.pircbotx.hooks.events.MessageEvent event) {
        String user = "";
        event.getBot().sendRaw().rawLineNow("WHOIS " + u.getNick());
        WaitForQueue waitForQueue = new WaitForQueue(event.getBot());
        WhoisEvent test = null;
        try {
            test = waitForQueue.waitFor(WhoisEvent.class);
            waitForQueue.close();
            user = test.getRegisteredAs();
        } catch (InterruptedException ex) {
            Logger.getLogger(MessageEvent.class.getName()).log(Level.SEVERE, null, ex);
            event.getChannel().send().message("Please enter a valid username!");
            user = "";
        }
        return user;
    }

    public static boolean checkAccount(String user) {
        boolean paid = false;
        try {
            URL url = new URL("https://minecraft.net/haspaid.jsp?user=" + user);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str = in.readLine();
            in.close();
            if (str != null) {
                paid = Boolean.valueOf(str);
            }
        } catch (java.io.IOException e1) {
        }
        return paid;
    }

    public static String McBans(String user) {
        String bans = null;
        int i = 0;
        try {
            URL url = new URL("http://api.fishbans.com/bans/" + user);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String json = br.readLine();
            br.close();
            JSONObject jsonObj = new JSONObject(json);
            i = Integer.parseInt(jsonObj.getJSONObject("bans").getJSONObject("service").getJSONObject("mcbans").getString("bans"))
                    + Integer.parseInt(jsonObj.getJSONObject("bans").getJSONObject("service").getJSONObject("mcbouncer").getString("bans"))
                    + Integer.parseInt(jsonObj.getJSONObject("bans").getJSONObject("service").getJSONObject("mcblockit").getString("bans"))
                    + Integer.parseInt(jsonObj.getJSONObject("bans").getJSONObject("service").getJSONObject("minebans").getString("bans"))
                    + Integer.parseInt(jsonObj.getJSONObject("bans").getJSONObject("service").getJSONObject("glizer").getString("bans"));

            bans = Colors.BOLD + user + Colors.NORMAL + " has a total of " + Colors.BOLD + i + Colors.NORMAL + " bans!";
        } catch (Exception x) {
            System.out.println(x);
            bans = "Please make sure you spelled the Minecraft name right! ";
        }
        return bans;
    }

    public static String getInsult() {
        String insult1 = null;
        do {
            try {
                URL insult;
                insult = new URL("http://www.pangloss.com/seidel/Shaker/index.html?");
                BufferedReader br = new BufferedReader(new InputStreamReader(insult.openStream()));
                for (int i = 0; i < 16; ++i)
                    br.readLine();
                String line = br.readLine();
                String y = line.replaceAll("</font>", " ").replace("</form><hr>", "").replaceAll("<br>", " ");
                insult1 = y;
                br.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } while (insult1.isEmpty());

        return insult1;
    }

    /*
    public static String getCompliment() {
        String compliment = null;
        do {
            try {
                URL insult;
                insult = new URL("http://www.madsci.org/cgi-bin/cgiwrap/~lynn/jardin/SCG");
                BufferedReader br = new BufferedReader(new InputStreamReader(insult.openStream()));
                for (int i = 0; i < 51; ++i)
                br.readLine();
                String line = br.readLine();
                compliment = line;
                br.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } while (compliment.isEmpty());
        return compliment;
    }
    */

    public static void Parser(File file) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("MCVersion", true);
            obj.put("JavaVersion", true);
            obj.put("Modded", true);
            obj.put("ServerBrand", true);
            obj.put("ServerType", true);
            obj.put("Description", true);
            obj.put("OSName", true);
            obj.put("Suggestion", true);
            JsonUtils.writeJsonFile(file, obj.toString());
            System.out.println(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void Geveryone(File file) throws JSONException {
        String perms = "{\"Permissions\":[\"command.wiki\", \"command.mcstatus\", \"command.chstatus\"]}";
        JsonUtils.writeJsonFile(file, perms);
    }

    public static void TweetAuth(File file) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("OAuthConsumerKey", "");
            obj.put("OAuthConsumerSecret", "");
            obj.put("OAuthAccessToken", "");
            obj.put("OAuthAccessTokenSecret", "");
            JsonUtils.writeJsonFile(file, obj.toString());
            System.out.println(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void TweetUser(File file) {
        String Auth = "{\"Users\":[\"FTB_Team\", \"TPPIModPack\"]}";
        JsonUtils.writeJsonFile(file, Auth);
    }
}