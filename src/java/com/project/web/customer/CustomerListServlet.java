package com.project.web.customer;

import com.project.dao.CustomerDAO;
import com.project.model.Customer;
import java.io.IOException;
// SQLException removed; use generic exception handling after JPA migration
import java.util.Collections;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "CustomerListServlet", urlPatterns = {"/customers"})
public class CustomerListServlet extends HttpServlet {

    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String q = request.getParameter("q");
        List<Customer> customers;

        try {
            customers = customerDAO.findAll(q);
        } catch (Exception ex) {
            customers = Collections.emptyList();
            request.setAttribute("errorMessage", "Khong tai duoc danh sach khach hang: " + ex.getMessage());
        }

        request.setAttribute("customers", customers);
        request.setAttribute("q", q == null ? "" : q);
        request.getRequestDispatcher("/customers/list.jsp").forward(request, response);
    }
}
