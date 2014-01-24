package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Hardik on 1/23/14.
 */
public class Log extends Command {
    public Log() {
        super("Log", "Parse a minecraft error log", "Log [pastebin link]");
    }

    private Config config;
    private PermissionManager manager;

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        ArrayList<String> info = new ArrayList<>();
        ArrayList<String> items = new ArrayList<>();
        ArrayList<String> Message = new ArrayList<>();
        String message = "";
        String temp = "";
        String[] args = event.getMessage().split(" ");
        String Raw = "";
        for (String word : event.getMessage().split(" ")) {
            if (word.toLowerCase().contains("pastebin")) {
                Raw = "http://pastebin.com/raw.php?i=" + args[1].replaceAll("http://pastebin.com/", "");
            }
            if (word.toLowerCase().contains("hastebin")){
                Raw = "http://hastebin.com/raw/" + args[1].replaceAll("http://hastebin.com/", "");
            }
        }

        String tmp;
        try {
            URL insult;
            insult = new URL(Raw);
            BufferedReader br = new BufferedReader(new InputStreamReader(insult.openStream()));
            while ((tmp = br.readLine()) != null) {
                if (tmp.contains("Server brand: ")) {
                    temp = Colors.BOLD + "Server Brand: " + Colors.NORMAL + tmp.replaceAll("^.*?(?=[A-Z][a-z])|Server brand: ", "").replaceAll("\\\\[.*?\\\\]", "");
                    if (!info.contains(temp)) {
                        info.add(temp);
                    }
                } else if (tmp.contains("Minecraft Version:")) {
                    temp = Colors.BOLD + "Minecraft Version: " + Colors.NORMAL + tmp.replaceAll("^.*?(?=[A-Z][a-z])|Minecraft Version: ", "").replaceAll("\\\\[.*?\\\\]", "");
                    if (!info.contains(temp)) {
                        info.add(temp);
                    }
                } else if (tmp.contains("Java Version: ")) {
                    temp = Colors.BOLD + "Java Version: " + Colors.NORMAL + tmp.replaceAll("^.*?(?=[A-Z][a-z])|Java Version: ", "").replaceAll("\\\\[.*?\\\\]", "");
                    if (!info.contains(temp)) {
                        info.add(temp);
                    }
                } else if (tmp.contains("Is Modded: ")) {
                    temp = Colors.BOLD + "Client Brand: " + Colors.NORMAL + tmp.replaceAll("^.*?(?=[A-Z][a-z])|Is Modded: Definitely; Client brand changed to ", "").replaceAll("\\\\[.*?\\\\]", "");
                    if (!info.contains(temp)) {
                        info.add(temp);
                    }
                }

            }

            for (int x = 0; x < info.size(); x++) {
                Message.add(info.get(x));
            }

            for (String s : Message) {
                message += s + " | \t";
            }

            event.getChannel().send().message(message);
        } catch (Exception e1) {
            e1.printStackTrace();
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
