package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Hardik on 1/17/14.
 */
public class Wiki extends Command {

    private Config config;
    private PermissionManager manager;

    public Wiki() {
        super("Wiki", "Wiki FTB stuff", "Wiki [Query]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        StringBuilder sb = new StringBuilder();
        String[] args = event.getMessage().split(" ");

        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }

        String message = sb.toString().trim();
        String y = "http://wiki.feed-the-beast.com/" + message;
        //http://wiki.feed-the-beast.com/index.php?search=
        String x = y.replaceAll(" ", "_");
        String finalurl = null;
        String nope = null;
        String tmp = null;
        String no = "There were no results matching the query.";
        try {
            URL read;
            read = new URL(x);
            BufferedReader xx = new BufferedReader(new InputStreamReader(read.openStream()));
            while ((tmp = xx.readLine()) != null) {
                if (tmp.contains(" This block is part of vanilla Minecraft. More information can be found at the ")) {
                    String Vanilla = ("http://minecraft.gamepedia.com/" + message).replaceAll(" ", "_");
                    event.getChannel().send().message(message + "(Vanilla): " + Vanilla);
                    return true;
                }
            }

            xx.close();

        } catch (Exception e) {
            //event.getChannel().send().message("http://youtu.be/gvdf5n-zI14  |  Please check your spelling!  |  CAPS MATTER e.g POTATO, Potato & PoTaTo are three different things!  | Since the Wiki is being updated, That block might not be added yet.");
            try {
                String sear = ("http://wiki.feed-the-beast.com/index.php?search=" + message).replaceAll(" ", "_");
                URL search;
                search = new URL(sear);
                BufferedReader zz = new BufferedReader(new InputStreamReader(search.openStream()));
                while ((tmp = zz.readLine()) != null) {
                    if (tmp.contains("There were no results matching the query.")) {
                        event.getChannel().send().message("http://youtu.be/gvdf5n-zI14  |  Please check your spelling!  |  CAPS MATTER e.g POTATO, Potato & PoTaTo are three different things!  | Since the Wiki is being updated, That page might not be added yet.");
                        return true;
                    }
                }

                zz.close();

                URL shorten;
                shorten = new URL("http://is.gd/create.php?format=simple&url=" + sear);
                BufferedReader br = new BufferedReader(new InputStreamReader(shorten.openStream()));
                finalurl = br.readLine();
                br.close();

                event.getChannel().send().message("Search result: " + finalurl);
                return true;
            } catch (Exception ex) {
                System.out.println(ex);
            }
            return true;
        }

        event.getChannel().send().message(message + ": " + x);
        return true;
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
