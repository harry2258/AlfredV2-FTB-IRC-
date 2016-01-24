/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.api;

import com.google.common.io.Files;
import com.google.gson.*;
import com.harry2258.Alfred.json.Perms;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

/**
 * @author Hardik
 */
public class JsonUtils {
    //public static File jsonFilePath = new File(System.getProperty("user.dir") + "/Perms/perms.json");
    public static final String Jsonfile = System.getProperty("user.dir") + "/exec.json";
    public static final Gson GSON;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.enableComplexMapKeySerialization();
        builder.setPrettyPrinting();
        GSON = builder.create();
    }

    public static Perms getPermsFromString(String s) {
        return GSON.fromJson(s, Perms.class);
    }

    public static boolean isJSONObject(String possibleJson) {
        boolean valid;
        try {
            new JsonParser().parse(possibleJson);
            valid = true;
        } catch (JsonParseException e) {
            valid = false;
        }
        return valid;
    }

    public static String prettyPrint(List<String> l) {
        String ret = "";
        Iterator i = l.listIterator();
        while (i.hasNext()) {
            ret += ((String) i.next()).replace("commands.", "");
            if (i.hasNext())
                ret += " | ";
        }
        return ret;
    }

    public static String getStringFromFile(String filePath) throws Exception {
        return Files.toString(new File(filePath), Charset.isSupported("UTF-8") ? Charset.forName("UTF-8") : Charset.defaultCharset());
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    public static void writeJsonFile(File file, String json) {
        try {
            FileUtils.write(file, json, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeJsonFile(File file, Perms p) {
        try {
            FileUtils.write(file, GSON.toJson(p), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JsonObject getJsonObject(String s) {
        JsonElement element = new JsonParser().parse(s);
        if (element != null && element.isJsonObject()) {
            return element.getAsJsonObject();
        }
        return null;
    }

    public static void createJsonStructure(File file) {
        //you do realize you can literally make a file in the resources directory and just save it outside of the jar instead of this, right?
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
            String jsonString = "{\"Perms\":{\"Mods\":[\"niel\"], \"ModPerms\": [\"command.custom\", \"command.google\", \"command.info\"], \"Admins\":[], \"Everyone\": [\"command.mcstatus\", \"command.chstatus\"]}}";
            writeJsonFile(file, jsonString);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
