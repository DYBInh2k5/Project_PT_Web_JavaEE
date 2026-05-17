package com.project.web.shop;

import com.project.dao.InvoiceDAO;
import com.project.model.Invoice;
import com.project.model.InvoiceItem;
import java.io.IOException;
// SQLException removed; use generic exception handling after JPA migration
import java.util.Collections;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ShopOrderLookupServlet", urlPatterns = {"/shop/order-lookup"})
public class ShopOrderLookupServlet extends HttpServlet {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Map<Integer, Integer> cart = ShopCartSupport.getCart(request.getSession(true));
        request.setAttribute("cartCount", ShopCartSupport.getCartCount(cart));

        Integer invoiceId = parseInteger(request.getParameter("invoiceId"));
        String phone = trimToNull(request.getParameter("phone"));
        String email = trimToNull(request.getParameter("email"));

        request.setAttribute("invoiceId", invoiceId == null ? "" : invoiceId);
        request.setAttribute("phone", phone == null ? "" : phone);
        request.setAttribute("email", email == null ? "" : email);

        if (invoiceId != null && (phone != null || email != null)) {
            try {
                Invoice invoice = invoiceDAO.findByIdForCustomerLookup(invoiceId, phone, email);
                if (invoice == null) {
                    request.setAttribute("errorMessage", "Khong tim thay don hang phu hop voi thong tin da nhap.");
                } else {
                    List<InvoiceItem> items = invoiceDAO.findItemsByInvoiceId(invoiceId);
                    request.setAttribute("invoice", invoice);
                    request.setAttribute("items", items);
                    request.setAttribute("orderCode", ShopOrderCodeUtil.encode(invoiceId));
                }
            } catch (Exception ex) {
                request.setAttribute("errorMessage", "Khong tai duoc don hang: " + ex.getMessage());
                request.setAttribute("items", Collections.emptyList());
            }
        }

        request.getRequestDispatcher("/shop/order-lookup.jsp").forward(request, response);
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

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }
}