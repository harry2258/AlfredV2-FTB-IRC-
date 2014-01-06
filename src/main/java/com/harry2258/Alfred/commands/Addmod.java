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
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
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
    @Override
    public boolean execute(MessageEvent event) {
        JsonUtils.createJsonStructure();
        String[] args = event.getMessage().split(" ");
        String newmod = event.getBot().getUserChannelDao().getUser(args[1]).getNick();
        if (args.length == 2) {
            try {
                String strFileJson = JsonUtils.getStringFromFile(JsonUtils.Jsonfile.toString());
                event.getChannel().send().message(strFileJson);
                JSONObject jsonObj = new JSONObject(strFileJson);
                Gson gson = new Gson();
                JsonParser jsonParser = new JsonParser();
                
                //JSONObject JSONObject = new JSONObject(jsonStr);
                jsonObj.getJSONObject("Perms").append("Mods", newmod);
                JsonUtils.writeJsonFile(JsonUtils.jsonFilePath, jsonObj.toString());
                String strJson = JsonUtils.getStringFromFile(JsonUtils.Jsonfile.toString());
                event.getChannel().send().message(strJson);
                return true;
             
            } catch (Exception ex) {
                System.out.println(ex);
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
