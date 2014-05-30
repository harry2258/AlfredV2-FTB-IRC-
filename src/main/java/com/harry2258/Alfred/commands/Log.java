package com.harry2258.Alfred.commands;

import com.google.gson.JsonObject;
import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.*;
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
        super("Log", "Parse a minecraft error log", "Log [Crash log link]");
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
        String webpage = "";
        String Description = "";
        String CausedBy = "";
        String Stacktrace = "";

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
        }

        if (!Main.cmd.exists()) {
            Main.cmd.getParentFile().mkdirs();
            Main.cmd.createNewFile();
            Utils.Parser(Main.cmd);
        }
        String tmp;
        try {
            String test = JsonUtils.getStringFromFile(Main.cmd.toString());
            JsonObject jsonObj = JsonUtils.getJsonObject(test);
            URL url;
            url = new URL(Raw);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((tmp = br.readLine()) != null) {
                if (tmp.contains("This paste has been removed!")) {
                    event.getChannel().send().message("The paste cannot be found!");
                    return true;
                } else if (tmp.contains("FTBLaunch starting up")) {
                    temp = Colors.BOLD + "Launcher: " + Colors.NORMAL + tmp.replaceAll(".*(?:version )|\\)", "");
                    if (!info.contains(temp)) {
                        info.add(temp);
                    }
                } else if (tmp.contains("Server brand: ")) {
                    temp = Colors.BOLD + "Server Brand: " + Colors.NORMAL + tmp.replaceAll("^.*?(?=[A-Z][a-z])", "").replaceAll("\\\\[.*?\\\\]", "").replace("Server brand: ", "");
                    if (!info.contains(temp) && jsonObj.get("ServerBrand").getAsBoolean()) {
                        info.add(temp);
                    }
                } else if (tmp.contains("Server type: ")) {
                    temp = Colors.BOLD + "Server type: " + Colors.NORMAL + tmp.replaceAll("^.*?(?=[A-Z][a-z])", "").replaceAll("\\\\[.*?\\\\]", "").replace("Server type: ", "");
                    if (!info.contains(temp) && jsonObj.get("ServerType").getAsBoolean()) {
                        info.add(temp);
                    }
                } else if (tmp.contains("Minecraft Version:")) {
                    temp = Colors.BOLD + "Minecraft Version: " + Colors.NORMAL + tmp.replaceAll("^.*?(?=[A-Z][a-z])", "").replaceAll("\\\\[.*?\\\\]", "").replaceAll(".*(?:Minecraft Version: )", "");
                    if (!info.contains(temp) && jsonObj.get("MCVersion").getAsBoolean()) {
                        info.add(temp);
                    }
                } else if (tmp.contains("Java Version: ") && tmp.contains("LaunchFrame.main")) {
                    temp = Colors.BOLD + "Java Version: " + Colors.NORMAL + tmp.replaceAll(".*(?:Java Version:)|(?:sorted as: ).*", "");
                    if (!info.contains(temp) && jsonObj.get("JavaVersion").getAsBoolean()) {
                        info.add(temp);
                    }
                } else if (tmp.contains("Is Modded: ")) {
                    temp = Colors.BOLD + "Client Brand: " + Colors.NORMAL + tmp.replaceAll("^.*?(?=[A-Z][a-z])", "").replaceAll("\\\\[.*?\\\\]", "").replaceAll(".*(?:Is Modded: Definitely; Client brand changed to )", "");
                    if (!info.contains(temp) && jsonObj.get("Modded").getAsBoolean()) {
                        info.add(temp);
                    }
                } else if (tmp.contains("Feed The Beast Mod Pack")) {
                    temp = Colors.BOLD + "Mods: " + Colors.NORMAL + tmp.replaceAll("^.*?(?=[A-Z][a-z])", "").replaceAll("\\\\[.*?\\\\]", "").replaceAll(".*(?:, )|mods active", "");
                    if (!info.contains(temp) && jsonObj.get("Modded").getAsBoolean()) {
                        info.add(temp);
                    }
                } else if (tmp.contains("Operating System: ") || tmp.contains("OS: ")) {
                    temp = Colors.BOLD + "OS: " + Colors.NORMAL + tmp.replaceAll("^.*?(?=[A-Z][a-z])", "").replaceAll("\\\\[.*?\\\\]", "").replaceAll(".*(?:Operating System: )|.*(?:OS: )", "").replaceAll("x86|x64|32-bit|64-bit", "");
                    if (!info.contains(temp) && jsonObj.get("OSName").getAsBoolean()) {
                        info.add(temp);
                    }
                } else if (tmp.contains("Caused by") || tmp.contains("java.lang.NoClassDefFoundError")) {
                    System.out.println(tmp);
                    CausedBy = Colors.BOLD + "Caused by: " + Colors.NORMAL + tmp.replaceAll(".*(?:Caused by: )", "").replaceAll("^.*?(?=[A-Z][a-z])", "").replaceAll("\\\\[.*?\\\\]", "");
                } else if (tmp.contains("Description: ")) {
                    String harhar = readString(url.openStream()).replaceAll("\\n|\\r|\\t", " ");
                    CausedBy = Colors.BOLD + "Error: " + Colors.NORMAL + harhar.replaceAll(".*(?:" + tmp + ")|at.*", "").replaceAll("^.*?(?=[A-Z][a-z])", "").replaceAll("\\\\[.*?\\\\]", "").trim();
                    Description = Colors.BOLD + "Description: " + Colors.NORMAL + tmp.replaceAll(".*(?:Description: )", "").replaceAll("^.*?(?=[A-Z][a-z])", "").replaceAll("\\\\[.*?\\\\]", "");
                } else if (tmp.contains("Stacktrace:")) {
                    String harhar = readString(url.openStream()).replaceAll("\\n|\\r|\\t", " ");
                    Stacktrace = Colors.BOLD + "Stacktrace: " + Colors.NORMAL + harhar.replaceAll("^.*?(?=[A-Z][a-z])", "").replaceAll("\\\\[.*?\\\\]", "").replaceAll(".*(?:Stacktrace:)|\\)   at.*", "").replaceAll("   ", "").replaceAll("\\)  at.*", "") + ")";
                }
                webpage += tmp;
            }

            for (String anInfo : info) {
                Message.add(anInfo);
            }

            for (String s : Message) {
                message += s + " | \t";
            }

            if (message.length() > 500) {
                event.getChannel().send().message("The log was too big and was not sent! Please retry again or disable some features in parser.json");
                return false;
            } else if (message.isEmpty()) {
                event.getChannel().send().message("Could not get any information from that log!");
            } else {
                event.getChannel().send().message(message);
            }
            /*
            if (!Description.isEmpty() && Description.length() < 500) {
                if (jsonObj.getBoolean("Information")) {
                    event.getChannel().send().message(Description);
                } else {
                    event.getUser().send().message(Description);
                }
            }

            if (!CausedBy.isEmpty() && CausedBy.length() < 500) {
                if (jsonObj.getBoolean("Information")) {
                    event.getChannel().send().message(CausedBy);
                } else {
                    event.getUser().send().message(CausedBy);
                }
            }

            if (!Stacktrace.isEmpty() && Stacktrace.length() < 500) {
                if (jsonObj.getBoolean("Stacktrace")) {
                    event.getChannel().send().message(Stacktrace);
                } else {
                    event.getUser().send().message(Stacktrace);
                }
            }
            */
            Error.getProblems(webpage, event);
            return true;
        //} catch (JSONException e1) {
        //    event.getChannel().send().message("OH NO! The parser.json is corrupted, Please delete it and retry.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String readString(InputStream stream) {
        Scanner scanner = new Scanner(stream).useDelimiter("\\A");
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
