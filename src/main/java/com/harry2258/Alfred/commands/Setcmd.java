package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

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
    public Setcmd(){
        super("Setcmd", "Set a custom command", "setcmd <trigger> <output> ex $setcmd test this is a test!");
    }

    @Override
    public boolean execute(MessageEvent event) {
        String[] args = event.getMessage().split(" ");
        if(args.length > 1){
            String commandname = args[1];
            StringBuilder sb = new StringBuilder();
            for(int i = 2; i < args.length; i++){
                sb.append(args[i]).append(" ");
            }
            try{
                File command = new File("commands/" + event.getChannel().getName() + "/" + commandname + ".cmd");
                command.getParentFile().mkdirs();
                command.createNewFile();
                PrintWriter writer = new PrintWriter(new FileWriter(command));
                for(String s : sb.toString().trim().split("\\\\n")){
                    writer.println(s);
                }
                writer.flush();
                writer.close();
                event.getChannel().send().message("'"+ commandname + "' was set to '" + sb.toString() +"'");
                return true;
            }catch (Exception e){
            }
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
