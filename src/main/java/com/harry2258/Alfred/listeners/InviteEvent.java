/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.listeners;

import com.harry2258.Alfred.api.Config;
import org.pircbotx.hooks.ListenerAdapter;

/**
 *
 * @author Zack
 */
public class InviteEvent extends ListenerAdapter
{

    private Config config;

    public InviteEvent(Config conf)
    {
        this.config = conf;
    }
    @Override
    public void onInvite(org.pircbotx.hooks.events.InviteEvent event){
        System.out.println("Invite envent fired!");
        if(config.isAutoAcceptInvite()){
            System.out.println("Joining channel " + event.getChannel());
            event.getBot().joinChannel(event.getChannel());
        }else{
            System.out.println("Not joining.");
        }
    }
}