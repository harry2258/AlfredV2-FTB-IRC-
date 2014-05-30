package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Hardik on 1/16/14.
 */
public class Sys extends Command {
    private Config config;
    private PermissionManager manager;

    public Sys() {
        super("Sys", "Gets the system uptime!");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        long uptime = -1;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            Process uptimeProc = Runtime.getRuntime().exec("net stats srv");
            BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("Statistics since")) {
                    SimpleDateFormat format = new SimpleDateFormat("'Statistics since' MM/dd/yyyy hh:mm:ss a");
                    Date boottime = format.parse(line);
                    uptime = System.currentTimeMillis() - boottime.getTime();
                    System.out.println(System.currentTimeMillis() + " | " + boottime.getTime());
                }
            }
            long Days = TimeUnit.MILLISECONDS.toDays(uptime);
            long Hours = TimeUnit.MILLISECONDS.toHours(uptime) - (Days * 24);
            long Mins = TimeUnit.MILLISECONDS.toMinutes(uptime) - (TimeUnit.MILLISECONDS.toHours(uptime) * 60);
            long Secs = TimeUnit.MILLISECONDS.toSeconds(uptime) - (TimeUnit.MILLISECONDS.toMinutes(uptime) * 60);
            String time = String.format("%d Days, %d Hours, %d Min, %d Sec", Days, Hours, Mins, Secs);
            event.getChannel().send().message(Colors.DARK_GREEN + "System uptime" + Colors.NORMAL + ": " + time);

        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            /*
            Process uptimeProc = Runtime.getRuntime().exec("uptime");
            BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
            String line = in.readLine();
            String time = "";
            if (line != null) {
                Pattern parse = Pattern.compile("((\\d+) days,)? (\\d+):(\\d+)");
                Matcher matcher = parse.matcher(line);
                if (matcher.find()) {
                    System.out.println(matcher.toString());
                    String _days = matcher.group(2);
                    String _hours = matcher.group(3);
                    String _minutes = matcher.group(4);
                    int days = _days != null ? Integer.parseInt(_days) : 0;
                    int hours = _hours != null ? Integer.parseInt(_hours) : 0;
                    int minutes = _minutes != null ? Integer.parseInt(_minutes) : 0;
                    //uptime = (minutes * 60000) + (hours * 60000 * 60) + (days * 6000 * 60 * 24);
                    time = String.format("%d Days, %d Hours, %d Min", days, hours, minutes);
                }
            }
            */
            int unixTime = Integer.valueOf(new Scanner(new FileInputStream("/proc/uptime")).next().replaceAll("\\.[0-9]+", ""));
            int day = (int) TimeUnit.SECONDS.toDays(unixTime);
            long hours = TimeUnit.SECONDS.toHours(unixTime) - (day * 24);
            long minute = TimeUnit.SECONDS.toMinutes(unixTime) - (TimeUnit.SECONDS.toHours(unixTime) * 60);
            long seconds = TimeUnit.SECONDS.toSeconds(unixTime) - (TimeUnit.SECONDS.toMinutes(unixTime) * 60);
            String time = String.format("%d Days %d Hours %d Minutes and %d seconds", day, hours, minute, seconds);
            event.getChannel().send().message(Colors.DARK_GREEN + "System uptime" + Colors.NORMAL + ": " + time);
        }

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
