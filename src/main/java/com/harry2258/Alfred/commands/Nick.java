package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.JsonUtils;
import com.harry2258.Alfred.api.PermissionManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.pircbotx.hooks.events.MessageEvent;

public class Nick extends Command {
    private Config config;
    private PermissionManager manager;

    public Nick() {
        super("Nick", "changes the bot's nickname", "Nick newnickname");
    }

    @Override
    public boolean execute(MessageEvent event) {
        String Jsonfile = System.getProperty("user.dir") + "/Perms/" + event.getChannel().getName();
        try {
            String perms = JsonUtils.getStringFromFile(Jsonfile);
            JSONObject jsonObj = new JSONObject(perms);
            if (jsonObj.getJSONObject("Perms").getString("Exec").contains(event.getUser().getNick()) && event.getUser().isVerified()) {
            String[] args = event.getMessage().split(" ");
            if (args.length >= 1) {
                event.getBot().sendIRC().changeNick(event.getMessage().split(" ")[1]);
                return true;
            }
        }
        } catch (Exception ex) {
            Logger.getLogger(Nick.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
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
