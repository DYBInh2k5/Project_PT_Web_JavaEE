package com.project.web;

import com.project.db.SqlServerConnection;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "DbHealthServlet", urlPatterns = {"/db-check"})
public class DbHealthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");

        try (PrintWriter out = response.getWriter();
             Connection conn = SqlServerConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DB_NAME() AS dbName")) {

            String dbName = "unknown";
            if (rs.next()) {
                dbName = rs.getString("dbName");
            }

            out.println("Connection OK");
            out.println("Current database: " + dbName);
            out.println("Expected database: QLBanSach");
            out.println("Match: " + String.valueOf("QLBanSach".equalsIgnoreCase(dbName)));

        } catch (SQLException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.println("Connection FAILED");
                out.println("Reason: " + ex.getMessage());
            }
        }
    }
}
