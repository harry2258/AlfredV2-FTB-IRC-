package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.JsonUtils;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.File;

/**
 * Created by Hardik on 1/15/14.
 */
public class Reload extends Command {
    private Config config;
    private PermissionManager manager;

    public Reload() {
        super("Reload", "Reload all channel permissions!");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        Main.map.clear();
        File file = new File(System.getProperty("user.dir") + "/perms/" + event.getChannel().getName().toLowerCase() + "/" + "perms.json");
        if (!file.exists()) {
            System.out.println("Creating perms.json for " + event.getChannel());
            JsonUtils.createJsonStructure(file);
        }
        String perms = JsonUtils.getStringFromFile(file.toString());
        Main.map.put(event.getChannel().getName(), perms);
        event.getUser().send().notice("Permissions were reloaded for " + event.getChannel().getName() + "!");
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
