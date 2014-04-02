package com.harry2258.Alfred.Misc;

import com.harry2258.Alfred.api.JsonUtils;
import com.harry2258.Alfred.api.Utils;
import org.json.JSONObject;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Hardik on 2/4/14.
 */

public class CreeperHost extends Thread
{
    MessageEvent event;

    public CreeperHost(MessageEvent event)
    {
        this.event = event;
    }

    public static ArrayList<String> ChReposlist = new ArrayList<>();
    private static String edges = "new";

    public void run ()
    {
        HashMap<String, String> Jsons = new HashMap<>();
        ArrayList<String> chRepos = new ArrayList<>();
        ArrayList<String> chURLs = new ArrayList<>();
        ArrayList<String> chURLNames = new ArrayList<>();
        ArrayList<Boolean> tests = new ArrayList<>();
        ArrayList<String> Status = new ArrayList<>();
        ArrayList<String> Message = new ArrayList<>();
        ArrayList<Integer> Load = new ArrayList<>();
        String sendMessage = "";
        Boolean Json = false;
        Boolean connect = false;
        Boolean harry2258Json = false;
        int ch = ChReposlist.size();
        final long startTime = System.currentTimeMillis();

        try
        {
            URL repoListing;
            repoListing = new URL("https://dl.dropboxusercontent.com/u/10600322/edges.json");
            BufferedReader h = new BufferedReader(new InputStreamReader(repoListing.openStream()));
            String st;
            while ((st = h.readLine()) != null)
            {
                Json = JsonUtils.isJSONObject(st);
                st = st.replace("{", "").replace("}", "").replace("\"", "");
                String[] splitString = st.split(",");
                for (String entry : splitString)
                {
                    String[] splitEntry = entry.split(":");
                    if (splitEntry.length == 2)
                    {
                        chRepos.add(splitEntry[0]);
                        chURLs.add(splitEntry[1]);
                        chURLNames.add(splitEntry[1].substring(0, splitEntry[1].indexOf(getRepoURL(splitEntry[1]))));
                        if (!ChReposlist.contains(splitEntry[1]))
                        {
                            ChReposlist.add(splitEntry[1].substring(0));
                        }
                    }
                }
            }
            event.getUser().send().notice("Got edges.json from progwml6");
            harry2258Json = true;
        }
        catch (Exception f)
        {
            f.printStackTrace();
            event.getUser().send().notice("Failed to get edges.json from progwml6");
        }

        if (!harry2258Json)
        {
            do
            {
                if (Utils.pingUrl("http://" + edges + "/edges.json"))
                {
                    event.getUser().send().notice("Connected using: http://" + edges + "/edges.json");
                    connect = true;
                }
                else
                {
                    if (ChReposlist.isEmpty())
                    {
                        try
                        {
                            String temp;
                            String edgesjson = JsonUtils.getStringFromFile(System.getProperty("user.dir") + "/edges.json");
                            Json = JsonUtils.isJSONObject(edgesjson);
                            temp = edgesjson.replace("{", "").replace("}", "").replace("\"", "");
                            String[] splitString = edgesjson.split(",");
                            for (String entry : splitString)
                            {
                                String[] splitEntry = entry.split(":");
                                if (splitEntry.length == 2)
                                {
                                    chURLNames.add(splitEntry[1].substring(0, splitEntry[1].indexOf(getRepoURL(splitEntry[1]))));
                                    if (!ChReposlist.contains(splitEntry[1]))
                                    {
                                        ChReposlist.add(splitEntry[1].substring(0));
                                    }
                                }
                            }
                            event.getUser().send().notice("Could not connect to new.creeperrepo.net, getting edges from edges.json");
                            ch--;
                            edges = ChReposlist.get(ch);
                        }
                        catch (Exception jsonfile)
                        {
                            jsonfile.printStackTrace();
                        }
                    }
                    else
                    {
                        event.getUser().send().notice("Could not connect to: http://" + edges + "/edges.json");
                        ch--;
                        edges = ChReposlist.get(ch);
                        connect = false;
                    }
                }
            } while (!connect);

            try
            {
                URL url;
                url = new URL("http://" + edges + "/edges.json");
                BufferedReader re = new BufferedReader(new InputStreamReader(url.openStream()));
                String st;
                while ((st = re.readLine()) != null)
                {
                    Json = JsonUtils.isJSONObject(st);
                    //File file = new File(System.getProperty("user.dir") + "/edges.json");
                    //JsonUtils.writeJsonFile(file, st);
                    st = st.replace("{", "").replace("}", "").replace("\"", "");
                    String[] splitString = st.split(",");
                    for (String entry : splitString)
                    {
                        String[] splitEntry = entry.split(":");
                        if (splitEntry.length == 2)
                        {
                            chRepos.add(splitEntry[0]);
                            chURLs.add(splitEntry[1]);
                            chURLNames.add(splitEntry[1].substring(0, splitEntry[1].indexOf(getRepoURL(splitEntry[1]))));
                            if (!ChReposlist.contains(splitEntry[1]))
                            {
                                ChReposlist.add(splitEntry[1].substring(0));
                            }
                        }
                    }
                }
            }
            catch (IOException E)
            {
                E.printStackTrace();
            }
        }

        chRepos.remove("Chicago");

        for (int i = 0; i < chURLs.size(); i++)
        {
            boolean canConnect = false;
            if (chURLs.get(i).contains("chicago2"))
            {
                continue;
            }
            if (chURLs.get(i).contains("creeperrepo.net") || chURLs.get(i).contains("cursecdn.com"))
            {
                try
                {
                    boolean test = Utils.pingUrl(chURLs.get(i));
                    tests.add(test);
                    if (!test)
                    {
                        event.getUser().send().notice("Ping to " + chURLs.get(i) + " timedout!");
                        System.out.println("Ping to " + chURLs.get(i) + " timedout!");
                    }
                    else
                    {
                        canConnect = true;
                    }
                }
                catch (Exception e)
                {
                    //e.printStackTrace();
                }

                if (chURLNames.get(i).contains("chicago2"))
                {
                    continue;
                }
                if (chURLs.get(i).contains("creeperrepo.net"))
                {
                    String jsonURL = "http://status.creeperrepo.net/fetchjson.php?l=" + chURLNames.get(i);
                    if (canConnect)
                    {

                        try
                        {
                            URL newURL = new URL(jsonURL);
                            HttpURLConnection urlConn = (HttpURLConnection) newURL.openConnection();
                            urlConn.setConnectTimeout(3000);
                            urlConn.setReadTimeout(5000);
                            final long startTime1 = System.currentTimeMillis();
                            urlConn.connect();

                            String ts;
                            BufferedReader re = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                            final long endTime = System.currentTimeMillis();
                            String jsons = re.readLine();
                            System.out.println("[" + chURLNames.get(i) + "] Connected to stats page in " + (endTime - startTime1) + "(MS)");
                            String test = "0";
                            int x = 0;
                            JSONObject jsonObj = new JSONObject(jsons);
                            test = jsonObj.getString("Bandwidth");
                            x = Integer.parseInt(test) * 100 / 1000000;
                            re.close();
                            urlConn.disconnect();
                            Load.add(x);
                            System.out.println(chURLNames.get(i) + " : " + x);
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                            Load.add(0);
                        }
                    }
                    else
                    {
                        Load.add(0);
                    }
                }
                else if (chURLs.get(i).contains("cursecdn.com"))
                {
                    System.out.println(chURLNames.get(i) + " : " + "N/A");
                    Load.add(-1);
                }
            }
        }

        for (Boolean test1 : tests)
        {
            if (test1)
            {
                Status.add(Colors.DARK_GREEN + "✓");
            }
            else
            {
                Status.add(Colors.RED + "✘");
            }
        }
        String test = null;

        if (Json)
        {
            test = Colors.DARK_GREEN + "✓";
        }
        else
        {
            test = Colors.RED + "✘";
        }

        Message.add("CreeperRepo: " + test + Colors.NORMAL + " Average Load " + (int) calculateAverage(Load) + "% | ");
        System.out.println(chRepos);
        System.out.println(Status);
        System.out.println(Load);

        for (int x = 0; x < chRepos.size(); x++)
        {
            Message.add(chRepos.get(x) + ": " + Status.get(x) + Colors.NORMAL + " " + (Load.get(x) == -1 ? "N/A" : Load.get(x)) + "% | ");
        }
        for (String s : Message)
        {
            sendMessage += s + "\t";
        }

        event.getChannel().send().message(sendMessage);
        final long endTime = System.currentTimeMillis();
        event.getUser().send().notice("Took me " + (endTime - startTime) / 1000 + " seconds");

    }

    private double calculateAverage (List<Integer> marks)
    {
        Integer sum = 0;
        int bad = 0;
        if (!marks.isEmpty())
        {
            for (Integer mark : marks)
            {
                if (mark != -1)
                {
                    sum += mark;
                }
                else
                    bad++;
            }
            return sum.doubleValue() / (marks.size() - bad);
        }
        else return 0.0;
    }

    public static String getRepoURL (String fullURL)
    {
        if (fullURL.contains("creeper"))
            return ".creeperrepo.net";
        return ".cursecdn.com";

    }

}
