/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

/**
 *
 * @author Zack
 */
public class Kill extends Command
{

    private Config config;
    
    @Override
    public void setConfig(Config conf)
    {
        this.config = conf;
    }
    @Override
    public void run(MessageEvent event)
    {
        PircBotX bot = event.getBot();
        if (config.isAdmin(event.getUser().getNick(), event.getUser().getHostmask()))
        {
            bot.setAutoReconnect(false);
            bot.quitServer("Bye bye!");
            bot.shutdown();
        }else{
            bot.sendMessage(event.getChannel(), config.getPermissionDenied().replaceAll("%USERNAME%", event.getUser().getNick()));
        }
    }
    @Override
    public String getDescription()
    {
        return "Disconnects the bot from the current server";
    }

    @Override
    public String getSyntax()
    {
        return config.getTrigger() + "Kill";
    }

    @Override
    public String getName()
    {
        return "Kill";
    }

    @Override
    public boolean isSecret()
    {
        return false;
    }
}
