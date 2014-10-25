package com.harry2258.Alfred.commands;

import com.google.gson.JsonObject;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.JsonUtils;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

/**
 * Created by Hardik at 12:44 PM on 5/28/14.
 */
public class Stats extends Command {

    public Stats() {
        super("Stats", "Gets stats on modpack that use YAMPST!", "Stats [Author] [Modpack Name]");
    }

    private Config config;
    private PermissionManager manager;

    @Override
    public boolean execute(MessageEvent event) {
        /*

        String[] args = event.getMessage().split(" ");

        String Owner = args[1];
        String TrackerName = args[2];
        String TrackerID;

        String Installed = "0";
        String Launched = "0";
        int Crashed = 0;
        String Hours = PlayTime((long)0);
        if (args.length < 3 || args.length >= 4) {
            return false;
        }
        try {
            URL id = new URL("http://api.yampst.net/public/v1.php?trackername=" + TrackerName + "&owner=" + Owner);
            URLConnection c = id.openConnection();
            c.setReadTimeout((int) TimeUnit.SECONDS.toMillis(5));
            BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
            TrackerID = JsonUtils.getJsonObject(br.readLine()).getAsJsonArray("list").get(0).getAsJsonObject().get(args[1]).toString().replaceAll("\"","");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            URL info = new URL("http://api.yampst.net/public/v1.php?trackid=" + TrackerID + "&owner=" + Owner);
            System.out.println(info);
            URLConnection c = info.openConnection();
            c.setReadTimeout((int) TimeUnit.SECONDS.toMillis(5));
            BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
            JsonObject jsonObj = JsonUtils.getJsonObject(br.readLine()).getAsJsonObject("data").getAsJsonObject("basic").getAsJsonObject("client");
            try { Installed = jsonObj.get("install").toString(); } catch (Exception install) {install.printStackTrace();}
            try { Launched = jsonObj.get("launch").toString(); } catch (Exception launch) {launch.printStackTrace();}
            try { Crashed = jsonObj.get("crash").getAsInt(); } catch (Exception crash) {crash.printStackTrace();}
            try { Hours = PlayTime(TimeUnit.HOURS.toMillis(jsonObj.get("time").getAsLong())); } catch (Exception time) {time.printStackTrace();}

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        event.getChannel().send().message(String.format("The modpack %s has been installed %s times, launched %s times and has crashed %s times. Total play time is: %s", TrackerName, Installed, Launched, String.valueOf(Crashed), Hours));
        */
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

    public static String PlayTime(Long time) {
        int seconds = (int) (time / 1000) % 60;
        int minutes = (int) (time / (60000)) % 60;
        int hours = (int) (time / (3600000)) % 24;
        int days = (int) (time / 86400000);
        return String.format("%d Days %d Hours %d Minutes and %d Seconds", days, hours, minutes, seconds);
    }
}
