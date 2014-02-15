package com.harry2258.Alfred.Misc;

import com.harry2258.Alfred.api.JsonUtils;
import com.harry2258.Alfred.api.Utils;
import org.json.JSONObject;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Hardik on 2/4/14.
 */

public class CreeperHost extends Thread {
    MessageEvent event;

    public CreeperHost(MessageEvent event) {
        this.event = event;
    }

    public void run() {

        ArrayList<String> chRepos = new ArrayList<>();
        ArrayList<String> chURLs = new ArrayList<>();
        ArrayList<String> chURLNames = new ArrayList<>();
        HashMap<String, Boolean> RPstatus = new HashMap<>();
        ArrayList<Boolean> tests = new ArrayList<>();
        ArrayList<String> Status = new ArrayList<>();
        ArrayList<String> Message = new ArrayList<>();
        ArrayList<Integer> Load = new ArrayList<>();
        String sendMessage = "";
        Boolean Json = false;
        try {
            URL url;
            url = new URL("http://new.creeperrepo.net/edges.json");
            BufferedReader re = new BufferedReader(new InputStreamReader(url.openStream()));
            String st;
            while ((st = re.readLine()) != null) {
                Json = JsonUtils.isJSONObject(st);
                st = st.replace("{", "").replace("}", "").replace("\"", "");
                String[] splitString = st.split(",");
                for (String entry : splitString) {
                    String[] splitEntry = entry.split(":");
                    if (splitEntry.length == 2) {
                        chRepos.add(splitEntry[0]);
                        chURLs.add(splitEntry[1]);
                        chURLNames.add(splitEntry[1].substring(0, splitEntry[1].indexOf(".creeperrepo.net")));
                    }
                }
            }
        } catch (IOException E) {
            E.printStackTrace();
        }

        chRepos.remove("Chicago");

        for (int i = 0; i < chURLs.size(); i++) {
            if (chURLs.get(i).contains("chicago2")) {
                continue;
            }
            if (chURLs.get(i).contains("creeperrepo.net")) {
                try {
                    boolean test = Utils.pingUrl(chURLs.get(i));
                    tests.add(test);
                    if (!test) {
                        event.getUser().send().notice("Ping to " + chURLs.get(i) + " timedout!");
                        System.out.println("Could not connect!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (chURLNames.get(i).contains("chicago2")) {
                    continue;
                }
                String jsonURL = "http://status.creeperrepo.net/fetchjson.php?l=" + chURLNames.get(i);
                System.out.println("Connecting to " + jsonURL);
                try {
                    URL newURL;
                    newURL = new URL(jsonURL);
                    String ts;
                    BufferedReader re = new BufferedReader(new InputStreamReader(newURL.openStream()));
                    String test = "0";
                    while ((ts = re.readLine()) != null) {
                        JSONObject jsonObj = new JSONObject(ts);
                        test = jsonObj.getString("Bandwidth");
                        int x = (int) (Integer.parseInt(test) * 100) / 1000000;
                        Load.add(x);
                    }
                } catch (Exception ex) {
                    Load.add(0);
                }
            }
        }

        /*
        for (String url : chURLs) {
            if (url.contains("chicago2")){
                continue;
            }
            if (url.contains("creeperrepo.net")) {
                try {
                    boolean test = Utils.pingUrl(url);
                    tests.add(test);
                    if (!test) {
                        event.getUser().send().notice("Ping to " + url + " timedout!");
                        System.out.println("Could not connect!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (String chURLName : chURLNames) {
            if (chURLName.contains("chicago2")) {
                continue;
            }
            String jsonURL = "http://status.creeperrepo.net/fetchjson.php?l=" + chURLName;
            System.out.println("Connecting to " + jsonURL);
            try {
                URL newURL;
                newURL = new URL(jsonURL);
                String ts;
                BufferedReader re = new BufferedReader(new InputStreamReader(newURL.openStream()));
                String test = "0";
                while ((ts = re.readLine()) != null) {
                    JSONObject jsonObj = new JSONObject(ts);
                    test = jsonObj.getString("Bandwidth");
                    int x = (int) (Integer.parseInt(test) * 100) / 1000000;
                    Load.add(x);
                }
            } catch (Exception ex) {
                Load.add(0);
            }
        }
        */
        for (Boolean test1 : tests) {
            if (test1) {
                Status.add(Colors.DARK_GREEN + "✓");
            } else {
                Status.add(Colors.RED + "✘");
            }
        }
        String test = null;

        if (Json) {
            test = Colors.DARK_GREEN + "✓";
        } else {
            test = Colors.RED + "✘";
        }

        Message.add("CreeperRepo: " + test + Colors.NORMAL + " Average Load " + (int) calculateAverage(Load) + "% | ");
        System.out.println(chRepos);
        System.out.println(Status);
        System.out.println(Load);

        for (int x = 0; x < chRepos.size(); x++) {
            Message.add(chRepos.get(x) + ": " + Status.get(x) + Colors.NORMAL + " " + Load.get(x) + "% | ");

        }

        for (String s : Message) {
            sendMessage += s + "\t";
        }

        event.getChannel().send().message(sendMessage);
    }

    private double calculateAverage(List<Integer> marks) {
        Integer sum = 0;
        if (!marks.isEmpty()) {
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
}
