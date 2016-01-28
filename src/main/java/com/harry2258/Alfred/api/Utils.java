/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.Misc.CreeperHost;
import com.harry2258.Alfred.json.Perms;
import com.harry2258.Alfred.listeners.MessageEvent;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.UserLevel;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.WhoisEvent;

import java.io.*;
import java.net.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17";

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
            conn.setInstanceFollowRedirects(true);
            String type = conn.getContentType();
            int length = conn.getContentLength() / 1024;
            response = String.format("HTTP %s: %s", conn.getResponseCode(), conn.getResponseMessage());
            String info;
            if (type.contains("text") || type.contains("application")) {
                Document doc = Jsoup.connect(link).userAgent(USER_AGENT).followRedirects(true).get();
                return (doc.title() == null || doc.title().isEmpty() ? "No title found!" : doc.title());
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
            if (e.attr("class").equalsIgnoreCase("g-hovercard yt-uix-sessionlink yt-user-name ")) {
                user = e.text();
            }
        }
        for (Element e : doc.select("div")) {
            if (e.attr("class").equalsIgnoreCase("watch-view-count")) {
                views = e.text().replace(" views", "");
            }
            if (e.attr("class").equalsIgnoreCase("yt-uix-expander yt-uix-button-panel")) {
                System.out.println(e.getElementsContainingOwnText("Uploaded on "));
                //publishdate = e.text();
            }

            if (e.attr("id").equalsIgnoreCase("watch7-headline")) {
                title = e.text();
            }
        }

        for (Element e : doc.select("p")) {
            if (e.attr("id").equalsIgnoreCase("watch-uploader-info")) {
                publishdate = e.text().replace("Uploaded on ", "");
            }
        }

        for (Element e : doc.select("button")) {
            if (e.attr("id").equalsIgnoreCase("watch-like")) {
                likes = e.text();
            }
            if (e.attr("id").equals("watch-dislike")) {
                dislikes = e.text();
            }
        }

        info = title + "  [" + Colors.DARK_GREEN + views + Colors.NORMAL + "]  [" + Colors.DARK_GREEN + "+" + likes + Colors.NORMAL + "]  [" + Colors.RED + "-" + dislikes + Colors.NORMAL + "]  [" + Colors.MAGENTA + user + Colors.NORMAL + " - " + Colors.PURPLE + publishdate + Colors.NORMAL + "]";
        //System.out.println(info);
        return info;
    }

    public static String checkMojangServers() {
        String returns = "";
        try {
            URL url = new URL("https://status.mojang.com/check");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String result;

            while ((result = reader.readLine()) != null) {
                String a = result.replace("red", Colors.RED + "✘" + Colors.NORMAL)
                        .replace("green", Colors.DARK_GREEN + "✓" + Colors.NORMAL)
                        .replace("yellow", Colors.YELLOW + "~" + Colors.NORMAL)
                        .replace("[", "").replace("]", "").replace("{", "")
                        .replace("}", "").replace(":", ": ").replace("\"", "")
                        .replace("session.", "Legacy Session.")
                        .replace("server", " Server");

                String[] c = a.replaceAll("\\.minecraft.net", "")
                        .replaceAll("\\.mojang.com|.net", "")
                        .replaceAll("\\.com", "")
                        .split(",");

                for (String tmp : c) {
                    returns += Character.toUpperCase(tmp.charAt(0)) + tmp.substring(1).toLowerCase() + " | ";
                }
            }

            reader.close();
        } catch (IOException e) {
            if (e.getMessage().contains("503")) {
                System.out.println("The minecraft status server is temporarily unavailable, please try again later");
            }
            try {
                URL xpaw = new URL("http://xpaw.ru/mcstatus/status.json");
                URLConnection u = xpaw.openConnection();
                u.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17");

                BufferedReader first = new BufferedReader(new InputStreamReader(u.getInputStream()));
                String result = first.readLine();
                String json1 = result.replaceAll("\n", " ");

                JsonObject report = JsonUtils.getJsonObject(json1).getAsJsonObject("report");
                returns = ("Session: " + report.getAsJsonObject("session").get("title").getAsString() +
                        " | Login: " + report.getAsJsonObject("login").get("title").getAsString() +
                        " | Legacy Skins: " + report.getAsJsonObject("skins").get("title").getAsString() +
                        " | Website: " + report.getAsJsonObject("website").get("title").getAsString() +
                        " | Realms: " + report.getAsJsonObject("realms").get("title").getAsString()).replaceAll("Online", Colors.DARK_GREEN + "✓" + Colors.NORMAL).replaceAll("Offline", Colors.RED + "✘" + Colors.NORMAL).replaceAll("Server Error", Colors.RED + "Server Error" + Colors.NORMAL);
            } catch (IOException x) {
                x.printStackTrace();
            }

        }
        return returns;
    }

    public static String shortenUrl(String longUrl) {
        try {
            URL isgd = new URL("http://is.gd/create.php?format=json&url=" + longUrl);
            URLConnection u = isgd.openConnection();
            u.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17");
            BufferedReader first = new BufferedReader(new InputStreamReader(u.getInputStream()));
            String result = first.readLine().trim();
            return JsonUtils.getJsonObject(result).get("shorturl").getAsString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String checkServerStatus(InetAddress i, int port) {
        String returns;
        try {
            //wow...i never actually used the port argument?
            Socket s = new Socket(i, port);
            s.setSoTimeout((int) TimeUnit.SECONDS.toMillis(10));
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
            String result = String.format("Google: %s | %s | [ %s ]", StringEscapeUtils.unescapeHtml4(output.get("titleNoFormatting").toString().replaceAll("\"", "")), StringEscapeUtils.unescapeHtml4(output.get("content").toString().replaceAll("\\s+", " ").replaceAll("<.*?>", "").replaceAll("\"", "").replaceAll("\\\\n", "")), Utils.shortenUrl(output.get("url").toString().replaceAll("\"", "")));
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
        String URL;
        if (address.contains("http://")) {
            URL = address;
        } else {
            URL = "http://" + address;
        }
        try {
            final URL url = new URL(URL);
            final HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(3000);
            final long startTime = System.currentTimeMillis();
            urlConn.connect();
            final long endTime = System.currentTimeMillis();
            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Ping to " + address + " was success [Time " + (endTime - startTime) + " MS ]");
                return true;
            }
        } catch (final IOException e1) {
            e1.printStackTrace();
        }
        return false;
    }

    public static String getAccount(User u, org.pircbotx.hooks.events.MessageEvent event) {
        String user = null;
        event.getBot().sendRaw().rawLineNow("WHOIS " + u.getNick());
        WaitForQueue waitForQueue = new WaitForQueue(event.getBot());
        WhoisEvent test;
        try {
            test = waitForQueue.waitFor(WhoisEvent.class);
            waitForQueue.close();
            user = test.getRegisteredAs();
        } catch (InterruptedException ex) {
            Logger.getLogger(MessageEvent.class.getName()).log(Level.SEVERE, null, ex);
            event.getUser().send().notice("Please enter a valid username!");
        }
        waitForQueue.close();

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
            e1.printStackTrace();
        }
        return paid;
    }

    public static String McBans(String user) {
        String bans;
        int i;
        try {
            URL url = new URL("http://api.fishbans.com/bans/" + user);
            JsonObject banservice = JsonUtils.getJsonObject(IOUtils.toString(url)).getAsJsonObject("bans").getAsJsonObject("service");
            i = Integer.parseInt(banservice.getAsJsonObject("mcbans").get("bans").getAsString())
                    + Integer.parseInt(banservice.getAsJsonObject("mcbouncer").get("bans").getAsString())
                    + Integer.parseInt(banservice.getAsJsonObject("mcblockit").get("bans").getAsString())
                    + Integer.parseInt(banservice.getAsJsonObject("minebans").get("bans").getAsString())
                    + Integer.parseInt(banservice.getAsJsonObject("glizer").get("bans").getAsString());
            if (Integer.valueOf(i).equals(0) || Integer.valueOf(i).equals(1)) {
                bans = Colors.BOLD + user + Colors.NORMAL + " has a total of " + Colors.BOLD + i + Colors.NORMAL + " ban!";
            } else {
                bans = Colors.BOLD + user + Colors.NORMAL + " has a total of " + Colors.BOLD + i + Colors.NORMAL + " bans!";
            }
        } catch (IOException x) {
            x.printStackTrace();
            bans = "Please make sure you spelled the Minecraft name right! ";
        }
        return bans;
    }

    public static String getInsult() {
        String insult1 = null;
        try {
            do {
                URL insult;
                insult = new URL("http://www.pangloss.com/seidel/Shaker/index.html?");
                BufferedReader br = new BufferedReader(new InputStreamReader(insult.openStream()));
                for (int i = 0; i < 16; ++i)
                    br.readLine();
                String line = br.readLine();
                insult1 = line.replaceAll("</font>", " ").replace("</form><hr>", "").replaceAll("<br>", " ");
                br.close();
            } while (insult1.isEmpty());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return insult1;
    }


    public static String getCompliment() {
        String compliment = null;
        try {
            Random random = new Random();
            Document doc = Jsoup.connect("http://www.chainofgood.co.uk/passiton").userAgent("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17").get();
            Elements medium = doc.select(".medium");
            compliment = medium.get(random.nextInt(medium.size())).toString().replaceAll("<[^>]*>", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compliment;
    }

    public static String getDrama() {
        String drama = null;
        try {
            Document doc = Jsoup.connect("http://mc-drama.herokuapp.com/raw").get();
            drama = doc.text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return drama;
    }

    public static void Parser(File file) {
        JsonObject obj = new JsonObject();
        obj.addProperty("MCVersion", true);
        obj.addProperty("JavaVersion", true);
        obj.addProperty("Modded", true);
        obj.addProperty("ServerBrand", true);
        obj.addProperty("ServerType", true);
        obj.addProperty("Description", true);
        obj.addProperty("OSName", true);
        obj.addProperty("Suggestion", true);
        obj.addProperty("Information", true);
        obj.addProperty("Stacktrace", true);
        JsonUtils.writeJsonFile(file, obj.toString());
    }

    public static void Geveryone(File file) {
        try {
            BufferedReader s = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/Newglobal.json")));
            String tmp;
            file.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            while ((tmp = s.readLine()) != null) {
                out.write(tmp);
                out.flush();
                out.newLine();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void edges(File file) {
        try {
            BufferedReader s = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/Newedges.json")));
            String tmp;
            file.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            while ((tmp = s.readLine()) != null) {
                out.write(tmp);
                out.flush();
                out.newLine();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void TweetAuth(File file) {
        try {
            BufferedReader s = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("NewTwitter/oauth.json")));
            String tmp;
            file.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            while ((tmp = s.readLine()) != null) {
                out.write(tmp);
                out.flush();
                out.newLine();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void TweetUser(File file) {
        try {
            BufferedReader s = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("NewTwitter/tweetuser.json")));
            String tmp;
            file.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            while ((tmp = s.readLine()) != null) {
                out.write(tmp);
                out.flush();
                out.newLine();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getTimeDifference(long time) {
        String dif = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

            Date currenttime = new Date(System.currentTimeMillis());

            Date posttime = new java.util.Date(time * 1000);

            String ctime = dateFormat.format(currenttime);
            String ptime = dateFormat.format(posttime);

            Date d1;
            Date d2;

            //Yes Paula, out of all the things i could be working, i work on this.
            //I'LL WORK ON THE SPEECH LATER!

            d1 = dateFormat.parse(ctime);
            d2 = dateFormat.parse(ptime);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime(); //Gets difference in time

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            dif = Math.abs(diffDays) + " Days, " + Math.abs(diffHours) + " Hours, " + Math.abs(diffMinutes) + " Minutes, " + Math.abs(diffSeconds) + " Seconds";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dif;
    }

    public static String getUptime() {
        Long time = System.currentTimeMillis() - Main.startup;
        int seconds = (int) (time / 1000) % 60;
        int minutes = (int) (time / (60000)) % 60;
        int hours = (int) (time / (3600000)) % 24;
        int days = (int) (time / 86400000);
        return String.format("%d Days %d Hours %d Minutes and %d seconds", days, hours, minutes, seconds);
    }

    public static boolean ValidIP(String IP) {
        String IPADDRESS_PATTERN =
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        Pattern ip = Pattern.compile(IPADDRESS_PATTERN);
        Matcher match = ip.matcher(IP);
        return match.find();
    }

    public static boolean ReloadDatabase() {
        try {
            PreparedStatement stmt3 = Main.database.prepareStatement("SELECT a.Channel, a.Permission, a.URL FROM `Channel_Permissions` a, `Rejoin_Channels` b WHERE a.Channel = b.Channel;");
            ResultSet rs3 = stmt3.executeQuery();
            while (rs3.next()) {
                String channel = rs3.getString("Channel");
                if (!JsonUtils.isJSONObject(rs3.getString("Permission"))) {
                    System.out.println("Invalid Json string for channel: " + channel);
                    return false;
                }
                Main.URL.put(channel, rs3.getString("URL"));
                Perms p = JsonUtils.getPermsFromString(rs3.getString("Permission"));
                Main.map.remove(channel.toLowerCase());
                Main.map.put(channel.toLowerCase(), p);
                System.out.println("Loaded setting for channel: " + channel);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getIP(String User, org.pircbotx.hooks.events.MessageEvent event) {
        String ip = null;

        User u = event.getBot().getUserChannelDao().getUser(User);
        event.getBot().sendRaw().rawLineNow("WHOIS " + u.getNick());
        WaitForQueue waitForQueue = new WaitForQueue(event.getBot());
        WhoisEvent test;
        try {
            test = waitForQueue.waitFor(WhoisEvent.class);
            waitForQueue.close();
            if (Utils.ValidIP(test.getHostname()))
                ip = test.getHostname();
            else
                ip = java.net.InetAddress.getByName(test.getHostname()).getHostAddress();
        } catch (InterruptedException ex) {
            Logger.getLogger(com.harry2258.Alfred.listeners.MessageEvent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }

}
