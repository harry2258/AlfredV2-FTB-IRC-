package com.harry2258.Alfred.Database;

import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Hardik at 1:16 AM on 8/7/2014.
 */
public class Create {

    public static boolean CreateTables(Config config) {

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + config.getDatabaseHost() + "/" + config.getDatabase(), config.getDatabaseUser(), config.getDatabasePass());
            //conn.prepareStatement("CREATE TABLE IF NOT EXISTS Channels ( ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, Channel VARCHAR(255) ) ").execute();
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS Channel_Permissions ( Channel VARCHAR(255) NOT NULL PRIMARY KEY, Admins VARCHAR(255), Mods VARCHAR(255), ModPerms VARCHAR(255), Everyone VARCHAR(255), URL VARCHAR(30) ) ").execute();
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS Rejoin_Channels (Channel VARCHAR(255) NOT NULL PRIMARY KEY)").execute();
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS Bot (`Nick` VARCHAR(255) NOT NULL PRIMARY KEY, `Password` VARCHAR(255), `Username` VARCHAR(255), `Ident` VARCHAR(255), `Bot_Trigger` VARCHAR(255), `Reconnect` BOOLEAN, `Accept_Invite` BOOLEAN, `Rejoin_Channels` BOOLEAN, `CTCP_Finger_Reply` VARCHAR(255), `CTCP_Version_Reply` VARCHAR(225))").execute();
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS Network_Settings (Server_Host VARCHAR(255), Server_Port VARCHAR(255), Server_Password VARCHAR(255), Use_SSL BOOLEAN, Permissions_Denied VARCHAR(255), Verify_SSL BOOLEAN, Enable_Chat_Socket BOOLEAN, Chat_Socket_Port INTEGER)").execute();
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS Misc (Twitter BOOLEAN, Reddit BOOLEAN, Check_Update BOOLEAN, Update_Channel VARCHAR(255), Update_Interval INTEGER, Weather_API_KEY VARCHAR(255))").execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean AddChannel(String Channel, Connection conn) {
        String Permissions = "{\"Perms\":{\"Mods\":[\"niel\"], \"ModPerms\": [\"command.custom\", \"command.info\"], \"Admins\":[\"Gaz492\"], \"Everyone\": [\"command.mcstatus\", \"command.chstatus\"]}}";

        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT IGNORE INTO `Channel_Permissions` (`Channel`, `Permission`, `URL`) VALUES (?, ?, 'none')");
            stmt.setString(1, Channel);
            stmt.setString(2, Permissions);
            stmt.execute();
            stmt.close();

            PreparedStatement stmt1 = conn.prepareStatement("INSERT IGNORE INTO `Rejoin_Channels` (`Channel`) VALUES (?)");
            stmt1.setString(1, Channel);
            stmt1.execute();
            stmt1.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void RemoveChannel(String Channel, Config config, PermissionManager manager) throws SQLException {
        Connection conn = getConnection(config);
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM `Rejoin_Channels` WHERE `Channel` = ? LIMIT 1;");
        stmt.setString(1, Channel);
        stmt.execute();
        stmt.close();
    }

    public static Connection getConnection(Config config) throws SQLException {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String host = config.getDatabaseHost();
        String user = config.getDatabaseUser();
        String pass = config.getDatabasePass();
        String database = config.getDatabase();

        return DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, user, pass);
    }
}
