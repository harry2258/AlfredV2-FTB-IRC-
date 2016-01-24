package com.harry2258.Alfred.commands;

import com.google.gson.JsonObject;
import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.*;
import com.harry2258.Alfred.json.Perms;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by Hardik on 1/15/14.
 */
public class Login extends Command {
    private Config config;
    private PermissionManager manager;

    public Login() {
        super("Login", "Log into the bot!", "Login (again/info)");
    }

    public static String Group(String user, String channel) {
        String group = "None :<";

        try {
            Perms perm = Main.map.get(channel);

            if (perm.getPermission().getMods().contains(user))
                group = "Moderator";

            if (perm.getPermission().getAdmins().contains(user))
                group = "Admin";

            String Exec = JsonUtils.getStringFromFile(Main.jsonFilePath.toString());
            JsonObject exec = JsonUtils.getJsonObject(Exec);
            for (String users : exec.getAsJsonObject("Perms").get("Exec").toString().replaceAll("[\\[\\]\"]", "").split(",")) {
                if (users.equalsIgnoreCase(user))
                    return "Exec";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return group;
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("again")) {
                MessageUtils.sendUserNotice(event, "Removing old login!");
                Main.Login.remove(event.getUser().getNick());
                Main.NotLoggedIn.remove(event.getUser().getNick());
                MessageUtils.sendUserNotice(event, "Logging in...");
                String account = Utils.getAccount(event.getUser(), event);
                Main.Login.put(event.getUser().getNick(), account);
                MessageUtils.sendUserNotice(event, "Logged in!");
                return true;
            }
            if (args[1].equalsIgnoreCase("info")) {
                MessageUtils.sendUserNotice(event, "You are logged in as: " + Main.Login.get(event.getUser().getNick()));
                MessageUtils.sendUserNotice(event, "You are in group: " + Group(Main.Login.get(event.getUser().getNick()), event.getChannel().getName().toLowerCase()));
                return true;
            }
        }


        if (Main.Login.containsKey(event.getUser().getNick()) && !Main.NotLoggedIn.contains(event.getUser().getNick())) {
            MessageUtils.sendUserNotice(event, "You are already logged in! If you want to update login, use \"" + config.getTrigger() + "login again\"");
            return true;
        }
        String account = Utils.getAccount(event.getUser(), event);
        Main.Login.put(event.getUser().getNick(), account);
        Main.NotLoggedIn.remove(event.getUser().getNick());
        MessageUtils.sendUserNotice(event, "You are now Logged in!");
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
