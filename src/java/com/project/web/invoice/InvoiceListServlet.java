package com.project.web.invoice;

import com.project.dao.InvoiceDAO;
import com.project.model.Invoice;
import java.io.IOException;
// SQLException removed; use generic exception handling after JPA migration
import java.util.Collections;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "InvoiceListServlet", urlPatterns = {"/invoices"})
public class InvoiceListServlet extends HttpServlet {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Invoice> invoices;
        try {
            invoices = invoiceDAO.findAll();
        } catch (Exception ex) {
            invoices = Collections.emptyList();
            request.setAttribute("errorMessage", "Khong tai duoc danh sach hoa don: " + ex.getMessage());
        }

        request.setAttribute("invoices", invoices);
        request.getRequestDispatcher("/invoices/list.jsp").forward(request, response);
    }
}
