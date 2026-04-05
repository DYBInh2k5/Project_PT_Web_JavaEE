package com.project.web;

import com.project.dao.DashboardDAO;
import com.project.model.dto.DashboardStats;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private final DashboardDAO dashboardDAO = new DashboardDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            DashboardStats stats = dashboardDAO.loadStats();
            request.setAttribute("stats", stats);
        } catch (SQLException ex) {
            DashboardStats empty = new DashboardStats();
            empty.setRevenue(BigDecimal.ZERO);
            request.setAttribute("stats", empty);
            request.setAttribute("errorMessage", "Khong tai duoc dashboard: " + ex.getMessage());
        }

        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}
