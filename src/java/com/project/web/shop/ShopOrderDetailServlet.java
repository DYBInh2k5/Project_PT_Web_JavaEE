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

@WebServlet(name = "ShopOrderDetailServlet", urlPatterns = {"/shop/order"})
public class ShopOrderDetailServlet extends HttpServlet {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        jakarta.servlet.http.HttpSession session = request.getSession(true);
        Map<Integer, Integer> cart = ShopCartSupport.getCart(session);
        request.setAttribute("cartCount", ShopCartSupport.getCartCount(cart));

        String code = request.getParameter("code");
        Integer maHD = ShopOrderCodeUtil.decode(code);
        request.setAttribute("orderCode", code);
        if (maHD == null) {
            request.setAttribute("errorMessage", "Ma don khong hop le.");
            request.getRequestDispatcher("/shop/order-detail.jsp").forward(request, response);
            return;
        }

        String phone = trimToNull(request.getParameter("phone"));
        String email = trimToNull(request.getParameter("email"));
        request.setAttribute("phone", phone == null ? "" : phone);
        request.setAttribute("email", email == null ? "" : email);

        boolean canAccess = ShopOrderAccessSupport.isOrderInHistory(session, maHD)
                || ShopOrderAccessSupport.hasCode(session, code);

        try {
            if (!canAccess && (phone != null || email != null)) {
                Invoice verified = invoiceDAO.findByIdForCustomerLookup(maHD, phone, email);
                if (verified != null) {
                    ShopOrderAccessSupport.grantCode(session, code);
                    canAccess = true;
                } else {
                    request.setAttribute("errorMessage", "Thong tin xac thuc khong dung voi don hang.");
                }
            }

            if (!canAccess) {
                request.setAttribute("verificationRequired", true);
                request.getRequestDispatcher("/shop/order-detail.jsp").forward(request, response);
                return;
            }

            Invoice invoice = invoiceDAO.findById(maHD);
            if (invoice == null) {
                request.setAttribute("errorMessage", "Khong tim thay don hang.");
                request.getRequestDispatcher("/shop/order-detail.jsp").forward(request, response);
                return;
            }

            List<InvoiceItem> items = invoiceDAO.findItemsByInvoiceId(maHD);
            request.setAttribute("invoice", invoice);
            request.setAttribute("items", items);
            request.setAttribute("orderCode", ShopOrderCodeUtil.encode(maHD));
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Khong tai duoc don hang: " + ex.getMessage());
            request.setAttribute("items", Collections.emptyList());
        }

        request.getRequestDispatcher("/shop/order-detail.jsp").forward(request, response);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }
}