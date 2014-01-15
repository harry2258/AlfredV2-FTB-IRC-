package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Hardik on 1/14/14.
 */
public class Insult extends Command {
    private Config config;
    private PermissionManager manager;

    public Insult() {
        super("Insult", "SHAKESPEAREAN INSULTS!", "Insult");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String insult1 = null;
        do {
            try {
                URL insult;
                insult = new URL("http://www.pangloss.com/seidel/Shaker/index.html?");
                BufferedReader br = new BufferedReader(new InputStreamReader(insult.openStream()));
                for (int i = 0; i < 16; ++i)
                    br.readLine();
                String line = br.readLine();
                String y = line.replaceAll("</font>", " ").replace("</form><hr>", "");
                insult1 = y;
                while (line != null) {
                    System.out.println(line);
                    line = br.readLine();
                }
                br.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } while (insult1.isEmpty());
        event.getChannel().send().message(insult1);
        return true;
    }

    @Override
    public void setConfig(Config config) {

    }

    @Override
    public void setManager(PermissionManager manager) {

    }
}
