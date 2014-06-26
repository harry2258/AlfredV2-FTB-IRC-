package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.*;

/**
 * Created by Hardik on 2/28/14.
 */
public class Copy extends Command {
    private Config config;
    private PermissionManager manager;

    public Copy() {
        super("Copy", "Copy a custom command from another channel!", "Copy [#Channel] [Custom command name]");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        if (args.length == 3) {
            Channel targetChan = event.getBot().getUserChannelDao().getChannel(args[1]);
            String commandname = args[2];
            File command = new File("commands/" + targetChan.getName() + "/" + commandname + ".cmd");
            if (command.exists()) {
                File currentChan = new File("commands/" + event.getChannel().getName() + "/" + commandname + ".cmd");
                if (!currentChan.exists()) {
                    currentChan.getParentFile().mkdirs();
                    currentChan.createNewFile();
                }

                InputStream inStream = new FileInputStream(command);
                OutputStream outStream = new FileOutputStream(currentChan);

                byte[] buffer = new byte[1024];

                int length;
                //copy the file content in bytes
                while ((length = inStream.read(buffer)) > 0) {

                    outStream.write(buffer, 0, length);

                }
                inStream.close();
                outStream.close();
                event.getChannel().send().message("Copied the command successfully!");
            } else {
                event.getChannel().send().message("There is no custom command by that names in the channel " + targetChan.getName());
            }

        } else {
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
