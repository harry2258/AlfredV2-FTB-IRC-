/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

/**
 *
 * @author Hardik
 */
public class ReloadPerms extends Command {
    private PermissionManager manager;
    private Config config;
    
    public ReloadPerms(){
        super("ReloadPerms", "Reloads the permission file!");
    }
    @Override
    public boolean execute(MessageEvent event) {
        event.getChannel().send().message("reloading permissions!");
        config.load();
        manager.load();
        event.getChannel().send().message("Permission were reloaded!");
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
