package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.*;
import org.json.JSONObject;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


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
        ArrayList<String> chURLNames= new ArrayList<String>();
        ArrayList<Boolean> tests = new ArrayList<Boolean>();
        ArrayList<String> Status = new ArrayList<String>();
        ArrayList<String> Message = new ArrayList<String>();
        ArrayList<Integer> Load = new ArrayList<Integer>();
        
        String sendMessage = "";
        Boolean Json = null;
        try {
                URL url;
                url = new URL("http://new.creeperrepo.net/edges.json");
                BufferedReader re = new BufferedReader(new InputStreamReader(url.openStream()));
                String st;
                while ((st = re.readLine()) != null) {
                    Json = JsonUtils.isJSONObject(st);
                    st = st.replace("{", "").replace("}", "").replace("\"", "");
                    String[] splitString = st.split(",");
                    for(String entry : splitString) {
                        String[] splitEntry = entry.split(":");
                        if(splitEntry.length == 2) {
                            chRepos.add(splitEntry[0]);
                            chURLs.add(splitEntry[1]);
                            chURLNames.add(splitEntry[1].substring(0, splitEntry[1].indexOf(".creeperrepo.net") - 0));
                        }
                    }
                }
            } catch (IOException E) {
                if (E.getMessage().contains("503")) {
                }
                if (E.getMessage().contains("404")) {
                }
            }
        
        chURLs.remove("chicago2.creeperrepo.net");
        chURLNames.remove("chicago2");
        chRepos.remove("Chicago");
        
                for(String url : chURLs) {
                    if(url.contains("creeperrepo.net")) {
                        boolean test = Utils.pingUrl(url);
                    tests.add(test);
                    if (!test) {
                    event.getUser().send().notice("Ping to " + url + " timedout!");
                    }
                    }
                    
                }
                
                for (int y = 0; y < chURLNames.size(); y++){
                    String jsonURL = new String("http://status.creeperrepo.net/fetchjson.php?l=" + chURLNames.get(y));
                    System.out.println("Connecting to " + jsonURL);
                        try {
                            URL newURL;
                            newURL = new URL(jsonURL);
                            String ts;
                            BufferedReader re = new BufferedReader(new InputStreamReader(newURL.openStream()));
                            while ((ts = re.readLine()) != null) {
                                JSONObject jsonObj = new JSONObject(ts);
                                String test =jsonObj.getString("Bandwidth").toString();
                                int x = (int) (Integer.parseInt(test) * 100) / 1000000;
                                Load.add(x);
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(Chstatus.class.getName()).log(Level.SEVERE, null, ex);
                            Load.add(Integer.parseInt("0"));
                        }
                }
                
                
                
                for (int x = 0; x< tests.size(); x++) {
                    if(tests.get(x).equals(true) ) {
                        Status.add(Colors.DARK_GREEN + "✓");
                    } else {
                        Status.add(Colors.RED + "✘");
                    }
                }
                
                String test = null;
                if (Json) {
                    test = Colors.DARK_GREEN + "✓";
                    } else {
                        test =Colors.RED + "✘";
                    }
                
                Message.add("CreeperRepo: " + test + Colors.NORMAL + " Average Load " + (int)calculateAverage(Load) + "% | " );
                
                for (int x = 0; x < chRepos.size(); x++) {
                    Message.add(chRepos.get(x) + ": " + Status.get(x) + Colors.NORMAL + " " + Load.get(x) + "% | " );
                    
                }
                
                for (String s : Message) {
                    sendMessage += s + "\t";
                }
                
                
        event.getChannel().send().message(sendMessage);
        return true;
        }
    
    private double calculateAverage(List <Integer> marks) {
        Integer sum = 0;
        if(!marks.isEmpty()) {
            for (Integer mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }
    
    public static String Split(String message) {
        String[] string;
        string = message.split(".");
        return string[0];
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