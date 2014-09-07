/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;

import bsh.EvalError;
import com.harry2258.Alfred.api.*;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hardik
 */
public class Exec extends Command {

    private static Config config;
    private static PermissionManager manager;
    private static bsh.Interpreter interpreter;

    static {
        try {
            interpreter = new bsh.Interpreter();
            interpreter.getNameSpace().doSuperImport();
            interpreter.set("utils", new Utils());
            interpreter.set("conf", config);
            interpreter.set("registry", new CommandRegistry());
            interpreter.set("manager", manager);
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                interpreter.eval("java.lang.String getStuff(java.lang.String command){ java.lang.String output = \"\";java.lang.Process p = java.lang.Runtime.getRuntime().exec(new java.lang.String[] {\"/bin/sh\", \"-c\", command}); java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));java.lang.String temp = \"\";while((temp = in.readLine()) != null){ output += temp + \"\\t\"; } return output; }");
            } else {
                //interpreter.eval("java.lang.String getStuff(java.lang.String command){ java.lang.String output = \"\";java.lang.Process p = Runtime.getRuntime().exec(command}); BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));java.lang.String temp = \"\";while((temp = in.readLine()) != null){ output += temp + \"\\t\"; } return output; }");
            }
        } catch (Exception ex) {
            Logger.getLogger(Exec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Exec() {
        super("Exec", "Execute java code at runtime", "Exec <code> ex. exec chan.send().message(\"Hello World!\");");
    }

    @Override
    public boolean execute(MessageEvent event) {
        try {
            if (PermissionManager.hasExec(event.getUser().getNick())) {
                if (!(event.getMessage().toLowerCase().contains("processbuilder") || event.getMessage().toLowerCase().contains("runtime") || event.getMessage().toLowerCase().contains("process") || event.getMessage().toLowerCase().contains("system.getproperty"))) {
                    String[] args = event.getMessage().split(" ");
                    StringBuilder sb = new StringBuilder();
                    if (args.length >= 2) {
                        try {
                            interpreter.set("event", event);
                            interpreter.set("bot", event.getBot());
                            interpreter.set("chan", event.getChannel());
                            interpreter.set("user", event.getUser());
                            interpreter.set("config", config);
                            for (int i = 1; i < args.length; i++) {
                                sb.append(args[i]).append(" ");
                            }
                            String command = sb.toString().trim();
                            interpreter.eval(command);
                            return true;
                        } catch (EvalError ex) {
                            Logger.getLogger(Exec.class.getName()).log(Level.SEVERE, null, ex);
                            event.getChannel().send().message(ex.toString());
                            return true;
                        }
                    }
                }

            } else {
                event.getUser().send().notice("You have to be in the group 'Exec' to use this command!");
                return false;
            }
        } catch (Exception ex) {
            Logger.getLogger(Exec.class.getName()).log(Level.SEVERE, null, ex);
            event.getChannel().send().message(ex.getMessage());
        }
        return false;
    }


    @Override
    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public void setManager(PermissionManager manager) {
        Exec.manager = manager;
    }
}
