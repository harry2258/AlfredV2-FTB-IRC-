package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.MessageUtils;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Nick extends Command {
    private Config config;
    private PermissionManager manager;

    public Nick() {
        super("Nick", "changes the bot's nickname", "Nick newnickname");
    }

    @Override
    public boolean execute(MessageEvent event) {
        if (event.getUser() == null) {
            return false;
        }
        if (PermissionManager.hasExec(event.getUser().getNick())) {
            String[] args = event.getMessage().split(" ");
            if (args.length >= 1) {
                event.getBot().sendIRC().changeNick(event.getMessage().split(" ")[1]);
                if (config.useDatabase) {
                    try {
                        PreparedStatement stmt = Main.database.prepareStatement("UPDATE  `Bot` SET  `Nick` =  ? WHERE  `Nick` =  ?;");
                        stmt.setString(1, args[1]);
                        stmt.setString(2, event.getBot().getNick());
                        stmt.execute();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        MessageUtils.sendUserNotice(event, "Could not update the database!");
                    }
                }
                return true;
            }
        }
        return false;
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
