/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.api;

import java.util.HashMap;

/**
 *
 * @author Zack
 */
public class CommandRegistry {

    public static HashMap<String, Command> commands = new HashMap<>();

    public static void register(Command command) {
        if (commands.containsKey(command.getName())) {
            return;
        }
        commands.put(command.getName().toLowerCase(), command);
    }

    public static void unRegister(Command command) {
        commands.remove(command.getName().toLowerCase());
    }

    public static Command getCommand(String name) {
        if (commands.containsKey(name)) {
            return commands.get(name);
        } else {
            try {
                commands.put(name, (Command) Command.class.getClassLoader().loadClass("com.harry2258.Alfred.commands." + name).newInstance());
                return commands.get(name);
            } catch (Exception ex) {
                //Logger.getLogger(CommandRegistry.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
