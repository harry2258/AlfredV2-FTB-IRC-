package com.harry2258.Alfred.Database;

import java.sql.*;
import java.util.List;

/**
 * Created by Hardik on 1/27/14.
 */
public class DB {

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:e:/main.db");
    }

    public void createTables() throws SQLException {
        Connection conn = getConnection();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS Problems { ID integer PRIMARY KEY AUTOINCREMENT, Diagnosis VARCHAR(255), Suggestion VARCHAR(255) }").execute();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS ProblemErrors { ID integer PRIMARY KEY AUTOINCREMENT, ProblemID integer, Error VARCHAR(255) }").execute();
        conn.close();
    }

    public void addProblem(String diagnosis, String suggestion, List<String> errors) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Problems (Diagnosis, Suggestion) VALUES (?, ?)");
        stmt.setString(1, diagnosis);
        stmt.setString(2, suggestion);
        stmt.execute();

        ResultSet rs = stmt.getGeneratedKeys();

        if(rs.next()) {
            for(String error : errors) {
                PreparedStatement stmtError = conn.prepareStatement("INSERT INTO ProblemErrors (ProblemID, Error) VALUES (?,?)");
                stmtError.setInt(1, rs.getInt(1));
                stmtError.setString(2, error);
                stmtError.execute();
                stmtError.close();
            }
        }

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
