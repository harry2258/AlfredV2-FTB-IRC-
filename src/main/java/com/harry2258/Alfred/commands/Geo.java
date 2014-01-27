package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.json.JSONObject;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.WhoisEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Hardik on 1/26/14.
 */
public class Geo extends Command {

    public Geo() {
        super("Geo", "Geolocate an IP address!");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        ArrayList<String> info = new ArrayList<>();
        ArrayList<String> Message = new ArrayList<>();
        String message = "";
        String ip = "";
        if (event.getChannel().isChannelPrivate() || event.getChannel().isSecret() || event.getChannel().isInviteOnly()){
            event.getChannel().send().message("No no no! Not in here!");
            return true;
        }

        if (args.length == 3 && PermissionManager.hasExec(event.getUser(), event) && args[1].equals("exec")) {

            if (event.getChannel().getUsers().toString().contains(args[2])) {
                User u = event.getBot().getUserChannelDao().getUser(args[2]);
                String user = "";
                event.getBot().sendRaw().rawLineNow("WHOIS " + u.getNick());
                WaitForQueue waitForQueue = new WaitForQueue(event.getBot());
                WhoisEvent test = null;
                try {
                    test = waitForQueue.waitFor(WhoisEvent.class);
                    waitForQueue.close();
                    ip = test.getHostname();
                } catch (InterruptedException ex) {
                    Logger.getLogger(com.harry2258.Alfred.listeners.MessageEvent.class.getName()).log(Level.SEVERE, null, ex);

                }
            }

            String geo = "http://freegeoip.net/json/" + ip;
            String tmp = "";
            try {
                URL url;
                url = new URL(geo);
                BufferedReader re = new BufferedReader(new InputStreamReader(url.openStream()));
                String jsonstring = re.readLine();
                JSONObject jsonObj = new JSONObject(jsonstring);
                System.out.println(jsonObj.toString());
                info.add(Colors.BOLD + "City: " + Colors.NORMAL + jsonObj.getString("city"));
                info.add(Colors.BOLD + "Zip: " + Colors.NORMAL + jsonObj.getString("zipcode"));
                info.add(Colors.BOLD + "State: " + Colors.NORMAL + jsonObj.getString("region_name"));
                info.add(Colors.BOLD + "Country: " + Colors.NORMAL + jsonObj.getString("country_name"));
                info.add(Colors.BOLD + "Coords: " + Colors.NORMAL + jsonObj.getString("latitude") + " " + jsonObj.getString("longitude"));

                for (int x = 0; x < info.size(); x++) {
                    Message.add(info.get(x));
                }

                for (String s : Message) {
                    message += s + " | \t";
                }

                event.getChannel().send().message(message);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        if (event.getChannel().getUsers().toString().contains(args[1])) {
            User u = event.getBot().getUserChannelDao().getUser(args[1]);
            String user = "";
            event.getBot().sendRaw().rawLineNow("WHOIS " + u.getNick());
            WaitForQueue waitForQueue = new WaitForQueue(event.getBot());
            WhoisEvent test = null;
            try {
                test = waitForQueue.waitFor(WhoisEvent.class);
                waitForQueue.close();
                ip = test.getHostname();
            } catch (InterruptedException ex) {
                Logger.getLogger(com.harry2258.Alfred.listeners.MessageEvent.class.getName()).log(Level.SEVERE, null, ex);

            }
        }

        String geo = "http://freegeoip.net/json/" + ip;
        String tmp = "";
        try {
            URL url;
            url = new URL(geo);
            BufferedReader re = new BufferedReader(new InputStreamReader(url.openStream()));
            String jsonstring = re.readLine();
            JSONObject jsonObj = new JSONObject(jsonstring);
            System.out.println(jsonObj.toString());
            info.add(Colors.BOLD + "State: " + Colors.NORMAL + jsonObj.getString("region_name"));
            info.add(Colors.BOLD + "Country: " + Colors.NORMAL + jsonObj.getString("country_name"));
            info.add(Colors.BOLD + "Coords: " + Colors.NORMAL + jsonObj.getString("latitude").replaceAll("(?:\\.).*","") + " " + jsonObj.getString("longitude").replaceAll("(?:\\.).*",""));

            for (int x = 0; x < info.size(); x++) {
                Message.add(info.get(x));
            }

            for (String s : Message) {
                message += s + " | \t";
            }

            event.getChannel().send().message(message);
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
