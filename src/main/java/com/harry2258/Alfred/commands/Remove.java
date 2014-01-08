/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.JsonUtils;
import com.harry2258.Alfred.api.PermissionManager;
import java.io.File;
import java.util.ArrayList;
import org.json.JSONObject;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

/**
 *
 * @author Hardik
 */
public class Remove extends Command {
    private Config config;
    private PermissionManager manager;
    
    public Remove() {
        super("Remove", "Removes the Permission/User from the list", "remove [" + Colors.DARK_BLUE + "Permission" + Colors.NORMAL + "/" + Colors.DARK_GREEN + "User" + Colors.NORMAL + "] [" + Colors.DARK_BLUE + "Permission" + Colors.NORMAL + "/" + Colors.DARK_GREEN +"User" + Colors.NORMAL +"]");
    }
    
    @Override
    public boolean execute(MessageEvent event) {
        File file = new File (System.getProperty("user.dir") + "/Perms/" + event.getChannel().getName() + "/" +  "perms.json");
        if (!file.exists()){
            JsonUtils.createJsonStructure(file);
        }
        String Jsonfile = System.getProperty("user.dir") + "/Perms/" + event.getChannel().getName() + "/" +  "perms.json";
        String[] args = event.getMessage().split(" ");
        String type = args[1];
        String mod = event.getBot().getUserChannelDao().getUser(args[2]).getNick();
        String check = args[2];
        String command = null;
           if (!check.contains("command.")){
               command = "command." + check;
           } else { command = check; }
           
           
    if (type.equalsIgnoreCase("mod")) {
        if (args.length == 3) {
            
            try {
                String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                JSONObject jsonObj = new JSONObject(strFileJson);
                ArrayList<String> tests = new ArrayList<String>();
                ArrayList<String> testss = new ArrayList<String>();
                if (jsonObj.getJSONObject("Perms").getString("Mods").contains(mod)) {
                    String test = jsonObj.getJSONObject("Perms").getJSONArray("Mods").toString();
                    jsonObj.getJSONObject("Perms").remove("Mods");
                    String string = test.replace(",\"" + mod + "\"" , "").replace("\"", "").replaceAll("]", "").replace("[", "");
                    String newstring = string.replaceAll(",", " ");
                    String[] finalstring = newstring.split(" ");
                    
                    for (int x = 0; x < finalstring.length; x++) {
                        jsonObj.getJSONObject("Perms").append("Mods", finalstring[x]);
                        
                    }
                    
                    JsonUtils.writeJsonFile(file, jsonObj.toString());
                    event.getUser().send().notice(mod + " was removed from the list!");
                    return true;
                } else {
                    event.getChannel().send().message(mod + " is not on the list!");
                return true;
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
    
    if (type.equalsIgnoreCase("modperms")) {
        if (args.length == 3) {
            try {
                String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                JSONObject jsonObj = new JSONObject(strFileJson);
                if (jsonObj.getJSONObject("Perms").getString("ModPerms").contains(command)) {
                    String test = jsonObj.getJSONObject("Perms").getJSONArray("ModPerms").toString();
                    jsonObj.getJSONObject("Perms").remove("ModPerms");
                    String string = test.replace(",\"" + command + "\"" , "").replace("\"", "").replaceAll("]", "").replace("[", "");
                    String newstring = string.replaceAll(",", " ");
                    String[] finalstring = newstring.split(" ");
                    
                    for (int x = 0; x < finalstring.length; x++) {
                        jsonObj.getJSONObject("Perms").append("ModPerms", finalstring[x]);
                        
                    }
                    
                    JsonUtils.writeJsonFile(file, jsonObj.toString());
                    event.getUser().send().notice(command + " was removed from the list!");
                    return true;
                } else {
                    event.getChannel().send().message(command + " is not on the list!");
                return true;
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
    
    if (type.equalsIgnoreCase("admin")) {
        if (args.length == 3) {
            
            try {
                String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                JSONObject jsonObj = new JSONObject(strFileJson);
                ArrayList<String> tests = new ArrayList<String>();
                ArrayList<String> testss = new ArrayList<String>();
                if (jsonObj.getJSONObject("Perms").getString("Admins").contains(mod)) {
                    String test = jsonObj.getJSONObject("Perms").getJSONArray("Admins").toString();
                    jsonObj.getJSONObject("Perms").remove("Admins");
                    String string = test.replace(",\"" + mod + "\"" , "").replace("\"", "").replaceAll("]", "").replace("[", "");
                    String newstring = string.replaceAll(",", " ");
                    String[] finalstring = newstring.split(" ");
                    
                    for (int x = 0; x < finalstring.length; x++) {
                        jsonObj.getJSONObject("Perms").append("Admins", finalstring[x]);
                        
                    }
                    
                    JsonUtils.writeJsonFile(file, jsonObj.toString());
                    String strJson = JsonUtils.getStringFromFile(JsonUtils.Jsonfile.toString());
                    event.getUser().send().notice(mod + " was removed from the list!");
                    return true;
                } else {
                    event.getChannel().send().message(mod + " is not on the list!");
                return true;
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
    
    if (type.equalsIgnoreCase("everyone")) {
        if (args.length == 3) {
            try {
                String strFileJson = JsonUtils.getStringFromFile(Jsonfile);
                JSONObject jsonObj = new JSONObject(strFileJson);
                if (jsonObj.getJSONObject("Perms").getString("Everyone").contains(command)) {
                    String test = jsonObj.getJSONObject("Perms").getJSONArray("Everyone").toString();
                    jsonObj.getJSONObject("Perms").remove("Everyone");
                    String string = test.replace(",\"" + command + "\"" , "").replace("\"", "").replaceAll("]", "").replace("[", "");
                    String newstring = string.replaceAll(",", " ");
                    String[] finalstring = newstring.split(" ");
                    
                    for (int x = 0; x < finalstring.length; x++) {
                        jsonObj.getJSONObject("Perms").append("Everyone", finalstring[x]);
                        
                    }
                    
                    JsonUtils.writeJsonFile(file, jsonObj.toString());
                    String strJson = JsonUtils.getStringFromFile(JsonUtils.Jsonfile.toString());
                    event.getUser().send().notice(command + " was removed from the list!");
                    return true;
                } else {
                    event.getChannel().send().message(command + " is not on the list!");
                return true;
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
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
