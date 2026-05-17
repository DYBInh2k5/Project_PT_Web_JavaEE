package com.project.web.shop;

import com.project.dao.BookDAO;
import com.project.model.Book;
import java.io.IOException;
// SQLException removed; use generic exception handling after JPA migration
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ShopBookDetailServlet", urlPatterns = {"/shop/book"})
public class ShopBookDetailServlet extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer id = parseInteger(request.getParameter("id"));
        if (id == null) {
            response.sendRedirect(request.getContextPath() + "/shop");
            return;
        }

        try {
            Book book = bookDAO.findById(id);
            if (book == null) {
                response.sendRedirect(request.getContextPath() + "/shop");
                return;
            }
            request.setAttribute("book", book);
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Khong tai duoc chi tiet sach: " + ex.getMessage());
        }

        Map<Integer, Integer> cart = ShopCartSupport.getCart(request.getSession(true));
        request.setAttribute("cartCount", ShopCartSupport.getCartCount(cart));
        request.getRequestDispatcher("/shop/detail.jsp").forward(request, response);
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