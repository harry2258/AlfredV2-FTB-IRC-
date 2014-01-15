package com.harry2258.Alfred.listeners;

import com.harry2258.Alfred.api.*;
import com.harry2258.Alfred.commands.Ignore;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;

import java.io.*;
import java.util.Date;

import static com.harry2258.Alfred.api.CommandRegistry.commands;

public class MessageEvent extends ListenerAdapter {
    public static PircBotX bot;
    private Config config;
    private PermissionManager manager;

    public MessageEvent(Config conf, PermissionManager man) {
        this.config = conf;
        this.manager = man;
    }

    @Override
    public void onMessage(org.pircbotx.hooks.events.MessageEvent event) throws Exception {
        String trigger = config.getTrigger();
        String[] args = event.getMessage().split(" ");
        Date date = new Date();
        if (event.getMessage().startsWith(trigger) && !Ignore.ignored.contains(Utils.getAccount(event.getUser(), event))) {
            if (event.getUser().isVerified()) {
                File file = new File("C:/Users/Home/Dropbox/Logs/" + event.getChannel().getName() + "/" + "CommandIssued.txt");
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }

                try {
                    String commandname = event.getMessage().split(" ")[0].substring(1).toLowerCase();
                    File commandfile = new File("commands/" + event.getChannel().getName() + "/" + commandname + ".cmd");
                    String mod = "command.custom";
                    if (commandfile.exists() && manager.hasPermission(mod, event.getUser(), event.getChannel(), event)) {
                        BufferedReader in = new BufferedReader(new FileReader(commandfile));
                        String tmp;
                        if (args.length == 2) {
                            User user = event.getBot().getUserChannelDao().getUser(args[1]);
                            while ((tmp = in.readLine()) != null) {
                                String temps = tmp.replaceAll("color.red", Colors.RED).replaceAll("color.green", Colors.GREEN).replaceAll("color.bold", Colors.BOLD).replaceAll("color.normal", Colors.NORMAL).replaceAll("color.darkgreen", Colors.DARK_GREEN).replaceAll("color.purple", Colors.PURPLE).replaceAll("color.darkgreen", Colors.DARK_GREEN);
                                user.send().notice(temps);
                            }
                            in.close();
                            return;

                        } else {
                            while ((tmp = in.readLine()) != null) {
                                String temps = tmp.replaceAll("color.red", Colors.RED).replaceAll("color.green", Colors.GREEN).replaceAll("color.bold", Colors.BOLD).replaceAll("color.normal", Colors.NORMAL).replaceAll("color.darkgreen", Colors.DARK_GREEN).replaceAll("color.purple", Colors.PURPLE).replaceAll("color.darkgreen", Colors.DARK_GREEN);
                                event.getChannel().send().message(temps);
                            }
                            in.close();
                            return;
                        }
                    }
                    String classname = Character.toUpperCase(event.getMessage().split(" ")[0].charAt(1)) + event.getMessage().split(" ")[0].substring(2).toLowerCase();
                    String permission = "command." + classname.toLowerCase();
                    if (event.getUser().isVerified()) {
                        if (commands.containsKey(classname)) {
                            if (manager.hasPermission(permission, event.getUser(), event.getChannel(), event)) {
                                Command command = CommandRegistry.getCommand(classname);
                                command.setConfig(config);
                                if (!command.execute(event)) {
                                    event.getChannel().send().message(Colors.RED + "An error occurred! " + command.getHelp());
                                    return;
                                }

                            } else {
                                event.getUser().send().notice(config.getPermissionDenied().replaceAll("%USERNAME%", event.getUser().getNick()));
                            }

                            StringBuilder sb = new StringBuilder();
                            for (int i = 2; i < args.length; i++) {
                                sb.append(args[i]).append(" ");
                            }

                            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
                                out.println("[" + date + "]" + " " + event.getUser().getNick() + ": " + "[" + classname + "] " + sb);
                                out.close();
                            }

                        } else {
                            event.getUser().send().notice("There is no command by that name!");
                        }
                    } else {
                        event.getUser().send().notice("You need to be logged in with NickServ to use the bot!");
                    }
                } catch (Exception e) {
                /*
                 * Unknown command
                 * >implying i give a fuck
                 * Logger.getLogger(MessageEvent.class.getName()).log(Level.SEVERE, null, e);
                 */
                }
            } else {
                event.getUser().send().notice("You need to be logged in with NickServ to use the bot!");
            }
        }
//        for (String word : event.getMessage().split(" ")) {
//            if (Utils.isUrl(word)) {
//                event.getChannel().send().message(event.getUser().getNick() + "'s URL: " + Utils.getTitle(word));
//            }
//        }

            /*
        if (event.getMessage().contains("Changelog") || event.getMessage().contains("changelog")) {
            event.getChannel().send().message(event.getUser().getNick() + ", Changelogs for all modpacks are here: http://is.gd/3NH0cH");
        }
        */

    }
}
