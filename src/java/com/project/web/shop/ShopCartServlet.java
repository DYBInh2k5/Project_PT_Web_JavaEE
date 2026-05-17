package com.project.web.shop;

import com.project.dao.BookDAO;
import com.project.model.dto.ShopCartItem;
import java.io.IOException;
import java.math.BigDecimal;
// SQLException removed; use generic exception handling after JPA migration
import java.util.Collections;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ShopCartServlet", urlPatterns = {"/shop/cart"})
public class ShopCartServlet extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Map<Integer, Integer> cart = ShopCartSupport.getCart(request.getSession(true));
        List<ShopCartItem> items;
        BigDecimal total = BigDecimal.ZERO;

        try {
            items = ShopCartSupport.buildCartItems(bookDAO, cart);
            for (ShopCartItem item : items) {
                total = total.add(item.getThanhTien());
            }
        } catch (Exception ex) {
            items = Collections.emptyList();
            request.setAttribute("errorMessage", "Khong tai duoc gio hang: " + ex.getMessage());
        }

        request.setAttribute("items", items);
        request.setAttribute("total", total);
        request.setAttribute("cartCount", ShopCartSupport.getCartCount(cart));
        request.getRequestDispatcher("/shop/cart.jsp").forward(request, response);
    }
}