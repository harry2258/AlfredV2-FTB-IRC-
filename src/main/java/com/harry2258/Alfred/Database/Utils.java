package com.harry2258.Alfred.Database;

import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Hardik at 12:28 PM at 12:30 PM on 10/24/2014.
 */
public class Utils {
    private Config config;
    private PermissionManager manager;

    public static Connection getConnection(Config config) {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + config.getDatabaseHost() + "/" + config.getDatabase(), config.getDatabaseUser(), config.getDatabasePass());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}