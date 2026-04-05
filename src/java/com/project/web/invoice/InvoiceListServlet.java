package com.project.web.invoice;

import com.project.dao.InvoiceDAO;
import com.project.model.Invoice;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "InvoiceListServlet", urlPatterns = {"/invoices"})
public class InvoiceListServlet extends HttpServlet {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Invoice> invoices;
        try {
            invoices = invoiceDAO.findAll();
        } catch (SQLException ex) {
            invoices = Collections.emptyList();
            request.setAttribute("errorMessage", "Khong tai duoc danh sach hoa don: " + ex.getMessage());
        }

        request.setAttribute("invoices", invoices);
        request.getRequestDispatcher("/invoices/list.jsp").forward(request, response);
    }
}
