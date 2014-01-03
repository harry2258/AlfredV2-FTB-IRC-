package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pircbotx.Colors;

import org.pircbotx.hooks.events.MessageEvent;


public class Chstatus extends Command {
    
    private Config config;
    private PermissionManager manager;
    public Chstatus() {
        super("ChStatus", "Shows status of CreeperHost repos", ":3");
    }

    @Override
    public boolean execute(MessageEvent event) {
        
        ArrayList<String> chRepos= new ArrayList<String>();
        ArrayList<String> chURLs= new ArrayList<String>();
        ArrayList<Boolean> tests = new ArrayList<Boolean>();
        ArrayList<String> Status = new ArrayList<String>();
        ArrayList<String> Message = new ArrayList<String>();
        String sendMessage = "";
        try {
                URL url;
                url = new URL("http://new.creeperrepo.net/edges.json");
                BufferedReader re = new BufferedReader(new InputStreamReader(url.openStream()));
                String st;
                chRepos.add("Launcher");
                while ((st = re.readLine()) != null) {
                    tests.add(Utils.isJSONObject(st));
                    st = st.replace("{", "").replace("}", "").replace("\"", "");
                    String[] splitString = st.split(",");
                    for(String entry : splitString) {
                        String[] splitEntry = entry.split(":");
                        if(splitEntry.length == 2) {
                            chRepos.add(splitEntry[0]);
                            chURLs.add(splitEntry[1]);
                        }
                    }
                }
            } catch (IOException E) {
                if (E.getMessage().contains("503")) {
                }
                if (E.getMessage().contains("404")) {
                }
            }
        
                for(String url : chURLs) {
                    if(url.contains("creeperrepo.net")) {
                    tests.add(Utils.pingUrl(url));
                    
                    }
                }
                
                for (Boolean status : tests) {
                    if(status.equals(true) ) {
                        Status.add(Colors.DARK_GREEN + "Online" + Colors.NORMAL + " | ");
                    } else {
                        Status.add(Colors.RED + "Offline" + Colors.NORMAL + " | ");
                    }
                }
                
                for (int x = 0; x < chRepos.size(); x++) {
                    Message.add(chRepos.get(x) + ": " + Status.get(x));
                }
                
                for (String s : Message) {
                    sendMessage += s + "\t";
                }
                
                
        event.getChannel().send().message(sendMessage);
        return true;
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