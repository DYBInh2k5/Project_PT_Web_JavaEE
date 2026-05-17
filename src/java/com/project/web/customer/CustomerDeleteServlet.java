package com.project.web.customer;

import com.project.dao.CustomerDAO;
import java.io.IOException;
// SQLException removed; use generic exception handling after JPA migration
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "CustomerDeleteServlet", urlPatterns = {"/customers/delete"})
public class CustomerDeleteServlet extends HttpServlet {

    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer maKH = parseInteger(request.getParameter("id"));
        if (maKH == null) {
            response.sendRedirect(request.getContextPath() + "/customers?msg=invalid");
            return;
        }

        try {
            customerDAO.delete(maKH);
            response.sendRedirect(request.getContextPath() + "/customers?msg=deleted");
        } catch (Exception ex) {
            response.sendRedirect(request.getContextPath() + "/customers?msg=delete_failed");
        }
    }

    private Integer parseInteger(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.valueOf(raw.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
