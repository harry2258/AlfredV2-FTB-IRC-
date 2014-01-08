/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.api;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Hardik
 */
public class JsonUtils {
    public static File jsonFilePath = new File (System.getProperty("user.dir")+ "/Perms/perms.json");
    public static final String Jsonfile = System.getProperty("user.dir") + "/perms.json";
    
    
    public static  boolean isJSONObject(String possibleJson) {
        boolean valid = false;
        try {
            new JsonParser().parse(possibleJson);
            valid = true;
        } catch (JsonParseException e) {
            valid = false;
        }
        return valid;
    }
    
    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        
        fin.close();        
        return ret;
        
    }
    
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
    return sb.toString();
    }
    
    public static void writeJsonFile(File file, String json) {
    BufferedWriter bufferedWriter = null;
    try {

        if(!file.exists()){
            file.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(file);
        bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(json);

    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        try {
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    }
    
    public static void createJsonStructure(File file){
        try {
            System.out.println(file);
            file.getParentFile().mkdirs();
            file.createNewFile();
            String jsonString = "{\"Perms\":{\"Mods\":[\"niel\"], \"ModPerms\": [\"command.mods\", \"command.google\"], \"Admins\":[\"batman\"], \"Everyone\": [\"command.mcstatus\", \"command.chstatus\"], \"Exec\":[\"batman\"]}}";
            writeJsonFile(file, jsonString);

        } catch (IOException e) {
            e.printStackTrace();
        }
}
}
