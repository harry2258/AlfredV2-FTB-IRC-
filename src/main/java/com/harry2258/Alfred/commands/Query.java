package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.*;
import org.pircbotx.hooks.events.MessageEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Hardik on 1/21/14.
 */
public class Query extends Command {

    private Config config;
    private PermissionManager manager;

    public Query() {
        super("Query", "Query a MC server for MOTD and players!", "Query [MC server IP]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        String result = null;
        if (args.length == 2) {
            try {
                result = Utils.checkServerStatus(InetAddress.getByName(args[1]), 25565);
            } catch (UnknownHostException ex) {
                MessageUtils.sendChannel(event, "Please verify if the server is set to default port (25565) else use " + config.getTrigger() + "query [ip] [port]");
                return false;
            }
        } else if (args.length == 3) {
            try {
                result = Utils.checkServerStatus(InetAddress.getByName(args[1]), Integer.valueOf(args[2]));
            } catch (UnknownHostException ex) {
                return false;
            }
        }
        MessageUtils.sendChannel(event, result);
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
