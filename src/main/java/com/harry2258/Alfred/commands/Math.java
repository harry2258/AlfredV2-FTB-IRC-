package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Created by Hardik at 10:00 PM on 12/19/2014.
 */
public class Math extends Command {

    private Config config;
    private PermissionManager manager;

    public Math() {
        super("Math", "Does Math then you're too lazy too!", "Math [Operation]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }

        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        event.respond(String.valueOf(engine.eval(sb.toString().trim())));
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
