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
import org.json.JSONObject;

public class PermissionManager {

    public Properties modproperties;
    public Properties properties;
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
                s.close();
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
                s.close();
                System.out.println("[!!] Done! [!!]");
            }
            properties.load(new FileInputStream("permissions.properties"));
            modproperties.load(new FileInputStream("modpermissions.properties"));
        } catch (Exception ex) {
            Logger.getLogger(PermissionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("\n\n=======================\nPERMISSIONS\n=======================");
        for (String s : properties.stringPropertyNames()) {
            List<String> permissions = Arrays.asList(properties.getProperty(s).split(" "));
            System.out.println(s + ": " + permissions.toString());
        }
        System.out.println("\n\n=======================\nMOD PERMISSIONS\n=======================");
        for (String s : modproperties.stringPropertyNames()) {
            List<String> permissions = Arrays.asList(modproperties.getProperty(s).split(" "));
            System.out.println(s + ": " + permissions.toString());
        }
    }
    public void reload() throws FileNotFoundException, IOException{
        //moar copypasta :3
        properties.load(new FileInputStream("permissions.properties"));
        modproperties.load(new FileInputStream("modpermissions.properties"));
        
        System.out.println("\n\n=======================\nPERMISSIONS\n=======================");
        for (String s : properties.stringPropertyNames()) {
            List<String> permissions = Arrays.asList(properties.getProperty(s).split(" "));
            System.out.println(s + ": " + permissions.toString());
        }
        
        
        System.out.println("\n\n=======================\nMOD PERMISSIONS\n=======================");
        for (String s : modproperties.stringPropertyNames()) {
            List<String> permissions = Arrays.asList(modproperties.getProperty(s).split(" "));
            System.out.println(s + ": " + permissions.toString());
        }
        
    }

    public boolean hasPermission(String permission, User user, Channel channel) throws Exception {
        boolean hostmatch = false;
        boolean nickmatch = false;
        boolean permmatch = false;
        String nick;
        String hostname;
        
        if (configs.isAdmin(user.getNick(), user.getHostmask())) {
            return true;
        }
        
        String perms = JsonUtils.getStringFromFile(JsonUtils.Jsonfile.toString());
        JSONObject jsonObj = new JSONObject(perms);
        
        if (jsonObj.getJSONObject("Perms").getString("Everyone").contains(permission)) {
            if (user.isVerified()) {
            return true;
            } else { user.send().notice("You need to be logged in to use this!");}
        }
        
        if (jsonObj.getJSONObject("Perms").getString("ModPerms").contains(permission)) {
            if (jsonObj.getJSONObject("Perms").getString("Mods").contains(user.getNick()) && user.isVerified() || channel.hasVoice(user) ) {
            return true;
            }
        }
        
        if (jsonObj.getJSONObject("Perms").getString("Admins").contains(user.getNick())) {
            if (user.isVerified()) {
            return true;
            }
        }
        
        if (jsonObj.getJSONObject("Perms").getString("Exec").contains(user.getNick())) {
            if (user.isVerified()) {
            return true;
            }
        }
        
        return false;
    }

    public PermissionManager getPermissionsManager() {
        return this;
    }
}
