package com.project.web.promotion;

import com.project.dao.PromotionDAO;
import com.project.model.Promotion;
import java.io.IOException;
// SQLException removed; use generic exception handling after JPA migration
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "PromotionListServlet", urlPatterns = {"/promotions"})
public class PromotionListServlet extends HttpServlet {

    private final PromotionDAO promotionDAO = new PromotionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String keyword = request.getParameter("q");
            List<Promotion> promotions = promotionDAO.findAll(keyword);
            request.setAttribute("promotions", promotions);
            request.setAttribute("keyword", keyword);
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Khong tai duoc danh sach khuyen mai: " + ex.getMessage());
        }

        request.getRequestDispatcher("/promotions/list.jsp").forward(request, response);
    }
}