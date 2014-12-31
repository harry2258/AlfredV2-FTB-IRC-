/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.api;

import com.harry2258.Alfred.Database.Create;
import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.commands.Ignore;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO LOMBOKIZE!!!
public class Config {

    public boolean useDatabase;
    public boolean updateDatabase;
    private boolean debug;
    private boolean autoNickChange;
    private boolean autoReconnectServer;
    private boolean autoRejoinChannel;
    private boolean autoAcceptInvite;
    private boolean useSSL;
    private boolean verifySSL;
    private boolean enableChatSocket;
    private boolean TwitterEnabled;
    private boolean RedditEnabled;
    private boolean updater;
    private String trigger;
    private String serverHostame;
    private String serverPassword;
    private String serverPort;
    private String botNickname;
    private String botUsername;
    private String botIdent;
    private String botPassword;
    private String ctcpFinger;
    private String ctcpVersion;
    private String DBtable;
    private String DBhost;
    private String DBuser;
    private String DBpass;
    private String updatechan;
    private String Weather;
    private List<String> channels;
    private List<String> loggedChannels;
    private String permissionDenied;
    private int chatSocketPort;
    private int UpdateInterval;


    public void load() {
        try {
            Properties properties = new Properties();

            properties.load(new FileInputStream("bot.properties"));
            this.setBotNickname(properties.getProperty("bot-nickname"));
            this.setBotUsername(properties.getProperty("bot-username"));
            this.setBotIdent(properties.getProperty("bot-ident"));
            this.setBotPassword(properties.getProperty("bot-password"));
            this.setTrigger(properties.getProperty("bot-trigger"));
            this.setCtcpFinger(properties.getProperty("ctcp-finger-reply"));
            this.setCtcpVersion(properties.getProperty("ctcp-version-reply"));
            this.setDebug(Boolean.parseBoolean(properties.getProperty("debug")));
            this.setAutoNickChange(Boolean.parseBoolean(properties.getProperty("auto-nickchange")));
            this.setAutoReconnectServer(Boolean.parseBoolean(properties.getProperty("auto-reconnect")));
            this.setAutoRejoinChannel(Boolean.parseBoolean(properties.getProperty("auto-rejoin")));
            this.setAutoAcceptInvite(Boolean.parseBoolean(properties.getProperty("auto-accept-invite")));
            this.setEnableChatSocket(Boolean.parseBoolean(properties.getProperty("enable-chat-socket")));
            this.setTwitterEnabled(Boolean.parseBoolean(properties.getProperty("Twitter")));
            this.setRedditEnabled(Boolean.parseBoolean(properties.getProperty("Reddit")));
            this.setChatSocketPort(Integer.parseInt(properties.getProperty("chat-socket-port")));
            this.setChannels(Arrays.asList(properties.getProperty("channels").split(" ")));
            this.setLoggedChannels(Arrays.asList(properties.getProperty("channels-log").split(" ")));
            this.setUseSSL(Boolean.parseBoolean(properties.getProperty("use-ssl")));
            this.setVerifySSL(Boolean.parseBoolean(properties.getProperty("verify-ssl")));
            this.setServerHostame(properties.getProperty("server-hostname"));
            this.setServerPort(properties.getProperty("server-port"));
            this.setServerPassword(properties.getProperty("server-password"));
            this.setPermissionDenied(properties.getProperty("permission-denied"));
            this.setUpdater(Boolean.parseBoolean(properties.getProperty("check-update")));
            this.setUpdateChan(properties.getProperty("update-channel"));
            this.setUpdateInterval(Integer.parseInt(properties.getProperty("update-interval")));
            this.DBHostName(properties.getProperty("Host"));
            this.DBUserName(properties.getProperty("Username"));
            this.DBPassName(properties.getProperty("Password"));
            this.DB(properties.getProperty("Database"));
            this.WeatherAPI(properties.getProperty("Weather-API-Key"));

        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean UseDatabase() {
        try {
            Properties properties = new Properties();
            File config = new File("bot.properties");
            if (!config.exists()) {
                System.out.println("[!!] No configuration file found! generating a new one! [!!]");
                BufferedReader s = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/bot.properties")));
                String tmp = "";
                config.createNewFile();
                BufferedWriter out = new BufferedWriter(new FileWriter(config));
                while ((tmp = s.readLine()) != null) {
                    out.write(tmp);
                    out.flush();
                    out.newLine();
                }
                out.close();
                System.out.println("[!!] Done! [!!]");
            }
            properties.load(new FileInputStream("bot.properties"));
            usingDatabase(Boolean.valueOf(properties.getProperty("Use-Database")));
            System.out.println("Using Database: " + useDatabase);

            if (useDatabase) {
                this.DBHostName(properties.getProperty("Host"));
                this.DBUserName(properties.getProperty("Username"));
                this.DBPassName(properties.getProperty("Password"));
                this.DB(properties.getProperty("Database"));

                if (Boolean.valueOf(properties.getProperty("Update-Database"))) {
                    updateDatabase = true;
                }
            }

            System.out.println("Update Database: " + updateDatabase);
            return useDatabase;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void loadDatabase(Connection conn) {

        try {
            //Check if tables exists, if not create them.
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS Channel_Permissions (Channel VARCHAR(255) NOT NULL PRIMARY KEY, Permission LONGTEXT, URL VARCHAR(4))").execute();
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS Rejoin_Channels (Channel VARCHAR(255) NOT NULL PRIMARY KEY)").execute();
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS Bot (`Nick` VARCHAR(255) NOT NULL PRIMARY KEY, `Password` VARCHAR(255), `Username` VARCHAR(255), `Ident` VARCHAR(255), `Bot_Trigger` VARCHAR(255), `Reconnect` VARCHAR(5), `Accept_Invite` VARCHAR(5), `Rejoin_Channels` VARCHAR(5), `CTCP_Finger_Reply` VARCHAR(1000), `CTCP_Version_Reply` VARCHAR(1000))").execute();
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS Network_Settings (Server_Host VARCHAR(255) NOT NULL PRIMARY KEY, Server_Port VARCHAR(255), Server_Password VARCHAR(255), Use_SSL VARCHAR(5), Permissions_Denied VARCHAR(5000), Verify_SSL VARCHAR(5), Enable_Chat_Socket VARCHAR(5), Chat_Socket_Port INTEGER)").execute();
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS Misc (Bot VARCHAR(255) NOT NULL PRIMARY KEY, Twitter VARCHAR(5), Reddit VARCHAR(5), Check_Update VARCHAR(5), Update_Channel VARCHAR(255), Update_Interval INTEGER, Weather_API_KEY VARCHAR(255))").execute();
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS Ignored_Users (`User` VARCHAR(255) NOT NULL PRIMARY KEY, `Ignored_By` VARCHAR(255), `User_Nick` VARCHAR(255), `Date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)").execute();
        } catch (SQLException s) {
            s.printStackTrace();
        }

        try {
            if (updateDatabase) {
                UpdateDatabase(conn);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //Bot Settings
            PreparedStatement stmt = conn.prepareStatement("SELECT *  FROM `Bot`");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                this.setBotNickname(rs.getString("Nick"));
                this.setBotPassword(rs.getString("Password"));
                this.setBotUsername(rs.getString("Username"));
                this.setBotIdent(rs.getString("Ident"));
                this.setTrigger(rs.getString("Bot_Trigger"));
                this.setAutoReconnectServer(Boolean.parseBoolean(rs.getString("Reconnect")));
                this.setAutoAcceptInvite(Boolean.parseBoolean(rs.getString("Accept_Invite")));
                this.setAutoRejoinChannel(Boolean.parseBoolean(rs.getString("Rejoin_Channels")));
                this.setCtcpFinger(rs.getString("CTCP_Finger_Reply"));
                this.setCtcpVersion(rs.getString("CTCP_Version_Reply"));
            }
            rs.close();
            stmt.close();

            //Network Settings
            PreparedStatement stmt1 = conn.prepareStatement("SELECT * FROM `Network_Settings`");
            ResultSet rs1 = stmt1.executeQuery();
            while (rs1.next()) {
                this.setServerHostame(rs1.getString("Server_Host"));
                this.setServerPassword(rs1.getString("Server_Password"));
                this.setServerPort(rs1.getString("Server_Port"));
                this.setUseSSL(Boolean.parseBoolean(rs1.getString("Use_SSL")));
                this.setVerifySSL(Boolean.parseBoolean(rs1.getString("Verify_SSL")));
                this.setEnableChatSocket(Boolean.parseBoolean(rs1.getString("Enable_Chat_Socket")));
                this.setChatSocketPort(rs1.getInt("Chat_Socket_port"));
                this.setPermissionDenied(rs1.getString("Permissions_Denied"));
            }
            rs1.close();
            stmt1.close();

            //Misc. Settings
            PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM `Misc`");
            ResultSet rs2 = stmt2.executeQuery();
            while (rs2.next()) {
                this.setTwitterEnabled(Boolean.parseBoolean(rs2.getString("Twitter")));
                this.setRedditEnabled(Boolean.parseBoolean(rs2.getString("Reddit")));
                this.setUpdateChan(rs2.getString("Update_Channel"));
                this.setUpdater(Boolean.parseBoolean(rs2.getString("Check_Update")));
                this.setUpdateInterval(rs2.getInt("Update_Interval"));
                this.WeatherAPI(rs2.getString("Weather_API_KEY"));
            }
            rs2.close();
            stmt2.close();

            //Ignored Users. Shame on them :C
            PreparedStatement stmt3 = conn.prepareStatement("SELECT * FROM `Ignored_Users`");
            ResultSet rs3 = stmt3.executeQuery();
            int i = 0;
            while (rs3.next()) {
                i = i + 1;
                Ignore.ignored.add(rs3.getString("User"));
            }
            System.out.println("Added " + String.valueOf(i) + " user to ignore list!");

            rs3.close();
            stmt3.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UpdateDatabase(Connection conn) {
        try {
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream("bot.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Nukes basic settings, Keeps Channel perms, Rejoin channels and Ignored Users.
            conn.prepareStatement("TRUNCATE Bot").execute();
            conn.prepareStatement("TRUNCATE Network_Settings").execute();
            conn.prepareStatement("TRUNCATE Misc").execute();

            //Update them from bot.properties
            String bot = String.format("INSERT INTO `Bot` (`Nick`, `Password`, `Username`, `Ident`, `Bot_Trigger`, `Reconnect`, `Accept_Invite`, `Rejoin_Channels`, `CTCP_Finger_Reply`, `CTCP_Version_Reply`) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                    properties.getProperty("bot-nickname"),
                    properties.getProperty("bot-password"),
                    properties.getProperty("bot-username"),
                    properties.getProperty("bot-ident"),
                    properties.getProperty("bot-trigger"),
                    properties.getProperty("auto-reconnect"),
                    properties.getProperty("auto-accept-invite"),
                    properties.getProperty("auto-rejoin"),
                    properties.getProperty("ctcp-finger-reply"),
                    properties.getProperty("ctcp-version-reply"));

            String Network = String.format("INSERT INTO Network_Settings (`Server_Host`, `Server_Port`, `Server_Password`, `Use_SSL`, `Permissions_Denied`, `Verify_SSL`, `Enable_Chat_Socket`, `Chat_Socket_Port`) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                    properties.getProperty("server-hostname"),
                    properties.getProperty("server-port"),
                    properties.getProperty("server-password"),
                    properties.getProperty("use-ssl"),
                    properties.getProperty("permission-denied").replaceAll("'", "''").replaceAll("`", "``"),
                    properties.getProperty("verify-ssl"),
                    properties.getProperty("enable-chat-socket"),
                    properties.getProperty("chat-socket-port"));

            String Misc = String.format("INSERT INTO Misc (`Bot`, `Twitter`, `Reddit`, `Check_Update`, `Update_Channel`, `Update_Interval`, `Weather_API_KEY`) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                    properties.getProperty("bot-nickname"),
                    properties.getProperty("Twitter"),
                    properties.getProperty("Reddit"),
                    properties.getProperty("check-update"),
                    properties.getProperty("update-channel"),
                    properties.getProperty("update-interval"),
                    properties.getProperty("Weather-API-Key"));

            conn.prepareStatement(bot).execute();
            conn.prepareStatement(Network).execute();
            conn.prepareStatement(Misc).execute();

            //Add any new channels to the Auto-Join list.
            for (String channel : Arrays.asList(properties.getProperty("channels").split(" "))) {
                if (Create.AddChannel(channel, conn)) System.out.println("Created Perms for " + channel);
                else System.out.println("Could not create permissions for " + channel);
            }


            //Done updating DB, don't want to leave it on. Do we?
            properties.setProperty("Update-Database", "false");
            properties.store(new FileOutputStream("bot.properties"), null);

        } catch (SQLException e) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * @return the autoNickChange
     */
    public boolean isAutoNickChange() {
        return autoNickChange;
    }

    /**
     * @param autoNickChange the autoNickChange to set
     */
    public void setAutoNickChange(boolean autoNickChange) {
        this.autoNickChange = autoNickChange;
    }

    /**
     * @return the autoReconnectServer
     */
    public boolean isAutoReconnectServer() {
        return autoReconnectServer;
    }

    /**
     * @param autoReconnectServer the autoReconnectServer to set
     */
    public void setAutoReconnectServer(boolean autoReconnectServer) {
        this.autoReconnectServer = autoReconnectServer;
    }

    /**
     * @return the autoRejoinChannel
     */
    public boolean isAutoRejoinChannel() {
        return autoRejoinChannel;
    }

    /**
     * @param autoRejoinChannel the autoRejoinChannel to set
     */
    public void setAutoRejoinChannel(boolean autoRejoinChannel) {
        this.autoRejoinChannel = autoRejoinChannel;
    }

    /**
     * @return the autoAcceptInvite
     */
    public boolean isAutoAcceptInvite() {
        return autoAcceptInvite;
    }

    /**
     * @param autoAcceptInvite the autoAcceptInvite to set
     */
    public void setAutoAcceptInvite(boolean autoAcceptInvite) {
        this.autoAcceptInvite = autoAcceptInvite;
    }

    /**
     * @return the useSSL
     */
    public boolean isUseSSL() {
        return useSSL;
    }

    /**
     * @param useSSL the useSSL to set
     */
    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    /**
     * @return the verifySSL
     */
    public boolean isVerifySSL() {
        return verifySSL;
    }

    /**
     * @param verifySSL the verifySSL to set
     */
    public void setVerifySSL(boolean verifySSL) {
        this.verifySSL = verifySSL;
    }

    /**
     * @return the trigger
     */
    public String getTrigger() {
        return trigger;
    }

    /**
     * @param trigger the trigger to set
     */
    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    /**
     * @return the serverHostame
     */
    public String getServerHostame() {
        return serverHostame;
    }

    /**
     * @param serverHostame the serverHostame to set
     */
    public void setServerHostame(String serverHostame) {
        this.serverHostame = serverHostame;
    }

    /**
     * @return the serverPassword
     */
    public String getServerPassword() {
        return serverPassword;
    }

    /**
     * @param serverPassword the serverPassword to set
     */
    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }

    /**
     * @return the serverPort
     */
    public String getServerPort() {
        return serverPort;
    }

    /**
     * @param serverPort the serverPort to set
     */
    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * @return the botNickname
     */
    public String getBotNickname() {
        return botNickname;
    }

    /**
     * @param botNickname the botNickname to set
     */
    public void setBotNickname(String botNickname) {
        this.botNickname = botNickname;
    }

    /**
     * @return the botUsername
     */
    public String getBotUsername() {
        return botUsername;
    }

    /**
     * @param botUsername the botUsername to set
     */
    public void setBotUsername(String botUsername) {
        this.botUsername = botUsername;
    }

    /**
     * @return the botIdent
     */
    public String getBotIdent() {
        return botIdent;
    }

    /**
     * @param botIdent the botIdent to set
     */
    public void setBotIdent(String botIdent) {
        this.botIdent = botIdent;
    }

    /**
     * @return the botPassword
     */
    public String getBotPassword() {
        return botPassword;
    }

    /**
     * @param botPassword the botPassword to set
     */
    public void setBotPassword(String botPassword) {
        this.botPassword = botPassword;
    }

    /**
     * @return the ctcpFinger
     */
    public String getCtcpFinger() {
        return ctcpFinger;
    }

    /**
     * @param ctcpFinger the ctcpFinger to set
     */
    public void setCtcpFinger(String ctcpFinger) {
        this.ctcpFinger = ctcpFinger;
    }

    /**
     * @return the ctcpVersion
     */
    public String getCtcpVersion() {
        return ctcpVersion;
    }

    /**
     * @param ctcpVersion the ctcpVersion to set
     */
    public void setCtcpVersion(String ctcpVersion) {
        this.ctcpVersion = ctcpVersion;
    }

    /**
     * @return the channels
     */
    public List<String> getChannels() {
        return channels;
    }

    /**
     * @param channels the channels to set
     */
    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

    /**
     * @return the loggedChannels
     */
    public List<String> getLoggedChannels() {
        return loggedChannels;
    }

    /**
     * @param loggedChannels the loggedChannels to set
     */
    public void setLoggedChannels(List<String> loggedChannels) {
        this.loggedChannels = loggedChannels;
    }

    /**
     * @return the permissionDenied
     */
    public String getPermissionDenied() {
        return permissionDenied;
    }

    /**
     * @param permissionDenied the permissionDenied to set
     */
    public void setPermissionDenied(String permissionDenied) {
        this.permissionDenied = permissionDenied;
    }

    /**
     * If the socket chat listener should be enabled
     *
     * @return If the socket chat listener should be enabled
     */
    public boolean isEnableChatSocket() {
        return enableChatSocket;
    }

    /**
     * Enable or disable the socket chat listener
     *
     * @param enableChatSocket Enable or disable the socket chat listener
     */
    public void setEnableChatSocket(boolean enableChatSocket) {
        this.enableChatSocket = enableChatSocket;
    }

    /**
     * @return chatSocketPort
     */
    public int getChatSocketPort() {
        return chatSocketPort;
    }

    /**
     * @param chatSocketPort the chatSocketPort to set
     */
    public void setChatSocketPort(int chatSocketPort) {
        this.chatSocketPort = chatSocketPort;
    }

    /**
     * @return isTwitterEnabled
     */
    public boolean isTwitterEnabled() {
        return TwitterEnabled;
    }

    /**
     * @param Twitterenable Enable or Disable twitter
     */
    private void setTwitterEnabled(boolean Twitterenable) {
        this.TwitterEnabled = Twitterenable;
    }

    /**
     * @return isRedditEnabled
     */
    public boolean isRedditEnabled() {
        return RedditEnabled;
    }

    /**
     * @param Redditenabled Enable ot Disable Reddit.
     */
    private void setRedditEnabled(boolean Redditenabled) {
        this.RedditEnabled = Redditenabled;
    }

    /**
     * @return the getDatabase name
     */
    public String getDatabase() {
        return DBtable;
    }

    /**
     * @param database Database name.
     */
    private void DB(String database) {
        this.DBtable = database;
    }

    /**
     * @return the getDatabaseHost
     */
    public String getDatabaseHost() {
        return DBhost;
    }

    /**
     * @param host Database host, IP/URL
     */
    private void DBHostName(String host) {
        this.DBhost = host;
    }

    /**
     * @return the getDatabaseUser
     */
    public String getDatabaseUser() {
        return DBuser;
    }

    /**
     * @param user Username for Database
     */
    private void DBUserName(String user) {
        this.DBuser = user;
    }

    /**
     * @return the getDatabasePass
     */
    public String getDatabasePass() {
        return DBpass;
    }

    /**
     * @param pass Password for Database
     */
    private void DBPassName(String pass) {
        this.DBpass = pass;
    }

    /**
     * @param Updater Enable or Disable update checker
     */
    private void setUpdater(boolean Updater) {
        this.updater = Updater;
    }

    /**
     * @return the UpdateChecker
     */
    public boolean UpdaterChecker() {
        return updater;
    }

    /**
     * @param updateChan Channel to announce updates in.
     */
    private void setUpdateChan(String updateChan) {
        this.updatechan = updateChan;
    }

    /**
     * @return the Updaterchanel
     */
    public String Updaterchannel() {
        return updatechan;
    }

    /**
     * @return getUpdateInterval
     */
    public int getUpdateInterval() {
        return UpdateInterval;
    }

    /**
     * How often to check for Updates
     *
     * @param updateInterval Update timer.
     */
    private void setUpdateInterval(int updateInterval) {
        UpdateInterval = updateInterval;
    }

    /**
     * @param Key WUnderground API Key
     */
    private void WeatherAPI(String Key) {
        this.Weather = Key;
    }

    /**
     * @return the getWeatherKey
     */
    public String getWeatherKey() {
        return Weather;
    }

    /**
     * @param use Using the database for configuration or not.
     */
    public void usingDatabase(Boolean use) {
        this.useDatabase = use;
    }
}