package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.*;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.HashMap;
import java.util.Map;


public class Test extends Command {

    private Config config;
    private PermissionManager manager;

    public Test() {
        super("Test", "This is a test command", "Test!");
    }


    @Override
    public boolean execute(MessageEvent event) throws Exception {
        event.getChannel().send().message("Test!");
        event.getChannel().send().message(event.getUser().getUserLevels(event.getChannel()).toString());
        /*
        if (!Main.map.containsKey(event.getChannel().getName())) {
            String Jsonfile = System.getProperty("user.dir") + "/Perms/" + event.getChannel().getName() + "/" + "perms.json";
            String perms = JsonUtils.getStringFromFile(Jsonfile);
            Main.map.put(event.getChannel().getName(), perms);
        }
        */
        //event.getChannel().send().message(Main.map.get(event.getChannel().getName()));
        for (Channel channel : event.getBot().getUserBot().getChannels()) {
            System.out.println(channel.getName());
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
