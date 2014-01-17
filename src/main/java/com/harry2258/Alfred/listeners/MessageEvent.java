package com.harry2258.Alfred.listeners;

import com.harry2258.Alfred.Main;
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
        if (!Main.Login.containsKey(event.getUser().getNick())) {
            if (event.getUser().isVerified()) {
                String account = Utils.getAccount(event.getUser(), event);
                Main.Login.put(event.getUser().getNick(), account);
                System.out.println(event.getUser().getNick() + " was added to the HashMap");
            }
        }
        if (event.getMessage().startsWith(trigger) && !Ignore.ignored.contains(Main.Login.get(event.getUser().getNick()))) {
            if (event.getUser().isVerified()) {
                if (event.getMessage().startsWith(config.getTrigger() + "login")) {
                    String classname = "Login";
                    Command command = CommandRegistry.getCommand(classname);
                    command.setConfig(config);
                }

                if (Main.Login.containsKey(event.getUser().getNick())) {
                    File file = new File(System.getProperty("user.dir") + "/Logs/" + event.getChannel().getName() + "/" + "CommandIssued.txt");
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
                                        event.getChannel().send().message(Colors.RED + "An error occurred! " + Colors.NORMAL + command.getHelp());
                                        return;
                                    }

                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 1; i < args.length; i++) {
                                        sb.append(args[i]).append(" ");
                                    }

                                    try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
                                        out.println("[" + date + "]" + " " + event.getUser().getNick() + ": " + "[" + classname + "] " + sb);
                                        out.close();
                                    }

                                } else {
                                    event.getUser().send().notice(config.getPermissionDenied().replaceAll("%USERNAME%", event.getUser().getNick()));
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
                    event.getUser().send().notice("Please log into the bot using " + config.getTrigger() + "login");
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

        String newuser = Utils.getAccount(event.getUser(), event);
        File reminder = new File(System.getProperty("user.dir") + "/Reminders/" + newuser + ".txt");
        if (reminder.exists()) {
            BufferedReader in = new BufferedReader(new FileReader(reminder));
            String tmp;
            event.getUser().send().notice("=========Reminders=========");
            while ((tmp = in.readLine()) != null) {
                event.getUser().send().notice(tmp);
            }
            in.close();
            reminder.delete();
        }

    }
}
