package com.project.web.shop;

import com.project.dao.InvoiceDAO;
import com.project.model.Invoice;
import com.project.model.InvoiceItem;
import com.project.model.dto.ShopOrderSummary;
import java.io.IOException;
// SQLException removed; use generic exception handling after JPA migration
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ShopMyOrdersServlet", urlPatterns = {"/shop/my-orders"})
public class ShopMyOrdersServlet extends HttpServlet {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Map<Integer, Integer> cart = ShopCartSupport.getCart(request.getSession(true));
        request.setAttribute("cartCount", ShopCartSupport.getCartCount(cart));

        List<Integer> historyIds = ShopCartSupport.getOrderHistory(request.getSession(true));
        if (historyIds.isEmpty()) {
            request.setAttribute("orders", Collections.emptyList());
            request.getRequestDispatcher("/shop/my-orders.jsp").forward(request, response);
            return;
        }

        try {
            List<Invoice> invoices = invoiceDAO.findByIds(historyIds);
            List<ShopOrderSummary> orders = new ArrayList<ShopOrderSummary>();

            for (Invoice invoice : invoices) {
                if (invoice == null || invoice.getMaHD() == null) {
                    continue;
                }

                List<InvoiceItem> items = invoiceDAO.findItemsByInvoiceId(invoice.getMaHD());
                int itemCount = 0;
                for (InvoiceItem item : items) {
                    if (item.getSoLuong() != null && item.getSoLuong() > 0) {
                        itemCount += item.getSoLuong();
                    }
                }

                ShopOrderSummary summary = new ShopOrderSummary();
                summary.setInvoice(invoice);
                summary.setItemCount(itemCount);
                summary.setOrderCode(ShopOrderCodeUtil.encode(invoice.getMaHD()));
                orders.add(summary);
            }

            request.setAttribute("orders", orders);
        } catch (Exception ex) {
            request.setAttribute("orders", Collections.emptyList());
            request.setAttribute("errorMessage", "Khong tai duoc lich su don hang: " + ex.getMessage());
        }

        request.getRequestDispatcher("/shop/my-orders.jsp").forward(request, response);
    }
}