/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.api;

import com.harry2258.Alfred.Main;
import org.pircbotx.User;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PermissionManager {

    private Properties properties;
    private Config configs;

    public PermissionManager(Config conf) {
        this.configs = conf;
    }

    public void load() {
        try {
            properties = new Properties();
            File config = new File("permissions.properties");
            if (!config.exists()) {
                System.out.println("[!!] No configuration file found! generating a new one! [!!]");
                config.createNewFile();
                BufferedReader s = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/permissions.properties")));
                String tmp = "";
                BufferedWriter out = new BufferedWriter(new FileWriter(config));
                while ((tmp = s.readLine()) != null) {
                    out.write(tmp);
                    out.flush();
                    out.newLine();
                } 
                out.close();
                System.out.println("[!!] Done! [!!]");
            }
            properties.load(new FileInputStream("permissions.properties"));
        } catch (Exception ex) {
            Logger.getLogger(PermissionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("\n\n=======================\nPERMISSIONS\n=======================");
        for (String s : properties.stringPropertyNames()) {
            System.out.println(s);
        }
    }
    
    public void reload() throws IOException {
        properties.load(new FileInputStream("permissions.properties"));
    }

    public boolean hasPermission(String permission, User user) {
        boolean hostmatch = false;
        boolean nickmatch = false;
        String nick;
        String hostname;
        if (configs.isAdmin(user.getNick(), user.getHostmask())) {
            return true;
        }
        //huehuheue copypasta from Config.java
        for (String host : properties.stringPropertyNames()) {
            nick = host.split("\\@")[0];
            Pattern p = Pattern.compile(user.getLogin());
            Matcher m = p.matcher(nick);
            System.out.println(nick);
            System.out.println(m.toString());
            if (m.find()) {
                nickmatch = true;
            }
            
            if (nickmatch && user.isVerified()) {
                List<String> permissions = Arrays.asList(properties.getProperty(host).split(" "));
                if (permissions.contains(permission) || permissions.contains("permissions.*")) {
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    public PermissionManager getPermissionsManager() {
        return this;
    }
}
