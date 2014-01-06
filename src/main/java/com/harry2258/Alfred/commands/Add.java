/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.JsonUtils;
import com.harry2258.Alfred.api.PermissionManager;
import org.json.JSONObject;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

/**
 *
 * @author Hardik
 */
public class Add extends Command {
    private Config config;
    private PermissionManager manager;
    
    public Add() {
        super("Add", "Adds the Permission/User to the list", "add [" + Colors.DARK_BLUE + "Permission" + Colors.NORMAL + "/" + Colors.DARK_GREEN + "User" + Colors.NORMAL + "] [" + Colors.DARK_BLUE + "Permission" + Colors.NORMAL + "/" + Colors.DARK_GREEN +"User" + Colors.NORMAL +"]");
    }
    @Override
    public boolean execute(MessageEvent event) {
        JsonUtils.createJsonStructure();
        String[] args = event.getMessage().split(" ");
        String type = args[1];
        String newmod = event.getBot().getUserChannelDao().getUser(args[2]).getNick();
    if (type.equalsIgnoreCase("mod")) {
        if (args.length == 3) {
            try {
                String strFileJson = JsonUtils.getStringFromFile(JsonUtils.Jsonfile.toString());
                JSONObject jsonObj = new JSONObject(strFileJson);
                Gson gson = new Gson();
                JsonParser jsonParser = new JsonParser();
                if (!jsonObj.getJSONObject("Perms").getString("Mods").contains(newmod)) {
                jsonObj.getJSONObject("Perms").append("Mods", newmod);
                JsonUtils.writeJsonFile(JsonUtils.jsonFilePath, jsonObj.toString());
                String strJson = JsonUtils.getStringFromFile(JsonUtils.Jsonfile.toString());
                return true;
                } else {
                    event.getChannel().send().message(newmod + " is already on the list!");
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
                String command = args[2];
                String strFileJson = JsonUtils.getStringFromFile(JsonUtils.Jsonfile.toString());
                JSONObject jsonObj = new JSONObject(strFileJson);
                Gson gson = new Gson();
                JsonParser jsonParser = new JsonParser();
                
                //JSONObject JSONObject = new JSONObject(jsonStr);
                if (!jsonObj.getJSONObject("Perms").getString("ModPerms").contains(command)) {
                jsonObj.getJSONObject("Perms").append("ModPerms", command);
                JsonUtils.writeJsonFile(JsonUtils.jsonFilePath, jsonObj.toString());
                String strJson = JsonUtils.getStringFromFile(JsonUtils.Jsonfile.toString());
                return true;
                } else {
                    event.getChannel().send().message(command + " is already on the list!");
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
