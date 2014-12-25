/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.api;

import com.google.common.collect.Maps;

import java.util.HashMap;

public class CommandRegistry {

    public static HashMap<String, Command> commands = Maps.newHashMap();

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
            }
        }
        return null;
    }
}
