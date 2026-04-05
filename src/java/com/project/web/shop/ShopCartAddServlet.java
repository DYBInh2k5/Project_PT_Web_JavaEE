package com.project.web.shop;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ShopCartAddServlet", urlPatterns = {"/shop/cart/add"})
public class ShopCartAddServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer id = parseInteger(request.getParameter("id"));
        Integer qty = parseInteger(request.getParameter("qty"));
        if (qty == null || qty <= 0) {
            qty = 1;
        }

        if (id != null) {
            Map<Integer, Integer> cart = ShopCartSupport.getCart(request.getSession(true));
            Integer oldQty = cart.get(id);
            int newQty = (oldQty == null ? 0 : oldQty) + qty;
            if (newQty > 999) {
                newQty = 999;
            }
            cart.put(id, newQty);
        }

        String redirect = request.getParameter("redirect");
        if (redirect == null || redirect.trim().isEmpty()) {
            redirect = request.getContextPath() + "/shop";
        }
        response.sendRedirect(redirect);
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