package com.harry2258.Alfred.listeners;

import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.CommandRegistry;
import com.harry2258.Alfred.api.Utils;
import com.harry2258.Alfred.api.Command;
import org.pircbotx.Colors;
import org.pircbotx.hooks.ListenerAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class MessageEvent extends ListenerAdapter {

    private Config config;
    private PermissionManager manager;

    public MessageEvent(Config conf, PermissionManager man) {
        this.config = conf;
        this.manager = man;
    }

    @Override
    public void onMessage(org.pircbotx.hooks.events.MessageEvent event) {
        String trigger = config.getTrigger();
        if (event.getMessage().startsWith(trigger)) {

            try {
                String commandname = event.getMessage().split(" ")[0].substring(1).toLowerCase();
                File commandfile = new File("commands/" + event.getChannel().getName() + "/" + commandname + ".cmd");
                if (commandfile.exists()) {
                    BufferedReader in = new BufferedReader(new FileReader(commandfile));
                    String tmp;
                    while ((tmp = in.readLine()) != null){
                        event.getChannel().send().message(commandname + ": " + tmp);
                    }
                    in.close();
                }
                String classname = Character.toUpperCase(event.getMessage().split(" ")[0].charAt(1)) + event.getMessage().split(" ")[0].substring(2).toLowerCase();
                String permission = "command." + classname.toLowerCase();
                if (manager.hasPermission(permission, event.getUser())) {
                    Command command = CommandRegistry.getCommand(classname);
                    command.setConfig(config);
                    if (!command.execute(event)) {
                        event.getChannel().send().message(Colors.RED + "An error occurred! " + command.getHelp());
                        return;
                    }
                } else {
                    event.getUser().send().notice(config.getPermissionDenied().replaceAll("%USERNAME%", event.getUser().getNick()));
                }
            } catch (Exception e) {
                /*
                 * Unknown command
                 * >implying i give a fuck
                 * Logger.getLogger(MessageEvent.class.getName()).log(Level.SEVERE, null, e);
                 */
            }
        }
//        for (String word : event.getMessage().split(" ")) {
//            if (Utils.isUrl(word)) {
//                event.getChannel().send().message(event.getUser().getNick() + "'s URL: " + Utils.getTitle(word));
//            }
//        }
        
        if (event.getMessage().contains("Changelog") || event.getMessage().contains("changelog")) {
            event.getChannel().send().message(event.getUser().getNick() + ", Changelogs for all modpacks are here: http://is.gd/3NH0cH");
        }
        
    }
}
