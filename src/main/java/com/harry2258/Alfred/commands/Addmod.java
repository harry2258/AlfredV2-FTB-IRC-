/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

/**
 *
 * @author Hardik
 */
public class Addmod extends Command {
    private Config config;
    private PermissionManager manager;
    
    public Addmod() {
        super("Addmod", "Adds the user to the mod list", "addmod [user]");
    }
    private static final String jsonFilePath = System.getProperty("user.dir") + "/perms.json";
    @Override
    public boolean execute(MessageEvent event) {
        
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
