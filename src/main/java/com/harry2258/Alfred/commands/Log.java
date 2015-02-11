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
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Hardik on 1/23/14.
 */
public class Log extends Command {
    private Config config;
    private PermissionManager manager;
    public Log() {
        super("Log", "Parse a minecraft error log", "Log [Crash log link]");
    }

    public static String readString(InputStream stream) {
        Scanner scanner = new Scanner(stream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private static String TechnicLauncher(BufferedReader br) {
        String info = null;

        return info;
    }

    @Override
    public boolean execute(MessageEvent event) throws IOException {
        String[] args = event.getMessage().split(" ");

        ArrayList<String> info = new ArrayList<>();
        ArrayList<String> Message = new ArrayList<>();

        String message = "";
        String temp = "";

        String Raw = args[1];
        String webpage = "";

        Boolean Optifine = false;

        for (String word : event.getMessage().split(" ")) {
            if (word.matches("(https?://)?(www\\.)?(paste.feed-the-beast)\\.([A-Za-z]{2,4}|[A-Za-z]{2}\\.[A-Za-z]{2})/.*")) {
                Raw = "http://paste.feed-the-beast.com/view/raw/" + args[1].replaceAll(".*(?:view/)", "");
            }
            if (word.matches("(https?://)?(www\\.)?(pastebin)\\.([A-Za-z]{2,4}|[A-Za-z]{2}\\.[A-Za-z]{2})/.*")) {
                Raw = "http://pastebin.com/raw.php?i=" + args[1].replaceAll(".*(?:bin..om/)", "");
            }
            if (word.matches("(https?://)?(www\\.)?(hastebin)\\.([A-Za-z]{2,4}|[A-Za-z]{2}\\.[A-Za-z]{2})/.*")) {
                Raw = "http://hastebin.com/raw/" + args[1].replaceAll(".*(?:bin..om/)", "");
            }
            if (word.matches("(https?://)?(www\\.)?(paste.atlauncher)\\.([A-Za-z]{2,4}|[A-Za-z]{2}\\.[A-Za-z]{2})/.*")) {
                Raw = "http://paste.atlauncher.com/view/raw/" + args[1].replaceAll(".*(?:view/)", "");
            }
            if (word.matches("(https?://)?(www\\.)?(paste)\\.([A-Za-z]{2,4}|[A-Za-z]{2}\\.[A-Za-z]{2})/.*")) {
                Raw = "http://paste.ee/r/" + args[1].replaceAll(".*(?:p/)", "");
            }
            if (word.matches("(https?://)?(www\\.)?(gist.github)\\.([A-Za-z]{2,4}|[A-Za-z]{2}\\.[A-Za-z]{2})/.*")) {
                Raw = args[1] + "raw";
            }
            if (word.matches("(https?://)?(www\\.)?(pastie)\\.([A-Za-z]{2,4}|[A-Za-z]{2}\\.[A-Za-z]{2})/.*")) {
                Raw = "http://pastie.org/pastes/" + args[1].replaceAll(".*(?:org/)", "") + "/text";
            }
        }

        if (!Main.parser.exists()) {
            Main.parser.getParentFile().mkdirs();
            Main.parser.createNewFile();
            Utils.Parser(Main.parser);
        }

        String tmp;
        try {
            String test = JsonUtils.getStringFromFile(Main.parser.toString());
            JsonObject jsonObj = JsonUtils.getJsonObject(test);
            URL url;
            url = new URL(Raw);
            URLConnection u = url.openConnection();
            u.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17");
            BufferedReader br = new BufferedReader(new InputStreamReader(u.getInputStream()));

            if (br.readLine().contains("Technic Launcher Error Report")) {
                System.out.println("Technic Log");
            } else {
                while ((tmp = br.readLine()) != null) {
                    if (tmp.contains("This paste has been removed!")) {
                        MessageUtils.sendChannel(event, "The paste cannot be found!");
                        return true;
                    } else if(tmp.contains("CI Build#: 9999999") || tmp.contains("by greg0ree") || tmp.contains("FTB_cracked")) {
                        MessageUtils.sendChannel(event, "We " + Colors.BOLD + "DO NOT" + Colors.NORMAL + " give support for cracked launcher!");
                        return true;
                    } else if (tmp.contains("FTBLaunch starting up")) {
                        temp = Colors.BOLD + "Launcher: " + Colors.NORMAL + tmp.replaceAll(".*(?:version )|(?: Build).*", "");
                        if (!info.contains(temp)) {
                            info.add(temp);
                        }
                    } else if (tmp.contains("FTB Launcher CI Build")) {
                        temp = Colors.BOLD + "CI #" + Colors.NORMAL + tmp.replaceAll(".*(?:#: )|(?:,).*", "");
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

                    } else if (tmp.contains("Java version:")) {
                        temp = Colors.BOLD + "Java Version: " + Colors.NORMAL + tmp.replaceAll(".*(?:Java version: )", "");
                        if (!info.contains(temp) && jsonObj.get("JavaVersion").getAsBoolean()) {
                            info.add(temp);
                        }
                    } else if (tmp.contains("Is Modded: ")) {
                        temp = Colors.BOLD + "Client Brand: " + Colors.NORMAL + tmp.replaceAll("^.*?(?=[A-Z][a-z])", "").replaceAll("\\\\[.*?\\\\]", "").replaceAll(".*(?:Is Modded: Definitely; Client brand changed to )|.*(?:Is Modded: Definitely; Server brand changed to )", "");
                        if (!info.contains(temp) && jsonObj.get("Modded").getAsBoolean()) {
                            info.add(temp);
                        }
                    } else if (tmp.contains("Feed The Beast Mod Pack") || tmp.contains("Optifine OptiFine_")) {
                        System.out.println(tmp);
                        temp = Colors.BOLD + "Mods: " + Colors.NORMAL + tmp.replaceAll("^.*?(?=[A-Z][a-z])", "").replaceAll("\\\\[.*?\\\\]", "").replaceAll(".*(?:, )|mods active", "");
                        if (!info.contains(temp) && jsonObj.get("Modded").getAsBoolean()) {
                            info.add(temp);
                        }
                        if (tmp.contains("Optifine")) {
                            Optifine = true;
                        }
                    } else if (tmp.contains("Operating System: ") || tmp.contains("OS: ")) {
                        temp = Colors.BOLD + "OS: " + Colors.NORMAL + tmp.replaceAll("^.*?(?=[A-Z][a-z])", "").replaceAll("\\\\[.*?\\\\]", "").replaceAll(".*(?:Operating System: )|.*(?:OS: )", "").replaceAll("x86|x64|32-bit|64-bit", "");
                        if (!info.contains(temp) && jsonObj.get("OSName").getAsBoolean()) {
                            info.add(temp);
                        }
                    }
                    //webpage += tmp;
                }

                info.add(Colors.BOLD + "Using Optifine: " + Colors.NORMAL + String.valueOf(Optifine));

                for (String anInfo : info) {
                    Message.add(anInfo);
                }

                for (String s : Message) {
                    message += s + " | \t";
                }

                if (message.length() > 500) {
                    MessageUtils.sendChannel(event, "The log was too big and was not sent! Please retry again or disable some features in parser.json");
                    return false;
                } else if (message.isEmpty()) {
                    MessageUtils.sendChannel(event, "Could not get any information from that log!");
                } else {
                    MessageUtils.sendChannel(event, message);
                }
            }

            //Error.getProblems(webpage, event);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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
