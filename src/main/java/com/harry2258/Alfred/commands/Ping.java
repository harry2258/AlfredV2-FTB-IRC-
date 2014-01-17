package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Hardik on 1/14/14.
 * Copy from old Alfred :D
 */
public class Ping extends Command {
    private Config config;
    private PermissionManager manager;

    public Ping() {
        super("Ping", "Pong!", "Ping [website]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String returns = "";
        Long time = Long.valueOf("0");
        try {
            String[] args = event.getMessage().split(" ");
            if (args.length == 2) {
                String host = args[1];
                int port = Integer.valueOf("80");

                Long start = System.currentTimeMillis();
                Socket s = new Socket(InetAddress.getByName(host), port);
                s.close();
                time = System.currentTimeMillis() - start;
                returns = "Response time: " + time + " miliseconds";
                event.getChannel().send().message(returns);
                return true;
            }
            String host = args[1];
            int port = Integer.valueOf(args[2]);
            Long start = System.currentTimeMillis();
            Socket s = new Socket(InetAddress.getByName(host), port);
            s.close();
            time = System.currentTimeMillis() - start;
            returns = "Response time: " + time + " miliseconds";
            event.getChannel().send().message(returns);

        } catch (Exception ex) {
            return false;
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
