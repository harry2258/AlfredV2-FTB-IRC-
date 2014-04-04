package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

import static com.harry2258.Alfred.api.CommandRegistry.commands;

/**
 * Created with IntelliJ IDEA.
 * User: Zack
 * Date: 11/24/13
 * Time: 3:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class Setcmd extends Command {
    private Config config;
    private PermissionManager manager;

    public Setcmd() {
        super("Setcmd", "Set a custom command", "setcmd [trigger] [output] ex `setcmd test this is a test!");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        Date date = new Date();
        String[] args = event.getMessage().split(" ");
        String classname = Character.toUpperCase(args[1].charAt(0)) + event.getMessage().split(" ")[1].substring(1).toLowerCase();
        if (!commands.containsKey(classname)) {
            if (args.length > 1) {
                StringBuilder sb = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }
                try {

                    File command = new File("commands/" + event.getChannel().getName() + "/" + classname + ".cmd");
                    command.getParentFile().mkdirs();
                    command.createNewFile();
                    PrintWriter writer = new PrintWriter(new FileWriter(command));
                    String[] lines = sb.toString().trim().split("\\\\n");
                    if (lines.length <= 3) {
                        for (String s : lines) {
                            writer.println(s);
                        }
                    } else {
                        event.getChannel().send().message("lines must be less than or equal to 3!");
                        return false;
                    }
                    writer.flush();
                    writer.close();
                    event.getUser().send().notice("'" + classname + "' was set to '" + sb.toString() + "'");

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            event.getUser().send().notice("You cannot create a custom command by that name!");
        }
        return false;
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
