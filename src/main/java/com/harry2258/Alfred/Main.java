package com.harry2258.Alfred;

import com.harry2258.Alfred.api.*;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.reflections.Reflections;
import org.slf4j.impl.SimpleLogger;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.harry2258.Alfred.api.JsonUtils.writeJsonFile;

/**
 * Hello world!
 */
public class Main {
    public static long startup = 0;
    public static PircBotX bot;
    public static File jsonFilePath = new File(System.getProperty("user.dir") + "/exec.json");
    public static Map<String, String> map = new HashMap<>();
    public static Map<String, String> Login = new HashMap<>();
    public static HashMap<Channel, Channel> relay = new HashMap<>();
    public static ArrayList<String> URL = new ArrayList<>();
    public static PropertiesConfiguration customcmd;
    public static File cmd = new File(System.getProperty("user.dir") + "/parser.yml");

    public static void main(String[] args) {
        System.setProperty(SimpleLogger.SHOW_DATE_TIME_KEY, "true");
        System.setProperty(SimpleLogger.DATE_TIME_FORMAT_KEY, "[HH:mm:ss]");
        System.setProperty(SimpleLogger.SHOW_THREAD_NAME_KEY, "false");
        System.setProperty(SimpleLogger.LEVEL_IN_BRACKETS_KEY, "true");
        System.setProperty(SimpleLogger.SHOW_LOG_NAME_KEY, "false");
        System.out.println("Starting");
        try {
            startup = System.currentTimeMillis();
            Config config = new Config();
            PermissionManager manager = new PermissionManager(config);
            System.out.println("Loading and registering commands");
            config.load();

            if (!jsonFilePath.exists()) {
                jsonFilePath.createNewFile();
                String jsonString = "{\"Perms\":{\"Exec\":[\"batman\", \"progwml6\"]}}";
                writeJsonFile(jsonFilePath, jsonString);
            }

            if(cmd.exists()){
                customcmd.load(cmd);
            }else{
                cmd.getParentFile().mkdirs();
                cmd.createNewFile();
                Utils.Parser(cmd);
            }

            Reflections reflections = new Reflections("com.harry2258.Alfred.commands");
            Set<Class<? extends Command>> subTypes = reflections.getSubTypesOf(Command.class);
            for (Class c : subTypes) {
                Command cmd = CommandRegistry.getCommand(c.getSimpleName());
                System.out.println("Registered command " + cmd.getName() + " as key " + c.getSimpleName());
                CommandRegistry.register(cmd);
            }
            //i have no idea what this is, but IDEA wouldn't shut the fuck up about changing it.
            Configuration.Builder<PircBotX> builder = new Configuration.Builder<>();
            builder.setName(config.getBotNickname());
            builder.setRealName(config.getBotUsername());
            builder.setLogin(config.getBotIdent());
            builder.setFinger(config.getCtcpFinger());
            builder.setEncoding(Charset.isSupported("UTF-8") ? Charset.forName("UTF-8") : Charset.defaultCharset());
            builder.setNickservPassword(config.getBotPassword());
            builder.setVersion("Alfred v2.0");
            builder.setServer(config.getServerHostame(), Integer.parseInt(config.getServerPort()), config.getServerPassword());
            builder.getListenerManager().addListener(new com.harry2258.Alfred.listeners.MessageEvent(config, manager));
            builder.getListenerManager().addListener(new com.harry2258.Alfred.listeners.InviteEvent(config, manager));
            builder.getListenerManager().addListener(new com.harry2258.Alfred.listeners.JoinEvent(config, manager));
            builder.getListenerManager().addListener(new com.harry2258.Alfred.listeners.NickChangeEvent(config, manager));
            builder.getListenerManager().addListener(new com.harry2258.Alfred.listeners.PartEvent(config, manager));
            builder.getListenerManager().addListener(new com.harry2258.Alfred.listeners.ActionEvent(config, manager));


            System.out.println("------Permissions------");
            for (String channel : config.getChannels()) {
                File file = new File(System.getProperty("user.dir") + "/Perms/" + channel.toLowerCase() + "/" + "perms.json");
                String Jsonfile = System.getProperty("user.dir") + "/Perms/" + channel.toLowerCase() + "/" + "perms.json";
                if (!file.exists()) {
                    System.out.println("Creating perms.json for " + channel);
                    JsonUtils.createJsonStructure(file);
                }
                String perms = JsonUtils.getStringFromFile(Jsonfile);
                map.put(channel.toLowerCase(), perms);
                System.out.println("Loaded perms for " + channel);
                builder.addAutoJoinChannel(channel);
            }

            System.out.println("-----------------------");
            PircBotX bot = new PircBotX(builder.buildConfiguration());
            System.out.println("Starting bot...");
            bot.startBot();
            System.out.println("Shutting down");
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}