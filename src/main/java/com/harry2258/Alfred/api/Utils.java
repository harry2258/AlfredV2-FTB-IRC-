/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.UserLevel;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

/**
 *
 * @author Zack
 */
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
                info = String.format("%s - Content Type: %s Size: %skb", title, type, length);
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

    public static String checkMojangServers() {
        String returns = null;
        try {
            URL url;
            url = new URL("https://status.mojang.com/check");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String result;
            while ((result = reader.readLine()) != null) {
                String a = result.replace("red", Colors.RED + "Offline" + Colors.NORMAL).replace("green", Colors.DARK_GREEN + "Online" + Colors.NORMAL).replace("[", "").replace("]", "");
                String b = a.replace("{", "").replace("}", "").replace(":", ": ").replace("\"", "").replaceAll(",", " | ");
                returns = b.replace("login.minecraft.net", "Login").replace("session.minecraft.net","Session").replace("account.mojang.com", "Account").replace("auth.mojang.com", "Auth").replace("skins.minecraft.net","Skins").replace("authserver.mojang.com","Auth Server").replace("sessionserver.mojang.com", "Session Server").replace("minecraft.net", "Minecraft");
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
                returns = "MOTD: " + args1[3].replaceAll("§[a-m]", "").replaceAll("§[1234567890]", "") + "   players: [" + args1[4] + "/" + args1[5] + "]";
            } else {
                returns = "MOTD: " + args1[3] + "   players: [" + args1[4] + "/" + args1[5] + "]";
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
            String result = String.format("Google: %s | %s | (%s)", StringEscapeUtils.unescapeHtml4(output.get("titleNoFormatting").toString().replaceAll("\"", "")), StringEscapeUtils.unescapeHtml4(output.get("content").toString().replaceAll("\\s+", " ").replaceAll("\\<.*?>", "").replaceAll("\"", "")), output.get("url").toString().replaceAll("\"", ""));
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
    
    public static String checkCreeperHost() throws UnknownHostException, IOException {
        //---------------------------
        String Offline = "Offline";
        String Online = "Online";
        Socket socket = null;
        //---------------------------
        String maidencheck = null;
        String nottingcheck = null;
        String grantcheck = null;
        String atlantacheck = null;
        String atlanta2check = null;
        String chicagocheck = null;
        String chicago2check = null;
        String LAcheck = null;
        boolean Maiden = false;
        boolean Notting = false;
        boolean Grant = false;
        boolean Atlant = false;
        boolean Atlant2 = false;
        boolean Chicag = false;
        boolean Chicag2 = false;
        boolean Los = false;
        
//----------------------
        try {
              socket = new Socket("england1.creeperrepo.net", 80);
              Maiden = true;
            } finally {            
        if (socket != null) try { socket.close(); }
        catch(IOException M) {System.out.println(M);}
        }
//----------------------
        try {
              socket = new Socket("england2.creeperrepo.net", 80);
              Notting = true;
            } finally {            
        if (socket != null) try { socket.close(); }
        catch(IOException M) {System.out.println(M);}
        }
//----------------------
        try {
              socket = new Socket("england3.creeperrepo.net", 80);
              Grant = true;
            } finally {            
        if (socket != null) try { socket.close(); }
        catch(IOException M) {System.out.println(M);}
        }
//----------------------
        try {
              socket = new Socket("atlanta1.creeperrepo.net", 80);
              Atlant = true;
            } finally {            
        if (socket != null) try { socket.close(); }
        catch(IOException M) {System.out.println(M);}
        }
//----------------------
        try {
              socket = new Socket("atlanta2.creeperrepo.net", 80);
              Atlant2 = true;
            } finally {            
        if (socket != null) try { socket.close(); }
        catch(IOException M) {System.out.println(M);}
        }
//----------------------
        try {
              socket = new Socket("chicago1.creeperrepo.net", 80);
              Chicag = true;
            } finally {            
        if (socket != null) try { socket.close(); }
        catch(IOException C) {System.out.println(C);}
        }
//----------------------
        try {
              socket = new Socket("chicago2.creeperrepo.net", 80);
              Chicag2 = true;
            } finally {            
        if (socket != null) try { socket.close(); }
        catch(IOException C2) {System.out.println(C2);}
        }
//----------------------
        try {
              socket = new Socket("losangeles1.creeperrepo.net", 80);
              Los = true;
            } finally {            
        if (socket != null) try { socket.close(); }
        catch(IOException LS) {
        System.out.println(LS);}
        }
//----------------------
           
       if (Maiden) {
           maidencheck = Colors.DARK_GREEN + Online + Colors.NORMAL + " | ";
       } else {
           maidencheck = Colors.RED + Offline + Colors.NORMAL + " | ";
       }
       if (Notting) {
           nottingcheck = Colors.DARK_GREEN + Online + Colors.NORMAL + " | ";
       } else {
           nottingcheck = Colors.RED + Offline + Colors.NORMAL + " | ";
       }
       if (Grant) {
           grantcheck = Colors.DARK_GREEN + Online + Colors.NORMAL + " | ";
       } else {
           grantcheck = Colors.RED + Offline + Colors.NORMAL + " | ";
       }
       if (Atlant) {
           atlantacheck = Colors.DARK_GREEN + Online + Colors.NORMAL + " | ";
       } else {
           atlantacheck = Colors.RED + Offline + Colors.NORMAL + " | ";
       }
       if (Atlant2) {
           atlanta2check = Colors.DARK_GREEN + Online + Colors.NORMAL + " | ";
       } else {
           atlanta2check = Colors.RED + Offline + Colors.NORMAL + " | ";
       }
       if (Chicag) {
           chicagocheck = Colors.DARK_GREEN + Online + Colors.NORMAL + " | ";
       } else {
           chicagocheck = Colors.RED + Offline + Colors.NORMAL + " | ";
       }
       if (Chicag2) {
           chicago2check = Colors.DARK_GREEN + Online + Colors.NORMAL + " | ";
       } else {
           chicago2check = Colors.RED + Offline + Colors.NORMAL + " | ";
       }
       if (Los) {
           LAcheck = Colors.DARK_GREEN + Online;
       } else {
           LAcheck = Colors.RED + Offline;
       }
       
         String status = "Maidenhead: "
                       + maidencheck 
                       + "Nottingham: "
                       + nottingcheck 
                       + "Grantham: "
                       + grantcheck 
                       + "Atlanta: "
                       + atlantacheck 
                       + "Atlanta 2: "
                       + atlanta2check 
                       + "Chicago: " 
                       + chicagocheck 
                       + "Chicago 2: " 
                       + chicago2check 
                       + "LA: "
                       + LAcheck
                       ;
       return status;
       
        }
}