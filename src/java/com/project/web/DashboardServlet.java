package com.project.web;

import com.project.dao.DashboardDAO;
import com.project.model.dto.DashboardStats;
import java.io.IOException;
import java.math.BigDecimal;
// SQLException no longer used after JPA migration
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private final DashboardDAO dashboardDAO = new DashboardDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            DashboardStats stats = dashboardDAO.loadStats();
            request.setAttribute("stats", stats);
        } catch (Exception ex) {
            DashboardStats empty = new DashboardStats();
            empty.setRevenue(BigDecimal.ZERO);
            request.setAttribute("stats", empty);
            request.setAttribute("errorMessage", "Khong tai duoc dashboard: " + ex.getMessage());
        }

        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}
