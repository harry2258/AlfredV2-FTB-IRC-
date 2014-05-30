package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
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
    public static HashMap<String, String> Errors = new HashMap<>();
    public static HashMap<String, String> Suggestion = new HashMap<>();
    ArrayList<String> errorlist = new ArrayList<>();

    public Error() {

        super("Error", "Errors Database!", "Error [Error]");
    }

    private static Config config;
    private PermissionManager manager;

    @Override
    public boolean execute(MessageEvent event) {

        String[] args = event.getMessage().split(" ");

        if (args.length <= 1) {
            return false;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
            if (args.length == 2 && args[1].equalsIgnoreCase("test")) {
                event.getUser().send().notice("Trying to connect to database!");
                if (getConnection().isValid(5000)) {
                    event.getChannel().send().message("Connection to database was successful!");
                } else {
                    event.getChannel().send().message("Could not connect to the database, Please check your bot.properties files.");
                }
                return true;
            }

            if (args.length == 2 && args[1].equalsIgnoreCase("create")) {
                createTables();
                event.getChannel().send().message("Created Table!");
                return true;
            }

            if (args.length == 2 && args[1].equalsIgnoreCase("review")) {
                event.getUser().send().notice(Colors.BOLD + "Errors: " + Colors.NORMAL + Errors.get(event.getUser().getNick()));
                event.getUser().send().notice(Colors.BOLD + "Diagnosis: " + Colors.NORMAL + Diagnosis.get(event.getUser().getNick()));
                event.getUser().send().notice(Colors.BOLD + "Suggestion: " + Colors.NORMAL + Suggestion.get(event.getUser().getNick()));
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
                event.getChannel().send().message("Submissions successful!");
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            event.getChannel().send().message(e.toString());
            return true;
        }


        StringBuilder br = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            br.append(args[i]).append(" ");
        }
        Errors.remove(event.getUser().getNick());
        Errors.put(event.getUser().getNick(), br.toString());
        event.getUser().send().notice("Added " + br.toString() + "to Error List!");
        return true;
    }

    @Override
    public void setConfig(Config config) {
        Error.config = config;
    }

    @Override
    public void setManager(PermissionManager manager) {
        this.manager = manager;
    }

    private static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String host = config.DatabaseHost();
        String user = config.DatabaseUser();
        String pass = config.DatabasePass();
        String database = config.Database();
        return DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, user, pass);
    }

    public void createTables() throws SQLException {
        Connection conn = getConnection();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS Problems ( ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, Diagnosis VARCHAR(255), Suggestion VARCHAR(255) ) ").execute();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS ProblemErrors ( ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, ProblemID integer, Error VARCHAR(255) )").execute();
        conn.close();
    }

    public void addProblem(String diagnosis, String suggestion, List<String> errors) throws SQLException {
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
        stmt.setInt(1, ID);
        stmt.setString(2, diagnosis);
        stmt.setString(3, suggestion);
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

    public static void getProblems(String pasteContent, MessageEvent event) {
        String errors = "";
        ArrayList<String> Diag = new ArrayList<>();
        ArrayList<String> Sugg = new ArrayList<>();
        Connection conn = null;
        try {

            conn = getConnection();

            PreparedStatement stmt = conn.prepareStatement("SELECT Problems.ID as ID, Diagnosis, Suggestion, CONCAT('%', GROUP_CONCAT(Error, '%')) as Errors FROM Problems, ProblemErrors WHERE Problems.ID = ProblemErrors.ProblemID GROUP BY ID, Diagnosis, Suggestion HAVING ? LIKE Errors");
            stmt.setString(1, pasteContent);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Errors: " + rs.getString("Errors"));
                System.out.println("Diagnosis: " + rs.getString("Diagnosis"));
                System.out.println("Suggestion: " + rs.getString("Suggestion"));
                errors += rs.getString("Errors") + " | ";
                Diag.add(rs.getString("Diagnosis"));
                Sugg.add(rs.getString("Suggestion"));
            }
            rs.close();
            stmt.close();
            if (!errors.isEmpty() || !Diag.isEmpty() || !Sugg.isEmpty()) {
                event.getChannel().send().message(Colors.BOLD + "Errors: " + Colors.NORMAL + errors);
                for (int i = 0; i < Diag.size(); i++) {
                    //event.getChannel().send().message(Colors.BOLD + "Diagnosis: " + Colors.NORMAL + Diag.get(i));
                    event.getChannel().send().message(Colors.BOLD + "Suggestion: " + Colors.NORMAL + Sugg.get(i));

                }
            } else {
                event.getChannel().send().message("No match found in database!");
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
}
