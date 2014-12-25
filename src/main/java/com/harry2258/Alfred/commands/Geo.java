package com.harry2258.Alfred.commands;

import com.google.gson.JsonObject;
import com.harry2258.Alfred.api.*;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.WhoisEvent;
import twitter4j.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Geo extends Command {

    public Geo() {
        super("Geo", "Geolocate an IP address!", "Geo [IP/User]");
    }

    @Override
    public boolean execute(MessageEvent event) throws UnknownHostException {
        String[] args = event.getMessage().split(" ");
        String info = "";
        String ip = args[1];

        if (event.getChannel().isChannelPrivate() || event.getChannel().isSecret() || event.getChannel().isInviteOnly()) {
            MessageUtils.sendChannel(event, "No no no! Not in here!");
            return true;
        }

        if (args.length == 3 && PermissionManager.hasExec(event.getUser().getNick()) && args[1].equals("exec")) {
            if (event.getChannel().getUsers().toString().contains(args[2])) {
                User u = event.getBot().getUserChannelDao().getUser(args[2]);
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
            } else {
                if (Utils.ValidIP(args[2]))
                    ip = args[2];
                else
                    try {
                        ip = java.net.InetAddress.getByName(args[2]).getHostAddress();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
            }

            String geo = "http://ip-api.com/json/" + ip;
            try {
                URL url;
                url = new URL(geo);
                BufferedReader re = new BufferedReader(new InputStreamReader(url.openStream()));
                String jsonstring = re.readLine();
                JsonObject jsonObj = JsonUtils.getJsonObject(jsonstring);
                info += Colors.BOLD + "City: " + Colors.NORMAL + jsonObj.get("city").getAsString() + " | \t";
                info += Colors.BOLD + "Zip: " + Colors.NORMAL + jsonObj.get("zip").getAsString() + " | \t";
                info += Colors.BOLD + "State: " + Colors.NORMAL + jsonObj.get("regionName").getAsString() + " | \t";
                info += Colors.BOLD + "Country: " + Colors.NORMAL + jsonObj.get("country").getAsString() + " | \t";
                info += Colors.BOLD + "Coords: " + Colors.NORMAL + jsonObj.get("lat").getAsString() + " " + jsonObj.get("lon").getAsString();

                event.getUser().send().message(info);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        if (event.getChannel().getUsers().asList().contains(args[1])) {
            ip = Utils.getIP(args[1], event);
        } else {
            if (Utils.ValidIP(args[1]))
                ip = args[1];
            else
                ip = java.net.InetAddress.getByName(args[1]).getHostAddress();
        }

        String geo = "http://ip-api.com/json/" + ip;
        String jsonstring = "";
        try {
            URL url;
            url = new URL(geo);
            BufferedReader re = new BufferedReader(new InputStreamReader(url.openStream()));
            jsonstring = re.readLine();
            JSONObject jsonObj = new JSONObject(jsonstring);
            info += Colors.BOLD + "State: " + Colors.NORMAL + (jsonObj.getString("regionName").equals("") ? "N/A" : jsonObj.getString("regionName")) + " | \t";
            info += Colors.BOLD + "Country: " + Colors.NORMAL + (jsonObj.getString("country").equals("") ? "N/A" : jsonObj.getString("country")) + " | \t";
            info += Colors.BOLD + "Coords: " + Colors.NORMAL + jsonObj.getString("lat").replaceAll("(?:\\.).*", "") + " " + jsonObj.getString("lon").replaceAll("(?:\\.).*", "");

            MessageUtils.sendChannel(event, info);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void setConfig(Config config) {

    }

    @Override
    public void setManager(PermissionManager manager) {

    }
}
