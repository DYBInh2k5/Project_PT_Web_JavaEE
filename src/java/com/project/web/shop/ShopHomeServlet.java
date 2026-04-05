package com.project.web.shop;

import com.project.dao.BookDAO;
import com.project.model.Book;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ShopHomeServlet", urlPatterns = {"/shop"})
public class ShopHomeServlet extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String q = request.getParameter("q");
        List<Book> books;
        try {
            books = bookDAO.findAll(q);
        } catch (SQLException ex) {
            books = Collections.emptyList();
            request.setAttribute("errorMessage", "Khong tai duoc danh sach sach: " + ex.getMessage());
        }

        Map<Integer, Integer> cart = ShopCartSupport.getCart(request.getSession(true));
        request.setAttribute("cartCount", ShopCartSupport.getCartCount(cart));
        request.setAttribute("books", books);
        request.setAttribute("q", q == null ? "" : q);
        request.getRequestDispatcher("/shop/index.jsp").forward(request, response);
    }
}