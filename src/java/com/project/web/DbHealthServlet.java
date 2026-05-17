package com.project.web;

import com.project.dao.JpaSupport;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "DbHealthServlet", urlPatterns = {"/db-check"})
public class DbHealthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");

        EntityManager em = JpaSupport.createEntityManager();
        try (PrintWriter out = response.getWriter()) {
            Object value = em.createNativeQuery("SELECT DB_NAME() AS dbName").getSingleResult();
            String dbName = value == null ? "unknown" : String.valueOf(value);

            out.println("Connection OK");
            out.println("Current database: " + dbName);
            out.println("Expected database: QLBanSach");
            out.println("Match: " + String.valueOf("QLBanSach".equalsIgnoreCase(dbName)));

        } catch (RuntimeException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.println("Connection FAILED");
                out.println("Reason: " + ex.getMessage());
            }
        } finally {
            em.close();
        }
    }
}
