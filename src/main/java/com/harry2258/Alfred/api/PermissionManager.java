/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.api;

import com.harry2258.Alfred.Main;
import org.pircbotx.User;
import org.pircbotx.Channel;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PermissionManager {

    private Properties modproperties;
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
            
            modproperties = new Properties();
            File modconfig = new File("modpermissions.properties");
            //hur copy from above
            if (!modconfig.exists()) {
                System.out.println("[!!] No Mod properties file found! generating a new one! [!!]");
                modconfig.createNewFile();
                BufferedReader s = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/modpermissions.properties")));
                String tmp = "perms: ";
                BufferedWriter out = new BufferedWriter(new FileWriter(modconfig));
                while ((tmp = s.readLine()) != null) {
                    out.write(tmp);
                    out.flush();
                    out.newLine();
                } 
                out.close();
                System.out.println("[!!] Done! [!!]");
            }
            properties.load(new FileInputStream("permissions.properties"));
            modproperties.load(new FileInputStream("modpermissions.properties"));
        } catch (Exception ex) {
            Logger.getLogger(PermissionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("\n\n=======================\nPERMISSIONS\n=======================");
        for (String s : properties.stringPropertyNames()) {
            System.out.println(s);
        }
        System.out.println("\n\n=======================\nMOD PERMISSIONS\n=======================");
        for (String s : modproperties.stringPropertyNames()) {
            System.out.println(s);
        }
    }
    
    public void reload() throws IOException {
        properties.load(new FileInputStream("permissions.properties"));
        modproperties.load(new FileInputStream("modpermissions.properties"));
    }

    public boolean hasPermission(String permission, User user, Channel channel) {
        boolean hostmatch = false;
        boolean nickmatch = false;
        boolean permmatch = false;
        String nick;
        String hostname;
        
        if (configs.isAdmin(user.getNick(), user.getHostmask())) {
            return true;
        }
        List<String> modperms = Arrays.asList(modproperties.getProperty("perms").split(" "));
        if (modperms.contains(permission)) {
            permmatch = true;
            if (user.isIrcop() || channel.hasVoice(user)) {
                return true;
            }
            for (String host : modproperties.stringPropertyNames()) {
                nick = host.split("\\@")[0];
                Pattern p = Pattern.compile(user.getLogin());
                Matcher m = p.matcher(nick);
                if (m.find()) {
                    nickmatch = true;
                }
                if (nickmatch && user.isVerified()) {
                    return true;
                }
            }
        }
        
        //huehuheue copypasta from Config.java
        for (String host : properties.stringPropertyNames()) {
            nick = host.split("\\@")[0];
            Pattern p = Pattern.compile(user.getLogin());
            Matcher m = p.matcher(nick);
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
