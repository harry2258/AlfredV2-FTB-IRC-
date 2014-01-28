package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.Database.*;
import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Hardik on 1/27/14.
 */
public class Error extends Command {
    private Config config;
    private PermissionManager manager;
    public static HashMap<String, String> Diagnosis = new HashMap<>();
    public static HashMap<String, String> Errors = new HashMap<>();
    public static HashMap<String, String> Suggestion = new HashMap<>();

    public Error() {

        super("Error", "Errors Database!");
    }

    @Override
    public boolean execute(MessageEvent event) {
        String[] args = event.getMessage().split(" ");
        try {
            Class.forName("org.sqlite.JDBC");

            if (args.length==2 && args[1].equalsIgnoreCase("create")){
            createTables();
            event.getChannel().send().message("Created Table!");
                return true;
            }
            if (args.length==2 && args[1].equalsIgnoreCase("review")){
                event.getUser().send().notice(Colors.BOLD + "Errors: " + Colors.NORMAL + Errors.get(event.getUser().getNick()));
                event.getUser().send().notice(Colors.BOLD + "Diagnosis: " + Colors.NORMAL + Diagnosis.get(event.getUser().getNick()));
                event.getUser().send().notice(Colors.BOLD + "Suggestion: " + Colors.NORMAL + Suggestion.get(event.getUser().getNick()));
                return true;
            }
            if (args.length==2 && args[1].equalsIgnoreCase("submit")){
                ArrayList<String> test1 = new ArrayList<>();
                String[] test = Errors.get(event.getUser().getNick()).split(" ", 2)[1].split(",");
                for (int i = 0; i < test.length; i++) {
                    test1.add(test[i]);
                }
                addProblem(Diagnosis.get(event.getUser().getNick()), Suggestion.get(event.getUser().getNick()), test1);
                Diagnosis.remove(event.getUser().getNick());
                Suggestion.remove(event.getUser().getNick());
                Errors.remove(event.getUser().getNick());

                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            event.getChannel().send().message(e.toString());
        }



        StringBuilder br= new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            br.append(args[i]).append(" ");
        }
        Errors.put(event.getUser().getNick(), br.toString());
        event.getUser().send().notice("Added " + br.toString() + "to Error List!");
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

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + System.getProperty("user.dir") + "\\main.db");
    }

    public void createTables() throws SQLException {
        Connection conn = getConnection();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS Problems ( ID integer PRIMARY KEY AUTOINCREMENT, Diagnosis VARCHAR(255), Suggestion VARCHAR(255) ) ").execute();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS ProblemErrors ( ID integer PRIMARY KEY AUTOINCREMENT, ProblemID integer, Error VARCHAR(255) )").execute();
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

        if(rs.next()) {
            for(String error : errors) {
                PreparedStatement stmtError = conn.prepareStatement("INSERT INTO ProblemErrors (ProblemID, Error) VALUES (?,?)");
                stmtError.setInt(1, rs.getInt(1));
                stmtError.setString(2, error);
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
        stmt.setInt(1,  ID);
        stmt.execute();
        stmt.close();

        for(String error : errors) {
            PreparedStatement stmtError = conn.prepareStatement("INSERT INTO ProblemErrors (ProblemID, Error) VALUES (?,?)");
            stmtError.setInt(1, ID);
            stmtError.setString(2, error);
            stmtError.execute();
            stmtError.close();
        }

        conn.close();
    }
}
