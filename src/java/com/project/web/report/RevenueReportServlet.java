package com.project.web.report;

import com.project.dao.ReportDAO;
import com.project.model.dto.RevenueByDate;
import com.project.model.dto.TopBookReportItem;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "RevenueReportServlet", urlPatterns = {"/reports/revenue"})
public class RevenueReportServlet extends HttpServlet {

    private final ReportDAO reportDAO = new ReportDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        LocalDate today = LocalDate.now();
        LocalDate defaultFrom = today.minusDays(30);

        LocalDate from = parseLocalDate(request.getParameter("from"));
        LocalDate to = parseLocalDate(request.getParameter("to"));

        if (from == null) {
            from = defaultFrom;
        }
        if (to == null) {
            to = today;
        }

        Date fromDate = Date.valueOf(from);
        Date toDate = Date.valueOf(to);

        try {
            List<RevenueByDate> revenueRows = reportDAO.findRevenueByDate(fromDate, toDate);
            List<TopBookReportItem> topBooks = reportDAO.findTopBooks(fromDate, toDate, 10);
            request.setAttribute("revenueRows", revenueRows);
            request.setAttribute("topBooks", topBooks);
        } catch (SQLException ex) {
            request.setAttribute("revenueRows", Collections.emptyList());
            request.setAttribute("topBooks", Collections.emptyList());
            request.setAttribute("errorMessage", "Khong tai duoc bao cao: " + ex.getMessage());
        }

        request.setAttribute("from", from.toString());
        request.setAttribute("to", to.toString());
        request.getRequestDispatcher("/reports/revenue.jsp").forward(request, response);
    }

    private LocalDate parseLocalDate(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(raw.trim());
        } catch (Exception ex) {
            return null;
        }
    }
}
