package com.project.web.invoice;

import com.project.dao.InvoiceDAO;
import com.project.model.Invoice;
import com.project.model.InvoiceItem;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "InvoiceDetailServlet", urlPatterns = {"/invoices/detail"})
public class InvoiceDetailServlet extends HttpServlet {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer maHD = parseInteger(request.getParameter("id"));
        if (maHD == null) {
            response.sendRedirect(request.getContextPath() + "/invoices");
            return;
        }

        try {
            Invoice invoice = invoiceDAO.findById(maHD);
            if (invoice == null) {
                response.sendRedirect(request.getContextPath() + "/invoices");
                return;
            }

            List<InvoiceItem> items = invoiceDAO.findItemsByInvoiceId(maHD);
            request.setAttribute("invoice", invoice);
            request.setAttribute("items", items);

        } catch (SQLException ex) {
            request.setAttribute("errorMessage", "Khong tai duoc chi tiet hoa don: " + ex.getMessage());
            request.setAttribute("items", Collections.emptyList());
        }

        request.getRequestDispatcher("/invoices/detail.jsp").forward(request, response);
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
