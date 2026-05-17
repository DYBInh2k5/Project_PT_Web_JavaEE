package com.project.web.shop;

import java.io.IOException;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ShopCartRemoveServlet", urlPatterns = {"/shop/cart/remove"})
public class ShopCartRemoveServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer id = parseInteger(request.getParameter("id"));
        if (id != null) {
            Map<Integer, Integer> cart = ShopCartSupport.getCart(request.getSession(true));
            cart.remove(id);
        }

        response.sendRedirect(request.getContextPath() + "/shop/cart");
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