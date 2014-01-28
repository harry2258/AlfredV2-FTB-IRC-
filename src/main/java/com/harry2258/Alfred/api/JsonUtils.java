/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.api;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.*;

/**
 * @author Hardik
 */
public class JsonUtils {
    //public static File jsonFilePath = new File(System.getProperty("user.dir") + "/Perms/perms.json");
    public static final String Jsonfile = System.getProperty("user.dir") + "/exec.json";


    public static boolean isJSONObject(String possibleJson) {
        boolean valid = false;
        try {
            new JsonParser().parse(possibleJson);
            valid = true;
        } catch (JsonParseException e) {
            valid = false;
        }
        return valid;
    }

    public static String getStringFromFile(String filePath) throws Exception {
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
            FileWriter fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(json);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void createJsonStructure(File file) {
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
            String jsonString = "{\"Perms\":{\"Mods\":[\"niel\"], \"ModPerms\": [\"command.custom\", \"command.google\"], \"Admins\":[\"batman\", \"progwml6\"], \"Everyone\": [\"command.mcstatus\", \"command.chstatus\"], \"Exec\":[\"batman\", \"progwml6\"]}}";
            writeJsonFile(file, jsonString);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
