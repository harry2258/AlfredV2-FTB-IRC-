package com.harry2258.Alfred;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.CommandRegistry;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.reflections.Reflections;
import org.slf4j.impl.SimpleLogger;

import java.nio.charset.Charset;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class Main {
    public static long startup = 0;
    public static PircBotX bot;

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
            Reflections reflections = new Reflections("com.harry2258.Alfred.commands");
            Set<Class<? extends Command>> subTypes = reflections.getSubTypesOf(Command.class);
            for (Class c : subTypes) {
                Command cmd = CommandRegistry.getCommand(c.getSimpleName());
                System.out.println("Registered command " + cmd.getName() + " as key " + c.getSimpleName());
                CommandRegistry.register(cmd);
            }
            //i have no idea what this is, but IDEA wouldn't shut the fuck up about changing it.
            Configuration.Builder<PircBotX> builder = new Configuration.Builder<PircBotX>();
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
            for (String channel : config.getChannels()) {
                builder.addAutoJoinChannel(channel);
            }
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