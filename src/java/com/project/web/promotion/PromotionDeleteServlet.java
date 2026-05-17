package com.project.web.promotion;

import com.project.dao.PromotionDAO;
import java.io.IOException;
// SQLException removed; use generic exception handling after JPA migration
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "PromotionDeleteServlet", urlPatterns = {"/promotions/delete"})
public class PromotionDeleteServlet extends HttpServlet {

    private final PromotionDAO promotionDAO = new PromotionDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer maKM = parseInteger(request.getParameter("id"));
        if (maKM == null) {
            response.sendRedirect(request.getContextPath() + "/promotions?msg=invalid");
            return;
        }

        try {
            promotionDAO.delete(maKM);
            response.sendRedirect(request.getContextPath() + "/promotions?msg=deleted");
        } catch (Exception ex) {
            response.sendRedirect(request.getContextPath() + "/promotions?msg=delete_failed");
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