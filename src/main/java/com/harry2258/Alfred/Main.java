package com.harry2258.Alfred;

import com.harry2258.Alfred.Misc.Reddit;
import com.harry2258.Alfred.Misc.Twitter;
import com.harry2258.Alfred.Misc.Update;
import com.harry2258.Alfred.api.*;
import com.harry2258.Alfred.listeners.*;
import com.harry2258.Alfred.runnables.ChatSocketListener;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.reflections.Reflections;
import org.slf4j.impl.SimpleLogger;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;
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
    public static HashMap<String, String> URL = new HashMap<>();
    public static PropertiesConfiguration customcmd;
    public static File cmd = new File(System.getProperty("user.dir") + "/parser.json");
    public static File globalperm = new File(System.getProperty("user.dir") + "/global.json");
    public static File edgesjsonfile = new File(System.getProperty("user.dir") + "/edges.json");
    public static List<String> users = new ArrayList<String>();
    public static String version = "";

    public static void main(String[] args) {
        System.setProperty(SimpleLogger.SHOW_DATE_TIME_KEY, "true");
        System.setProperty(SimpleLogger.DATE_TIME_FORMAT_KEY, "[HH:mm:ss]");
        System.setProperty(SimpleLogger.SHOW_THREAD_NAME_KEY, "false");
        System.setProperty(SimpleLogger.LEVEL_IN_BRACKETS_KEY, "true");
        System.setProperty(SimpleLogger.SHOW_LOG_NAME_KEY, "false");
        System.out.println("Starting");
        try {
            startup = System.currentTimeMillis();
            final Config config = new Config();
            PermissionManager manager = new PermissionManager(config);
            System.out.println("Loading and registering commands");
            config.load();

            //Creates Exec.json
            if (!jsonFilePath.exists()) {
                jsonFilePath.createNewFile();
                String jsonString = "{\"Perms\":{\"Exec\":[\"batman\", \"progwml6\"]}}";
                writeJsonFile(jsonFilePath, jsonString);
            }

            //creates Parse.Json for Log command
            if (!cmd.exists()) {
                cmd.getParentFile().mkdirs();
                cmd.createNewFile();
                Utils.Parser(cmd);
            }

            //Creates Global Everyone permission
            if (!globalperm.exists()) {
                globalperm.createNewFile();
                Utils.Geveryone(globalperm);
            }

            //Creates Edges.json for ChStatus command
            if (!edgesjsonfile.exists()) {
                edgesjsonfile.createNewFile();
                Utils.edges(edgesjsonfile);
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
            builder.setVersion("2.1.9");
            builder.setServer(config.getServerHostame(), Integer.parseInt(config.getServerPort()), config.getServerPassword());

            //Gotta listen to 'em
            builder.getListenerManager().addListener(new MessageEvent(config, manager));
            builder.getListenerManager().addListener(new InviteEvent(config, manager));
            builder.getListenerManager().addListener(new JoinEvent(config, manager));
            builder.getListenerManager().addListener(new BanEvent(config, manager));
            builder.getListenerManager().addListener(new NickChangeEvent(config, manager));
            builder.getListenerManager().addListener(new PartEvent(config, manager));
            builder.getListenerManager().addListener(new ActionEvent(config, manager));
            builder.getListenerManager().addListener(new KickEvent(config, manager));

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
            version = bot.getConfiguration().getVersion();
            if (config.isEnableChatSocket()) {
                new Thread(new ChatSocketListener(bot, config.getChatSocketPort())).start();
            }
            if (config.isEnabledTwitter()) {
                new Thread(new Twitter(bot)).start();
            }
            if (config.isRedditEnabled()) {
                new Thread(new Reddit(bot)).start();
            }
            if (config.UpdaterChecker()) {
                new Thread(new Update(bot, config)).start();
            }

            bot.startBot();
            System.out.println("Shutting down");
            if (config.isEnabledTwitter()) {
                Twitter.kill();
            }
            if (config.isRedditEnabled()) {
                Reddit.kill();
            }
            if (config.UpdaterChecker()) {
                Update.kill();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
