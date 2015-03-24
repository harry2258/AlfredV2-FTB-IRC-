package com.harry2258.Alfred.Misc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.harry2258.Alfred.api.JsonUtils;
import com.harry2258.Alfred.api.MessageUtils;
import com.harry2258.Alfred.api.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Hardik at 11:24 PM on 2/4/14.
 */

public class CreeperHost extends Thread {
    public static ArrayList<String> ChReposlist = new ArrayList<>();
    private static String edges = "new";
    MessageEvent event;

    public CreeperHost(MessageEvent event) {
        this.event = event;
    }

    public static List getKeysFromJson(String string) throws Exception {
        Object things = new Gson().fromJson(string, Object.class);
        List keys = new ArrayList();
        collectAllTheKeys(keys, things);
        return keys;
    }

    static void collectAllTheKeys(List keys, Object o) {
        Collection values = null;
        if (o instanceof Map) {
            Map map = (Map) o;
            keys.addAll(map.keySet());
            values = map.values();
        } else if (o instanceof Collection)
            values = (Collection) o;
        else
            return;

        for (Object value : values)
            collectAllTheKeys(keys, value);
    }

    public void run() {
        ArrayList chRepos = new ArrayList<>();
        ArrayList<String> chURLs = new ArrayList<>();
        ArrayList<String> chURLNames = new ArrayList<>();
        ArrayList<Boolean> tests = new ArrayList<>();
        ArrayList<String> Status = new ArrayList<>();
        ArrayList<String> Message = new ArrayList<>();
        ArrayList<Integer> Load = new ArrayList<>();
        String sendMessage = "";
        Boolean Json = false;
        Boolean connect = false;
        Boolean progwml6 = false;
        int ch = ChReposlist.size();
        final long startTime = System.currentTimeMillis();

        try {
            Document doc = Jsoup.connect("https://dl.dropboxusercontent.com/u/10600322/edges.json").get();
            JsonObject report = JsonUtils.getJsonObject(doc.text());
            chRepos = (ArrayList) getKeysFromJson(doc.text());
            for (int i = 0; i < chRepos.size(); i++) {
                chURLs.add(report.get(chRepos.get(i).toString()).toString().replaceAll("\"", ""));
                chURLNames.add(report.get(chRepos.get(i).toString()).toString().replaceAll("\\..*", "").replaceAll("\"", ""));
                if (!ChReposlist.contains(chURLs.get(i))) {
                    ChReposlist.add(chURLs.get(i));
                }
            }
            event.getUser().send().notice("Got edges.json from progwml6");
            progwml6 = true;
        } catch (Exception f) {
            f.printStackTrace();
            event.getUser().send().notice("Failed to get edges.json from progwml6");
        }

        if (!progwml6) {
            do {
                if (Utils.pingUrl(edges + "/edges.json")) {
                    event.getUser().send().notice("Connected using: http://" + edges + "/edges.json");
                    connect = true;
                } else {
                    if (ChReposlist.isEmpty()) {
                        event.getUser().send().notice("Could not connect to new.creeperrepo.net, getting edges from edges.json");
                        try {
                            String edgesjson = JsonUtils.getStringFromFile(System.getProperty("user.dir") + "/edges.json");
                            Json = JsonUtils.isJSONObject(edgesjson);
                            JsonObject report = JsonUtils.getJsonObject(edgesjson);
                            chRepos = (ArrayList) getKeysFromJson(edgesjson);
                            for (int i = 0; i < chRepos.size(); i++) {
                                chURLs.add(report.get(chRepos.get(i).toString()).toString().replaceAll("\"", ""));
                                chURLNames.add(report.get(chRepos.get(i).toString()).toString().replaceAll("\\..*", "").replaceAll("\"", ""));
                                if (!ChReposlist.contains(chURLs.get(i))) {
                                    ChReposlist.add(chURLs.get(i));
                                }
                            }
                            ch--;
                            edges = ChReposlist.get(ch);
                        } catch (Exception jsonfile) {
                            jsonfile.printStackTrace();
                        }

                    } else {
                        event.getUser().send().notice("Could not connect to: http://" + edges + "/edges.json");
                        ch--;
                        edges = ChReposlist.get(ch);
                        connect = false;
                    }
                }
            } while (!connect);

            try {
                Document doc = Jsoup.connect("http://" + edges + "/edges.json").get();
                Json = JsonUtils.isJSONObject(doc.text());
                JsonObject report = JsonUtils.getJsonObject(doc.text());
                chRepos = (ArrayList) getKeysFromJson(doc.text());
                for (int i = 0; i < chRepos.size(); i++) {
                    chURLs.add(report.get(chRepos.get(i).toString()).toString().replaceAll("\"", ""));
                    chURLNames.add(report.get(chRepos.get(i).toString()).toString().replaceAll("\\..*", "").replaceAll("\"", ""));
                    if (!ChReposlist.contains(chURLs.get(i))) {
                        ChReposlist.add(chURLs.get(i));
                    }
                }
            } catch (Exception E) {
                E.printStackTrace();
            }
        }

        chRepos.remove("Chicago");

        for (int i = 0; i < chURLs.size(); i++) {
            boolean canConnect = false;
            if (chURLs.get(i).contains("chicago2")) {
                continue;
            }
            if (chURLs.get(i).contains("creeperrepo.net") || chURLs.get(i).contains("cursecdn.com")) {
                try {
                    Socket s = new Socket();
                    System.out.println(chURLs.get(i));
                    s.connect(new InetSocketAddress(chURLs.get(i), 80), 3000);
                    //If it connects
                    System.out.println("Connected to " + chURLs.get(i));
                    canConnect = true;
                    tests.add(true);

                    s.close();
                } catch (Exception e) {
                    tests.add(false);
                    System.out.println("Could not connect to: " + chURLs.get(i));
                    event.getUser().send().notice("Ping to " + chRepos.get(i) + " repo timed out!");
                }

                if (chURLs.get(i).contains("creeperrepo.net")) {
                    String jsonURL = "http://status.creeperrepo.net/fetchjson.php?l=" + chURLNames.get(i);
                    if (canConnect) {

                        try {
                            URL newURL = new URL(jsonURL);
                            HttpURLConnection urlConn = (HttpURLConnection) newURL.openConnection();
                            urlConn.setConnectTimeout(3000);
                            urlConn.setReadTimeout(5000);
                            urlConn.connect();

                            BufferedReader re = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                            String jsons = re.readLine();
                            String test;
                            int x;
                            JsonObject jsonObj = JsonUtils.getJsonObject(jsons);
                            test = jsonObj.get("Bandwidth").getAsString();
                            x = Integer.parseInt(test) * 100 / 1000000;
                            re.close();
                            urlConn.disconnect();
                            Load.add(x);
                            System.out.println(chURLNames.get(i) + ": " + x);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Load.add(0);
                        }
                    } else {
                        Load.add(0);
                    }
                } else if (chURLs.get(i).contains("cursecdn.com")) {
                    Load.add(-1);
                }
            }
        }

        for (Boolean test1 : tests) {
            if (test1) {
                Status.add(Colors.DARK_GREEN + "✓");
            } else {
                Status.add(Colors.RED + "✘");
            }
        }
        String test;

        if (Json) {
            test = Colors.DARK_GREEN + "✓";
        } else {
            test = Colors.RED + "✘";
        }

        Message.add("CreeperRepo: " + test + Colors.NORMAL + " Average Load " + (int) calculateAverage(Load) + "% | ");
        String ColorLoad = "";
        for (int x = 0; x < chRepos.size(); x++) {

            if (Load.get(x) < 33) {
                ColorLoad = Colors.DARK_GREEN + Load.get(x) + "%" + Colors.NORMAL;
            } else if (Load.get(x) >= 33 && Load.get(x) < 66) {
                ColorLoad = Colors.YELLOW + Load.get(x) + "%" + Colors.NORMAL;
            } else if (Load.get(x) >= 66) {
                ColorLoad = Colors.RED + Load.get(x) + "%" + Colors.NORMAL;
            } else if (Load.get(x) == -1) {
                ColorLoad = "";
            }

            Message.add(chRepos.get(x) + ": " + Status.get(x) + Colors.NORMAL + " " + ColorLoad + " | ");
        }
        for (String s : Message) {
            sendMessage += s;
        }

        MessageUtils.sendChannel(event, sendMessage);
        final long endTime = System.currentTimeMillis();
        Double time = (double) (endTime - startTime) / 1000;
        event.getUser().send().notice("Took me " + time + " seconds");
    }

    private double calculateAverage(List<Integer> marks) {
        Integer sum = 0;
        int bad = 0;
        if (!marks.isEmpty()) {
            for (Integer mark : marks) {
                if (mark != -1) {
                    sum += mark;
                } else
                    bad++;
            }
            return sum.doubleValue() / (marks.size() - bad);
        } else return 0.0;

    }

}
