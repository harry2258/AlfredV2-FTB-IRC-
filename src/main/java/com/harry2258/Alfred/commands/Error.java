package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Database.Create;
import com.harry2258.Alfred.Main;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.MessageUtils;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Hardik on 1/27/14.
 */
public class Error extends Command {
    public static HashMap<String, String> Diagnosis = new HashMap<>();
    public static HashMap<String, String> Suggestion = new HashMap<>();
    private static HashMap<String, String> Errors = new HashMap<>();
    private static Config config;
    private static PermissionManager manager;

    public Error() {

        super("Error", "Errors Database!", "Error [Error]");
    }

    private static Connection getConnection() throws SQLException {
        return Create.getConnection(config);
    }

    public static void getProblems(String pasteContent, MessageEvent event) {
        String errors = "";
        ArrayList<String> Diag = new ArrayList<>();
        ArrayList<String> Sugg = new ArrayList<>();
        Connection conn;
        try {

            conn = Main.database;

            PreparedStatement stmt = conn.prepareStatement("SELECT Problems.ID as ID, Diagnosis, Suggestion, CONCAT('%', GROUP_CONCAT(Error, '%')) as Errors FROM Problems, ProblemErrors WHERE Problems.ID = ProblemErrors.ProblemID GROUP BY ID, Diagnosis, Suggestion HAVING ? LIKE Errors");
            stmt.setString(1, pasteContent);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                errors += rs.getString("Errors").replaceAll("%", "") + " | ";
                Diag.add(rs.getString("Diagnosis"));
                Sugg.add(rs.getString("Suggestion"));
            }
            rs.close();
            stmt.close();
            if (!errors.isEmpty() || !Diag.isEmpty() || !Sugg.isEmpty()) {
                for (int i = 0; i < Diag.size(); i++) {
                    //MessageUtils.sendChannel(event, Colors.BOLD + "Diagnosis: " + Colors.NORMAL + Diag.get(i));
                    MessageUtils.sendChannel(event, Colors.BOLD + "Suggestion: " + Colors.NORMAL + Sugg.get(i));

                }
            } else {
                MessageUtils.sendChannel(event, "No match found in database!");
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    @Override
    public boolean execute(MessageEvent event) {

        String[] args = event.getMessage().split(" ");

        if (args.length <= 1) {
            return false;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
            if (args.length == 2 && args[1].equalsIgnoreCase("test")) {
                MessageUtils.sendUserNotice(event, "Trying to connect to database.");
                if (getConnection().isValid(5000)) {
                    Main.database = DriverManager.getConnection("jdbc:mysql://" + config.getDatabaseHost() + "/" + config.getDatabase(), config.getDatabaseUser(), config.getDatabasePass());
                    MessageUtils.sendChannel(event, "Connection to database was successful!");
                } else {
                    MessageUtils.sendChannel(event, "Could not connect to the database, Please recheck your configuration.");
                }
                return true;
            }

            if (args.length == 2 && args[1].equalsIgnoreCase("create")) {
                createTables();
                MessageUtils.sendChannel(event, "Created Table!");
                return true;
            }

            if (args.length == 2 && args[1].equalsIgnoreCase("review")) {
                MessageUtils.sendUserNotice(event, Colors.BOLD + "Errors: " + Colors.NORMAL + Errors.get(event.getUser().getNick()));
                MessageUtils.sendUserNotice(event, Colors.BOLD + "Diagnosis: " + Colors.NORMAL + Diagnosis.get(event.getUser().getNick()));
                MessageUtils.sendUserNotice(event, Colors.BOLD + "Suggestion: " + Colors.NORMAL + Suggestion.get(event.getUser().getNick()));
                return true;
            }

            if (args.length == 2 && args[1].equalsIgnoreCase("submit")) {
                ArrayList<String> test1 = new ArrayList<>();
                String[] test = Errors.get(event.getUser().getNick()).split(", ");
                Collections.addAll(test1, test);
                addProblem(Diagnosis.get(event.getUser().getNick()), Suggestion.get(event.getUser().getNick()), test1);
                Diagnosis.remove(event.getUser().getNick());
                Suggestion.remove(event.getUser().getNick());
                Errors.remove(event.getUser().getNick());
                MessageUtils.sendChannel(event, "Submissions successful!");
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            MessageUtils.sendChannel(event, e.toString());
            return true;
        }


        StringBuilder br = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            br.append(args[i]).append(" ");
        }
        Errors.remove(event.getUser().getNick());
        Errors.put(event.getUser().getNick(), br.toString());
        MessageUtils.sendUserNotice(event, "Added " + br.toString() + "to Error List!");
        return true;
    }

    @Override
    public void setConfig(Config config) {
        Error.config = config;
    }

    @Override
    public void setManager(PermissionManager manager) {
        Error.manager = manager;
    }

    private void createTables() throws SQLException {
        Connection conn = getConnection();
        try {
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS Problems ( ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, Diagnosis VARCHAR(255), Suggestion VARCHAR(255) ) ").execute();
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS ProblemErrors ( ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, ProblemID integer, Error VARCHAR(255) )").execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.close();
    }

    private void addProblem(String diagnosis, String suggestion, List<String> errors) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Problems (Diagnosis, Suggestion) VALUES (?, ?)");
        stmt.setString(1, diagnosis);
        stmt.setString(2, suggestion);
        stmt.execute();

        ResultSet rs = stmt.getGeneratedKeys();
        System.out.println(rs);

        if (rs.next()) {
            for (String error : errors) {
                PreparedStatement stmtError = conn.prepareStatement("INSERT INTO ProblemErrors (ProblemID, Error) VALUES (?,?)");
                stmtError.setInt(1, rs.getInt(1));
                stmtError.setString(2, error.trim());
                stmtError.execute();
                stmtError.close();
            }
        }

        rs.close();
        stmt.close();
        conn.close();
    }

    public void updateProblem(int ID, String diagnosis, String suggestion, List<String> errors) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE Problems (Diagnosis, Suggestion) VALUES (?, ?)");
        //stmt.setInt(1, ID);
        stmt.setString(1, diagnosis);
        stmt.setString(2, suggestion);
        stmt.execute();
        stmt.close();

        stmt = conn.prepareStatement("DELETE FROM ProblemErrors WHERE ProblemID=?");
        stmt.setInt(1, ID);
        stmt.execute();
        stmt.close();

        for (String error : errors) {
            PreparedStatement stmtError = conn.prepareStatement("INSERT INTO ProblemErrors (ProblemID, Error) VALUES (?,?)");
            stmtError.setInt(1, ID);
            stmtError.setString(2, error);
            stmtError.execute();
            stmtError.close();
        }

        conn.close();
    }
}
