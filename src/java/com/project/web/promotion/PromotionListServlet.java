package com.project.web.promotion;

import com.project.dao.PromotionDAO;
import com.project.model.Promotion;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        } catch (SQLException ex) {
            request.setAttribute("errorMessage", "Khong tai duoc danh sach khuyen mai: " + ex.getMessage());
        }

        request.getRequestDispatcher("/promotions/list.jsp").forward(request, response);
    }
}