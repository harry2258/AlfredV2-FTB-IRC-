package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.*;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Hardik on 1/14/14.
 */
public class Remind extends Command {

    private Date date = new Date();
    private Config config;
    private PermissionManager manager;

    public Remind() {
        super("Remind", "Adds a reminder for the user!", "Remind [user] [reminder]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        Set<User> user = new HashSet<>(event.getChannel().getUsers());
        if (args.length >= 3) {
            String newuser;
            if (user.contains(args[1])) {
                newuser = Utils.getAccount(event.getBot().getUserChannelDao().getUser(args[1]), event);
            } else {
                newuser = args[1];
            }

            File file = new File(System.getProperty("user.dir") + "/Reminders/" + newuser + ".txt");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            StringBuilder sb = new StringBuilder();

            for (int i = 2; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }

            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
                out.println(date + " [" + event.getUser().getNick() + "] " + sb);
                out.close();
                MessageUtils.sendUserNotice(event, "Reminder set! It will be sent the next time " + args[1] + " is active.");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
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
