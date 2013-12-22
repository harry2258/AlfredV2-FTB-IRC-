package com.harry2258.Alfred.api;

import java.util.HashMap;
import static com.harry2258.Alfred.api.Command.commands;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pircbotx.hooks.events.MessageEvent;

public abstract class Command
{
    private Config config;
    public static HashMap<String, Object> commands = new HashMap<>();

    /**
     * Fairly self-explanitory. Used to execute the command's code
     *
     * @param pircbotx MessageEvent to run from
     */
    public void run(MessageEvent event)
    {
        //insert code here
    }

    /**
     * Used to pass a config to the command if needed
     *
     * @param conf the CookieConfig object to pass to the command. not
     * *required* unless you actually access the config.
     */
    public void setConfig(Config conf)
    {
        this.config = conf;
    }
    

    /**
     * @return A description of what the command does
     */
    public String getDescription()
    {
        return "No description available for %CLASSNAME%";
    }

    /**
     * @return Syntax of the command.
     */
    public String getSyntax()
    {
        return "No syntax information available for class %CLASSNAME%";
    }

    /**
     * @return the commands name
     */
    public String getName()
    {
        return "No command name set for %CLASSNAME%";
    }

    /**
     * @return if the command is secret (currently only used to hide the command
     * from help)
     */
    public boolean isSecret()
    {
        return false;
    }

    public static Object getOrCreateNewInstance(String classname)
    {
        if (commands.containsKey(classname))
        {

            return commands.get(classname);
        } else
        {
            try
            {
                commands.put(classname, Command.class.getClassLoader().loadClass("com.harry2258.Alfred.commands." + classname).newInstance());
                return commands.get(classname);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex)
            {
                Logger.getLogger(Command.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

}
