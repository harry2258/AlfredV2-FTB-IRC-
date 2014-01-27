package com.harry2258.Alfred.listeners;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.*;
import com.harry2258.Alfred.commands.Ignore;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
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
    public void onMessage(final org.pircbotx.hooks.events.MessageEvent event) throws Exception {
        String trigger = config.getTrigger();
        String[] args = event.getMessage().split(" ");
        Date date = new Date();
        final String eventuser = event.getUser().getNick();

        if (!Main.Login.containsKey(eventuser)) {
            if (event.getUser().isVerified()) {
                System.out.println("Adding user to HashMap");
                String account = Utils.getAccount(event.getUser(), event);
                Main.Login.put(eventuser, account);
                System.out.println(eventuser + " was added to the HashMap");
            }
        }

        if (Main.relay.containsKey(event.getChannel())) {
            Main.relay.get(event.getChannel()).send().message("[" + event.getChannel().getName() + "] <" + eventuser + "> " + event.getMessage());
        }

        if (event.getMessage().startsWith(trigger) && !Ignore.ignored.contains(Main.Login.get(eventuser))) {
            if (event.getMessage().startsWith(config.getTrigger() + "login")) {
                if (event.getUser().isVerified()) {
                    String classname = "Login";
                    Command command = CommandRegistry.getCommand(classname);
                    command.setConfig(config);
                    if (!command.execute(event)) {
                        event.getChannel().send().message(Colors.RED + "An error occurred! " + Colors.NORMAL + command.getHelp());
                        return;
                    }
                    return;
                } else {
                    event.getUser().send().notice("You need to be logged in with NickServ!");
                }
            }


            if (Main.Login.containsKey(eventuser)) {
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
                            String user = args[1];
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
                 * Logger.getLogger(MessageEvent.class.getName()).log(Level.SEVERE, null, e);
                 */
                }
            } else {
                event.getUser().send().notice("You need to be logged in with NickServ to use the bot!");
            }
        }

        if (Main.URL.contains(event.getChannel().getName())) {
            for (String word : event.getMessage().split(" ")) {
                if (Utils.isUrl(word) && !word.toLowerCase().contains("youtube") && !word.toLowerCase().contains("youtu.be") && !word.equals(config.getTrigger() + "setcmd")) {
                    event.getChannel().send().message("[" + Colors.RED + event.getUser().getNick() + Colors.NORMAL + "] " + Utils.getTitle(word));
                }

                if (Utils.isUrl(word) && word.toLowerCase().contains("youtube") || word.toLowerCase().contains("youtu.be") && !word.equals(config.getTrigger() + "ping")) {
                    event.getChannel().send().message("[" + Colors.RED + "YouTube" + Colors.NORMAL + "] " + Utils.getYoutubeInfo(word));

                }
            }

        }


        String Ruser = null;

        if (Main.Login.containsKey(eventuser)) {
            Ruser = Main.Login.get(eventuser);
        } else {
            Ruser = eventuser;
        }

        String login = System.getProperty("user.dir") + "/Reminders/" + Ruser + ".txt";
        File reminder = new File(login);
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

        //Copy from old Alphabot :3
        /*
        new Thread(new Runnable(){
            @Override
            public void run(){
                //System.out.println("entered run");
                try{
                    if(!Main.users.contains(eventuser)){
                        if(Main.violation.containsKey(eventuser)){
                            Main.violation.put(eventuser, (Integer) Main.violation.get(eventuser) + 1);
                        }else{
                            Main.violation.put(eventuser, 0);
                        }
                        Main.users.add(eventuser);
                        System.out.println("Added to list.");
                        Thread.sleep(1000);
                        System.out.println("Removed from list.");
                        Main.users.remove(eventuser);
                    }else{
                        if((Integer) Main.violation.get(eventuser) > 3){
                            event.getChannel().send().kick(event.getUser(), "Calm your tits bro");
                            Main.violation.put(eventuser, 0);
                            return;
                        }
                        System.out.println("User already in list. muting");
                        event.getUser().send().mode("+q");
                        Main.users.remove(eventuser);
                        System.out.println("muted.");
                        event.getUser().send().notice("You've been muted temporarily for spam.");

                        Thread.sleep(1000 * 10);
                        System.out.println("unmuted");
                        event.getUser().send().mode("-q");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        */

    }
}
