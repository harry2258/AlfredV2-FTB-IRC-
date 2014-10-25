package com.harry2258.Alfred.listeners;

import bsh.EvalError;
import bsh.Interpreter;
import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.*;
import com.harry2258.Alfred.commands.Ignore;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;

import java.io.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.harry2258.Alfred.api.CommandRegistry.commands;

public class MessageEvent extends ListenerAdapter {
    public static PircBotX bot;
    private Config config;
    private PermissionManager manager;
    static Interpreter interpreter = new Interpreter();
    public static boolean waiting = false;

    public MessageEvent(Config conf, PermissionManager man) {
        this.config = conf;
        this.manager = man;
    }

    @Override
    public void onMessage(final org.pircbotx.hooks.events.MessageEvent event) throws Exception {
        if (PrivateMessageEvent.waiting || waiting) {
            return;
        }

        if (Main.Chat.containsKey(event.getChannel()) && Main.Chat.get(event.getChannel())) {
            Command command = CommandRegistry.getCommand("Chat");
            command.setConfig(config);
            if (!command.execute(event)) {
                event.respond(Colors.RED + "An error occurred! " + Colors.NORMAL + command.getHelp());
                return;
            }
            return;
        }

        if (event.getMessage().startsWith(config.getTrigger() + "login")) {
            Command command = CommandRegistry.getCommand("Login");
            command.setConfig(config);
            if (!command.execute(event)) {
                event.respond(Colors.RED + "An error occurred! " + Colors.NORMAL + command.getHelp());
                return;
            }
            return;
        }

        if (event.getMessage().startsWith(config.getTrigger() + "flush")) {
            Command command = CommandRegistry.getCommand("Flush");
            command.setConfig(config);
            if (!command.execute(event)) {
                event.respond(Colors.RED + "An error occurred! " + Colors.NORMAL + command.getHelp());
                return;
            }
            return;
        }

        if (Main.NotLoggedIn.contains(event.getUser().getNick())) {
            if (event.getMessage().startsWith(config.getTrigger())){
                event.getUser().send().notice("You need to be logged in with NickServ to use the bot!");
            }
            return;
        }
        String trigger = config.getTrigger();
        String[] args = event.getMessage().split(" ");
        Date date = new Date();
        final String eventuser = event.getUser().getNick();
        String Ruser;
        boolean contains = true;

        if (!Main.Login.containsKey(eventuser) && !Main.NotLoggedIn.contains(eventuser)) {
            contains = false;
            if (event.getUser().isVerified()) {
                String account = Utils.getAccount(event.getUser(), event);
                Main.Login.put(eventuser, account);
                contains = true;
            } else {
                Main.NotLoggedIn.add(eventuser);
            }
        }

        if (Main.relay.containsKey(event.getChannel())) {
            Main.relay.get(event.getChannel()).send().message("[" + event.getChannel().getName() + "] <" + eventuser + "> " + event.getMessage());
        }


        if (event.getMessage().equalsIgnoreCase("prefix")) {
            event.getUser().send().notice("The prefix is: " + config.getTrigger());
        }


        if (contains) {
            Ruser = Main.Login.get(eventuser);
        } else {
            Ruser = eventuser;
        }
        //String login = System.getProperty("user.dir") + "/Reminders/" + Ruser + ".txt";
        File reminder = null;

        if (new File(System.getProperty("user.dir") + "/Reminders/" + Ruser + ".txt").exists()) {
            reminder = new File(System.getProperty("user.dir") + "/Reminders/" + Ruser + ".txt");
            BufferedReader in = new BufferedReader(new FileReader(reminder));
            String tmp;
            event.getUser().send().notice("=========Reminders=========");
            while ((tmp = in.readLine()) != null) {
                event.getUser().send().notice(tmp);
            }
            in.close();
            reminder.delete();
        } else if (new File(System.getProperty("user.dir") + "/Reminders/" + eventuser + ".txt").exists()) {
            reminder = new File(System.getProperty("user.dir") + "/Reminders/" + eventuser + ".txt");
            BufferedReader in = new BufferedReader(new FileReader(reminder));
            String tmp;
            event.getUser().send().notice("=========Reminders=========");
            while ((tmp = in.readLine()) != null) {
                event.getUser().send().notice(tmp);
            }
            in.close();
            reminder.delete();
        }


        if (event.getMessage().startsWith(trigger) && !Ignore.ignored.contains(Main.Login.get(eventuser))) {
            String classname = Character.toUpperCase(event.getMessage().split(" ")[0].charAt(1)) + event.getMessage().split(" ")[0].substring(2).toLowerCase();
            String permission = "command." + classname.toLowerCase();

            Boolean hasPerms = manager.hasPermission(permission, event.getUser(), event.getChannel(), event);
            Boolean verified = event.getUser().isVerified();

            if (!Main.Login.containsKey(eventuser)) {
                event.getUser().send().notice("You need to be logged in with NickServ to use the bot!");
                return;
            }

            File file = new File(System.getProperty("user.dir") + "/Logs/" + event.getChannel().getName() + "/" + "CommandIssued.txt");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            try {
                if (manager.hasPermission("command.custom", event.getUser(), event.getChannel(), event)) {

                    String commandname = event.getMessage().split(" ")[0].substring(1).toLowerCase();
                    File commandfile = new File("commands/" + event.getChannel().getName() + "/" + commandname + ".cmd");
                    if (commandfile.exists() || new File("commands/" + event.getChannel().getName() + "/" + commandname.toLowerCase() + ".cmd").exists()) {
                        BufferedReader in = new BufferedReader(new FileReader(commandfile));
                        String tmp;
                        if (args.length == 2) {
                            while ((tmp = in.readLine()) != null) {
                                String temps = tmp.replaceAll("color.red", Colors.RED).replaceAll("color.green", Colors.GREEN).replaceAll("color.bold", Colors.BOLD).replaceAll("color.normal", Colors.NORMAL).replaceAll("color.darkgreen", Colors.DARK_GREEN).replaceAll("color.purple", Colors.PURPLE).replaceAll("color.darkblue", Colors.DARK_BLUE).replaceAll("color.blue", Colors.BLUE);
                                //.replaceAll("%user%", user);
                                event.getChannel().send().message(args[1] + ", " + temps);
                            }
                            in.close();
                            return;

                        } else {
                            while ((tmp = in.readLine()) != null) {
                                String temps = tmp.replaceAll("color.red", Colors.RED).replaceAll("color.green", Colors.GREEN).replaceAll("color.bold", Colors.BOLD).replaceAll("color.normal", Colors.NORMAL).replaceAll("color.darkgreen", Colors.DARK_GREEN).replaceAll("color.purple", Colors.PURPLE).replaceAll("color.darkblue", Colors.DARK_BLUE).replaceAll("color.blue", Colors.BLUE);
                                event.getChannel().send().message(temps);
                            }
                            in.close();
                            return;
                        }
                    }
                }
                if (!Main.database.isValid(5000)) {Main.database = com.harry2258.Alfred.Database.Utils.getConnection(config);}

                String name = "";
                Boolean exist = false;

                if (new File("plugins/" + classname + ".bsh").exists()) {
                    name = classname;
                    exist = true;
                } else if (new File("plugins/" + classname.toLowerCase() + ".bsh").exists()) {
                    name = classname.toLowerCase();
                    exist = true;
                }

                if (exist && hasPerms) {
                    try {
                        interpreter.set("event", event);
                        interpreter.set("bot", event.getBot());
                        interpreter.set("chan", event.getChannel());
                        interpreter.set("user", event.getUser());
                        interpreter.eval(String.format("source(\"plugins/%s.bsh\")", name));
                    } catch (EvalError ex) {
                        ex.printStackTrace();
                        event.respond(ex.getLocalizedMessage());
                        return;
                    }
                    return;
                }

                if (verified) {
                    if (commands.containsKey(classname)) {
                        if (hasPerms) {
                            Command command = CommandRegistry.getCommand(classname);
                            command.setConfig(config);
                            if (!command.execute(event)) {
                                event.respond(Colors.RED + "An error occurred! " + Colors.NORMAL + command.getHelp());
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
                            event.getUser().send().notice(config.getPermissionDenied().replaceAll("%USERNAME%", eventuser));
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
                 *
                 */
                Logger.getLogger(MessageEvent.class.getName()).log(Level.SEVERE, null, e);

            }
        }

        if (Main.URL.containsKey(event.getChannel().getName()) && Main.URL.get(event.getChannel().getName()).equalsIgnoreCase("youtube")) {
            for (String word : event.getMessage().split(" ")) {
                if (Utils.isUrl(word) && word.matches("(https?://)?(www\\.)?(youtube|yimg|youtu)\\.([A-Za-z]{2,4}|[A-Za-z]{2}\\.[A-Za-z]{2})/(watch\\?v=)?[A-Za-z0-9\\-_]{6,12}(&[A-Za-z0-9\\-_]{1,}=[A-Za-z0-9\\-_]{1,})*")) {
                    event.getChannel().send().message("[" + Colors.RED + "YouTube" + Colors.NORMAL + "] " + Utils.getYoutubeInfo(word));
                }
            }

        }

        if (Main.URL.containsKey(event.getChannel().getName()) && Main.URL.get(event.getChannel().getName()).equalsIgnoreCase("all")) {
            for (String word : event.getMessage().split(" ")) {
                if (Utils.isUrl(word) && !word.equals(config.getTrigger() + "setcmd") && !word.matches("(https?://)?(www\\.)?(youtube|yimg|youtu)\\.([A-Za-z]{2,4}|[A-Za-z]{2}\\.[A-Za-z]{2})/(watch\\?v=)?[A-Za-z0-9\\-_]{6,12}(&[A-Za-z0-9\\-_]{1,}=[A-Za-z0-9\\-_]{1,})*")) {
                    event.getChannel().send().message("[" + Colors.RED + event.getUser().getNick() + Colors.NORMAL + "] " + Utils.getTitle(word));
                }
                if (Utils.isUrl(word) && !word.equals(config.getTrigger() + "ping") && word.matches("(https?://)?(www\\.)?(youtube|yimg|youtu)\\.([A-Za-z]{2,4}|[A-Za-z]{2}\\.[A-Za-z]{2})/(watch\\?v=)?[A-Za-z0-9\\-_]{6,12}(&[A-Za-z0-9\\-_]{1,}=[A-Za-z0-9\\-_]{1,})*")) {
                    event.getChannel().send().message("[" + Colors.RED + "YouTube" + Colors.NORMAL + "] " + Utils.getYoutubeInfo(word));

                }
            }
        }


        if (event.getMessage().equalsIgnoreCase("Im better than alfred") || event.getMessage().equalsIgnoreCase("I'm better than alfred")) {
            event.getChannel().send().message(eventuser + ", It's good to dream big");
        }

    }
}
