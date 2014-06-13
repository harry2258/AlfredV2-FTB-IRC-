package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.Misc.Reddit;
import com.harry2258.Alfred.Misc.Twitter;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.runnables.ChatSocketHandler;
import com.harry2258.Alfred.runnables.ChatSocketListener;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.File;
import java.util.ArrayList;
//import org.tanukisoftware.wrapper.WrapperManager;

/**
 * Created by Hardik at 7:03 PM on 5/2/14.
 */
public class Restart extends Command {
    private Config config;
    private PermissionManager manager;

    public Restart() {
        super("Restart", "Restarts the bot!");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        System.out.println("Restarting Bot!");
        if (PermissionManager.hasExec(event.getUser(), event)) {
            if (config.isEnableChatSocket()) {
                ChatSocketListener.kill();
                ChatSocketHandler.kill();
            }
            if (config.isEnabledTwitter()) {
                Twitter.kill();
            }
            if (config.isRedditEnabled()) {
                Reddit.kill();
            }
            if (config.UpdaterChecker()) {
                com.harry2258.Alfred.Misc.Update.kill();
            }
            final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            final File currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            if (!currentJar.getName().endsWith(".jar")) {
                return false;
            }

            final ArrayList<String> command = new ArrayList<>();
            command.add(javaBin);
            command.add("-jar");
            command.add(currentJar.getPath());
            //final ProcessBuilder builder = new ProcessBuilder(command);
            System.out.println(command);
            System.out.println(command.toString().replaceAll("\\]\\[", ""));
            Runtime.getRuntime().exec(command.toString());
            //builder.start();
            System.exit(1);
        }
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
