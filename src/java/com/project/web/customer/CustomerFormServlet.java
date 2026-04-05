package com.project.web.customer;

import com.project.dao.CustomerDAO;
import com.project.model.Customer;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "CustomerFormServlet", urlPatterns = {"/customers/new", "/customers/edit"})
public class CustomerFormServlet extends HttpServlet {

    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        boolean isEdit = "/customers/edit".equals(request.getServletPath());
        if (isEdit) {
            Integer maKH = parseInteger(request.getParameter("id"));
            if (maKH == null) {
                response.sendRedirect(request.getContextPath() + "/customers");
                return;
            }

            try {
                Customer customer = customerDAO.findById(maKH);
                if (customer == null) {
                    response.sendRedirect(request.getContextPath() + "/customers");
                    return;
                }
                request.setAttribute("customer", customer);
            } catch (SQLException ex) {
                request.setAttribute("errorMessage", "Khong tai duoc du lieu khach hang: " + ex.getMessage());
            }
        } else {
            request.setAttribute("customer", new Customer());
        }

        request.setAttribute("isEdit", isEdit);
        request.getRequestDispatcher("/customers/form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        boolean isEdit = "/customers/edit".equals(request.getServletPath());

        Customer customer = new Customer();
        customer.setTenKH(request.getParameter("tenKH"));
        customer.setDienThoai(request.getParameter("dienThoai"));
        customer.setEmail(request.getParameter("email"));
        customer.setDiaChi(request.getParameter("diaChi"));

        if (isEdit) {
            Integer maKH = parseInteger(request.getParameter("id"));
            if (maKH == null) {
                response.sendRedirect(request.getContextPath() + "/customers");
                return;
            }
            customer.setMaKH(maKH);
        }

        if (customer.getTenKH() == null || customer.getTenKH().trim().isEmpty()) {
            request.setAttribute("errorMessage", "Ten khach hang khong duoc de trong.");
            request.setAttribute("customer", customer);
            request.setAttribute("isEdit", isEdit);
            request.getRequestDispatcher("/customers/form.jsp").forward(request, response);
            return;
        }

        try {
            if (isEdit) {
                customerDAO.update(customer);
                response.sendRedirect(request.getContextPath() + "/customers?msg=updated");
            } else {
                customerDAO.insert(customer);
                response.sendRedirect(request.getContextPath() + "/customers?msg=created");
            }
        } catch (SQLException ex) {
            request.setAttribute("errorMessage", "Luu khach hang that bai: " + ex.getMessage());
            request.setAttribute("customer", customer);
            request.setAttribute("isEdit", isEdit);
            request.getRequestDispatcher("/customers/form.jsp").forward(request, response);
        }
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
