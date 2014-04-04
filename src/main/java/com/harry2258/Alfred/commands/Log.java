package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

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
    public boolean execute(MessageEvent event) throws IOException {
        String[] args = event.getMessage().split(" ");

        ArrayList<String> info = new ArrayList<>();
        ArrayList<String> Message = new ArrayList<>();

        String message = "";
        String temp = "";
        String Raw = args[1];
        String Java = "1";
        String webpage = "";

        for (String word : event.getMessage().split(" ")) {
            if (word.toLowerCase().contains("pastebin.com")) {
                Raw = "http://pastebin.com/raw.php?i=" + args[1].replaceAll(".*(?:bin..om/)", "");
            }
            if (word.toLowerCase().contains("hastebin.com")) {
                Raw = "http://hastebin.com/raw/" + args[1].replaceAll(".*(?:bin..om/)", "");
            }
            if (word.toLowerCase().contains("paste.atlauncher.com")) {
                Raw = "http://paste.atlauncher.com/view/raw/" + args[1].replaceAll(".*(?:view/)", "");
            }
            if (word.toLowerCase().contains("paste.ee")) {
                Raw = "http://paste.ee/r/" + args[1].replaceAll(".*(?:p/)", "");
            }
            if (word.toLowerCase().contains("https://gist.githubusercontent.com")) {
                Raw = args[1] + "raw";
            }
            if (word.toLowerCase().contains("http://pastie.org")) {
                Raw = "http://pastie.org/pastes/" + args[1].replaceAll(".*(?:org/)", "") + "/text";
            }
            System.out.println(Raw);
        }

        if (!Main.cmd.exists()) {
            Main.cmd.getParentFile().mkdirs();
            Main.cmd.createNewFile();
            Utils.Parser(Main.cmd);
        }
        String tmp;
        try {
            String test = JsonUtils.getStringFromFile(Main.cmd.toString());
            JSONObject jsonObj = new JSONObject(test);
            URL url;
            url = new URL(Raw);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((tmp = br.readLine()) != null) {
                if (tmp.contains("FTBLaunch starting up")) {
                    temp = Colors.BOLD + "Launcher: " + Colors.NORMAL + tmp.replaceAll(".*(?:version )|\\)", "");
                    if (!info.contains(temp)) {
                        info.add(temp);
                    }
                } else if (tmp.contains("Server brand: ")) {
                    temp = Colors.BOLD + "Server Brand: " + Colors.NORMAL + tmp.replaceAll("^.*?(?=[A-Z][a-z])", "").replaceAll("\\\\[.*?\\\\]", "").replace("Server brand: ", "");
                    if (!info.contains(temp) && jsonObj.getBoolean("ServerBrand")) {
                        info.add(temp);
                    }
                } else if (tmp.contains("Server type: ")) {
                    temp = Colors.BOLD + "Server type: " + Colors.NORMAL + tmp.replaceAll("^.*?(?=[A-Z][a-z])", "").replaceAll("\\\\[.*?\\\\]", "").replace("Server type: ", "");
                    if (!info.contains(temp) && jsonObj.getBoolean("ServerType")) {
                        info.add(temp);
                    }
                } else if (tmp.contains("Minecraft Version:")) {
                    temp = Colors.BOLD + "Minecraft Version: " + Colors.NORMAL + tmp.replaceAll("^.*?(?=[A-Z][a-z])", "").replaceAll("\\\\[.*?\\\\]", "").replace("Minecraft Version: ", "");
                    if (!info.contains(temp) && jsonObj.getBoolean("MCVersion")) {
                        info.add(temp);
                    }
                } else if (tmp.contains("Java Version: ") && tmp.contains("LaunchFrame.main")) {
                    temp = Colors.BOLD + "Java Version: " + Colors.NORMAL + tmp.replaceAll(".*(?:Java Version:)|(?:sorted as: ).*", "");
                    if (!info.contains(temp) && jsonObj.getBoolean("JavaVersion")) {
                        info.add(temp);
                    }
                    Java = tmp.replaceAll(".*(?:Java Version:)|(?:as: ).*|[._a-z]|[,A-Z]|\\-", "").trim();
                    if (Java.equals("180")) {
                        Java = "18000";
                    }
                } else if (tmp.contains("Is Modded: ")) {
                    temp = Colors.BOLD + "Client Brand: " + Colors.NORMAL + tmp.replaceAll("^.*?(?=[A-Z][a-z])", "").replaceAll("\\\\[.*?\\\\]", "").replace("Is Modded: Definitely; Client brand changed to ", "");
                    if (!info.contains(temp) && jsonObj.getBoolean("Modded")) {
                        info.add(temp);
                    }
                } else if (tmp.contains("Feed The Beast Mod Pack")) {
                    temp = Colors.BOLD + "Mods: " + Colors.NORMAL + tmp.replaceAll("^.*?(?=[A-Z][a-z])", "").replaceAll("\\\\[.*?\\\\]", "").replaceAll(".*(?:, )|mods active", "");
                    if (!info.contains(temp) && jsonObj.getBoolean("Modded")) {
                        info.add(temp);
                    }
                } else if (tmp.contains("Operating System: ") || tmp.contains("OS: ")) {
                    temp = Colors.BOLD + "OS: " + Colors.NORMAL + tmp.replaceAll("^.*?(?=[A-Z][a-z])", "").replaceAll("\\\\[.*?\\\\]", "").replaceAll(".*(?:Operating System: )|.*(?:OS: )", "").replaceAll("x86", "32-Bit").replaceAll("x64", "64-Bit");
                    if (!info.contains(temp) && jsonObj.getBoolean("OSName")) {
                        info.add(temp);
                    }
                }
                webpage += tmp;
            }

            //Error.getProblems(webpage, event);
            for (int x = 0; x < info.size(); x++) {
                Message.add(info.get(x));
            }

            for (String s : Message) {
                message += s + " | \t";
            }
            if (message.length() > 500) {
                event.getChannel().send().message("The log was too big and was not sent! Please retry again or disable some features in parser.json");
                return false;
            }
            event.getChannel().send().message(message);

            return true;
        } catch (JSONException e1) {
            event.getChannel().send().message("OH NO! The parser.json is corrupted, Please delete it and retry.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String readString(InputStream stream) {
        Scanner scanner = new Scanner(stream).useDelimiter("\\ A");
        return scanner.hasNext() ? scanner.next() : "";
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
