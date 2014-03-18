package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.FileInputStream;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

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
        //automatically assuming that the system is a *nix system is stupid. don't do it, at least have an if statment or something.
        int unixTime = Integer.valueOf(new Scanner(new FileInputStream("/proc/uptime")).next().replaceAll("\\.[0-9]+", ""));
        int day = (int) TimeUnit.SECONDS.toDays(unixTime);
        long hours = TimeUnit.SECONDS.toHours(unixTime) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(unixTime) - (TimeUnit.SECONDS.toHours(unixTime) * 60);
        long seconds = TimeUnit.SECONDS.toSeconds(unixTime) - (TimeUnit.SECONDS.toMinutes(unixTime) * 60);
        String time = String.format("%d Days %d Hours %d Minutes and %d seconds", day, hours, minute, seconds);
        event.getChannel().send().message(System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch"));
        event.getChannel().send().message(Colors.DARK_GREEN + "System uptime" + Colors.NORMAL + ": " + time);
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
