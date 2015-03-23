package com.harry2258.Alfred;

import com.harry2258.Alfred.Database.Create;
import com.harry2258.Alfred.Misc.*;
import com.harry2258.Alfred.api.*;
import com.harry2258.Alfred.json.Perms;
import com.harry2258.Alfred.listeners.*;
import com.harry2258.Alfred.runnables.ChatSocketListener;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.reflections.Reflections;
import org.slf4j.impl.SimpleLogger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.harry2258.Alfred.api.JsonUtils.writeJsonFile;

//import com.harry2258.Alfred.runnables.WebServ;

/**
 * Hello world!
 */
public class Main {
    public static long startup = 0;
    public static PircBotX bot;
    public static boolean SafeStop = false;

    public static Map<String, Perms> map = new HashMap<>(); //Channel perms, <Channel Name, Permission Json>
    public static Map<String, String> Login = new HashMap<>(); //Login names - Less WHOIS, <Nickname, Nickserver Name> | Many to one pretty much.
    public static Set<String> NotLoggedIn = new HashSet<>(); //People who aren't logged in so we don't have to WHOIS and get a bad response.

    public static HashMap<Channel, Channel> relay = new HashMap<>(); //For Spy command, <Channel being spied on, Channel said messages are being relayed to>
    public static HashMap<String, String> URL = new HashMap<>(); //URL scanning, <Channel, All/Youtube/none>
    public static HashMap<Channel, Boolean> Chat = new HashMap<>(); //Auto-chatting? Why not.

    public static File jsonFilePath = new File(System.getProperty("user.dir") + "/exec.json");
    public static File parser = new File(System.getProperty("user.dir") + "/parser.json");
    public static File globalperm = new File(System.getProperty("user.dir") + "/global.json");
    public static File edgesjsonfile = new File(System.getProperty("user.dir") + "/edges.json");

    public static String version = "";

    public static Connection database;


    public static void main(String[] args) {

        while (!SafeStop) {
            startup = System.currentTimeMillis();
            System.setProperty(SimpleLogger.SHOW_DATE_TIME_KEY, "true");
            System.setProperty(SimpleLogger.DATE_TIME_FORMAT_KEY, "[MM/dd HH:mm:ss]");
            System.out.println("Starting");
            try {
                final Config config = new Config();
                PermissionManager manager = new PermissionManager(config);
                System.out.println("Loading and registering commands");
                try {
                    if (!config.UseDatabase()) {
                        config.load();
                    } else {
                        System.out.println("Connecting to " + config.getDatabaseHost());
                        database = DriverManager.getConnection("jdbc:mysql://" + config.getDatabaseHost() + "/" + config.getDatabase(), config.getDatabaseUser(), config.getDatabasePass());
                        config.loadDatabase(database);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(0);
                }

                //Creates Exec.json
                if (!jsonFilePath.exists()) {
                    jsonFilePath.createNewFile();
                    String jsonString = "{\"Perms\":{\"Exec\":[\"batman\", \"progwml6\"]}}";
                    writeJsonFile(jsonFilePath, jsonString);
                }

                //creates Parse.Json for Log command
                if (!parser.exists()) {
                    parser.getParentFile().mkdirs();
                    parser.createNewFile();
                    Utils.Parser(parser);
                }

                //Creates Global Everyone permission
                if (!globalperm.exists()) {
                    Utils.Geveryone(globalperm);
                }

                //Creates Edges.json for ChStatus command
                if (!edgesjsonfile.exists()) {
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
                Configuration.Builder builder = new Configuration.Builder();
                builder.setName(config.getBotNickname());
                builder.setRealName(config.getBotUsername());
                builder.setLogin(config.getBotIdent());
                builder.setFinger(config.getCtcpFinger());
                builder.setEncoding(Charset.isSupported("UTF-8") ? Charset.forName("UTF-8") : Charset.defaultCharset());
                builder.setNickservPassword(config.getBotPassword());
                builder.setAutoReconnect(config.isAutoReconnectServer());
                builder.setAutoReconnectDelay(5000);
                builder.setVersion("2.4.1");
                builder.setMessageDelay(500);
                builder.addServer(config.getServerHostame(), Integer.parseInt(config.getServerPort()));
                builder.setMaxLineLength(450);

                //Gotta listen to 'em
                builder.getListenerManager().addListener(new MessageEvent(config, manager));
                builder.getListenerManager().addListener(new InviteEvent(config, manager));
                builder.getListenerManager().addListener(new JoinEvent(config, manager));
                builder.getListenerManager().addListener(new BanEvent(config, manager));
                builder.getListenerManager().addListener(new NickChangeEvent(config, manager));
                builder.getListenerManager().addListener(new ActionEvent(config, manager));
                builder.getListenerManager().addListener(new PartEvent(config, manager));
                builder.getListenerManager().addListener(new KickEvent(config, manager));
                builder.getListenerManager().addListener(new Disconnect(config, manager));
                builder.getListenerManager().addListener(new PrivateMessageEvent(config, manager));


                System.out.println("------Permissions------");
                if (!config.UseDatabase()) {

                    for (String channel : config.getChannels()) {
                        File file = new File(System.getProperty("user.dir") + "/perms/" + channel.toLowerCase() + "/" + "perms.json");
                        String Jsonfile = System.getProperty("user.dir") + "/perms/" + channel.toLowerCase() + "/" + "perms.json";
                        if (!file.exists()) {
                            System.out.println("Creating perms.json for " + channel);
                            JsonUtils.createJsonStructure(file);
                        }
                        String perms = JsonUtils.getStringFromFile(Jsonfile);
                        Perms p = JsonUtils.getPermsFromString(perms);
                        map.put(channel.toLowerCase(), p);
                        System.out.println("Loaded perms for " + channel);
                        builder.addAutoJoinChannel(channel);
                    }
                } else {
                    try {
                        //Getting out Auto-join channels
                        PreparedStatement stmt = database.prepareStatement("SELECT Channel  FROM `Rejoin_Channels`");
                        ResultSet rs = stmt.executeQuery();

                        while (rs.next()) {
                            builder.addAutoJoinChannel(rs.getString("Channel"));
                            if (!Create.AddChannel(rs.getString("Channel"), database))
                                System.out.println("Could not create permissions for " + rs.getString("Channel"));
                        }

                        //Getting Permissions and settings for said channels
                        stmt = database.prepareStatement("SELECT * FROM `Channel_Permissions`");
                        rs = stmt.executeQuery();
                        while (rs.next()) {
                            URL.put(rs.getString("Channel"), rs.getString("URL"));
                        }

                        PreparedStatement stmt3 = database.prepareStatement("SELECT a.Channel, a.Permission, a.URL FROM `Channel_Permissions` a, `Rejoin_Channels` b WHERE a.Channel = b.Channel;");
                        ResultSet rs3 = stmt3.executeQuery();
                        while (rs3.next()) {
                            String channel = rs3.getString("Channel");
                            Main.URL.put(channel, rs3.getString("URL"));
                            Perms p = JsonUtils.getPermsFromString(rs3.getString("Permission"));
                            Main.map.put(channel.toLowerCase(), p);
                            System.out.println("Loaded setting for channel: " + channel);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("-----------------------");
                PircBotX bot = new PircBotX(builder.buildConfiguration());
                System.out.println("Starting bot...");
                version = bot.getConfiguration().getVersion();
                new Thread(new ChatterBot(bot)).start();
                if (config.isEnableChatSocket()) {
                    new Thread(new ChatSocketListener(bot, config.getChatSocketPort())).start();
                }
                if (config.isTwitterEnabled()) {
                    new Thread(new Twitter(bot)).start();
                }
                if (config.isRedditEnabled()) {
                    new Thread(new Reddit(bot)).start();
                }
                if (config.UpdaterChecker()) {
                    new Thread(new Update(bot, config)).start();
                }
                //new Thread(new RecentChanges(bot)).start();

                bot.startBot();

            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Help! I've crashed and I can't recover!");
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static boolean startBot(PircBotX bot) {
        try {
            do {
                bot.startBot();
            } while (SafeStop);
        } catch (IOException | IrcException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
