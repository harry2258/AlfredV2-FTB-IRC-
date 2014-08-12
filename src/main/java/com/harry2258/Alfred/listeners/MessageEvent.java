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

import static com.harry2258.Alfred.api.CommandRegistry.commands;

public class MessageEvent extends ListenerAdapter {
    public static PircBotX bot;
    private Config config;
    private PermissionManager manager;
    static Interpreter interpreter = new Interpreter();

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
        String Ruser;

        if (!Main.Login.containsKey(eventuser)) {
            if (event.getUser().isVerified()) {
                String account = Utils.getAccount(event.getUser(), event);
                Main.Login.put(eventuser, account);
                System.out.println(eventuser + " was added to the HashMap");
            }
        }
        if (Main.relay.containsKey(event.getChannel())) {
            Main.relay.get(event.getChannel()).send().message("[" + event.getChannel().getName() + "] <" + eventuser + "> " + event.getMessage());
        }

        if (event.getMessage().equalsIgnoreCase("prefix")) {
            event.getUser().send().notice("The prefix is: " + config.getTrigger());
        }

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

        if (event.getMessage().startsWith(trigger) && !Ignore.ignored.contains(Main.Login.get(eventuser))) {
            if (event.getMessage().equalsIgnoreCase(config.getTrigger() + "login")) {
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
                    if (manager.hasPermission("command.custom", event.getUser(), event.getChannel(), event)) {
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

                    String classname = Character.toUpperCase(event.getMessage().split(" ")[0].charAt(1)) + event.getMessage().split(" ")[0].substring(2).toLowerCase();
                    String permission = "command." + classname.toLowerCase();
                    String name = "";
                    Boolean exist = false;
                    if (new File("plugins/" + classname + ".bsh").exists()) {
                        name = classname;
                        exist = true;
                    } else if (new File("plugins/" + classname.toLowerCase() + ".bsh").exists()) {
                        name = classname.toLowerCase();
                        exist = true;
                    }

                    if (exist && manager.hasPermission(permission, event.getUser(), event.getChannel(), event)) {
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

        if (Main.URL.containsKey(event.getChannel().getName()) && Main.URL.get(event.getChannel().getName()).equalsIgnoreCase("youtube")) {
            for (String word : event.getMessage().split(" ")) {
                /*
                if (Utils.isUrl(word) && word.toLowerCase().contains("youtube") || word.toLowerCase().contains("youtu.be") && !word.equals(config.getTrigger() + "ping")) {
                    event.getChannel().send().message("[" + Colors.RED + "YouTube" + Colors.NORMAL + "] " + Utils.getYoutubeInfo(word));
                }
                */
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
        /*
        if (event.getMessage().matches("^(s|tr)/([^/\\\\]*(?:\\\\.)?)+/(.*)(/[gi]*)?$")) {
            if (manager.hasPermission("command.regex", event.getUser(), event.getChannel(),event)) {
                debugEx = "";
                List<Match> match = Util.getAllMatches("^(s|tr)/((?:[^/\\\\]*(?:\\\\.)?)+)/((?:[^/]*(?:\\\\/)*)*)(?:/([gi]*))?$", event.getMessage());
                Match m = match.get(0);
                String type = m.getGroups()[0];
                String regex = m.getGroups()[1].replace("\\/", "/");
                String replacement = m.getGroups()[2].replace("\\/", "/");
                String flags = m.getGroups()[3];
                debugEx += String.format("Regex: '%s'\nReplacement: '%s'\nFlags: '%s'\nMode: '%s'", regex, replacement, flags, type);
                if (flags == null)
                    flags = "";
                String result = null;
                if (type.equals("s")) {
                    regex = "(?:" + regex + ")";
                    if (flags.contains("i"))
                        regex = "(?i)" + regex;
                    Pattern pattern1 = null;
                    Pattern pattern2 = null;
                    try {
                        pattern1 = Pattern.compile("^.*" + regex + ".*$");
                        pattern2 = Pattern.compile(regex);

                        PlaybackHandler h = PlaybackRegistry.getHandler(event.getChannel());
                        Iterator<Message> it = h.getMessageIterator(event.getChannel(), -1);
                        while (it.hasNext()) {
                            Message msg_ = it.next();
                            debugEx += "\n" + msg_.toString();
                            if (msg_.isIgnored() || (!(msg_ instanceof TextMessage))) continue;
                            TextMessage msg = (TextMessage) msg_;
                            String mmsg = msg.getMessage();
                            if (mmsg.equals(event.getMessage()) || mmsg.matches("^s/([^/\\\\]*(?:\\\\.)?)+/(.*)(/[gi]*)?$")) continue;
                            Matcher matcher1 = pattern1.matcher(mmsg);
                            Matcher matcher2 = pattern2.matcher(mmsg);
                            //matcher.useAnchoringBounds(false);
                            if (matcher1.matches()) {
                                if (flags.contains("g"))
                                    result = matcher2.replaceAll(replacement);//msg.getMessage().replaceAll(regex, replacement);
                                else
                                    result = matcher2.replaceFirst(replacement);
                                TextMessage msg2 = msg.clone();
                                msg2.setMessage(result);
                                h.appendMessage(event.getChannel(), msg2);
                                String res = msg2.formatLine();
                                access.getBot().sendMessage(event.getChannel(), res);
                                debugEx += String.format(" => %s\n\nResult: '%s'", result, res);
                                break;
                            }
                        }
                        if (result == null)
                            access.getBot().sendNotice(event.getUser(), "No match found");

                    } catch (PatternSyntaxException e) {
                        access.getBot().sendNotice(event.getUser(), "Invalid regex: " + e.getMessage().split("\n")[0]);
                    }
                } else {
                    try {
                        TransliterationRegex pattern = new TransliterationRegex(regex, replacement);
                        PlaybackHandler h = PlaybackRegistry.getHandler(event.getChannel());
                        Iterator<Message> it = h.getMessageIterator(event.getChannel(), -1);
                        while (it.hasNext()) {
                            Message msg_ = it.next();
                            debugEx += "\n" + msg_.toString();
                            if (msg_.isIgnored() || (!(msg_ instanceof TextMessage))) continue;
                            TextMessage msg = (TextMessage) msg_;
                            String mmsg = msg.getMessage();
                            if (mmsg.equals(event.getMessage()) || mmsg.matches("^s/([^/\\\\]*(?:\\\\.)?)+/(.*)(/[gi]*)?$")) continue;

                            String res = pattern.applyTo(mmsg);
                            if (!res.equals(mmsg)) {
                                TextMessage newOne = msg.clone();
                                newOne.setMessage(res);
                                h.appendMessage(event.getChannel(), newOne);
                                String result_data = newOne.formatLine();
                                event.getChannel().send().notice( result_data);
                                debugEx += String.format(" => %s\n\nResult: '%s'", res, result_data);
                                break;
                            }

                        }
                    } catch (Exception e) {
                        event.getUser().send().notice("Invalid regex: " + e.getMessage().split("\n")[0]);
                    }

                }
                /*try {
                    String name = "regex_" + System.currentTimeMillis() + ".dmp";
                    File f = new File(name);
                    BufferedWriter w = new BufferedWriter(new FileWriter(f));
                    w.write(debugEx);
                    debugEx = null;
                    w.flush();
                    w.close();
                    access.getLogger().debug("Dumped regex details as %s", f.getAbsolutePath());
                } catch (IOException e) {
                    access.getLogger().error("", e);
                }
            }
        }
        */

        if (event.getMessage().equalsIgnoreCase("Im better than alfred") || event.getMessage().equalsIgnoreCase("I'm better than alfred")) {
            event.getChannel().send().message(eventuser + ", It's good to dream big");
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
