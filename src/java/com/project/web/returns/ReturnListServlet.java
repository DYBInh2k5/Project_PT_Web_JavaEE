package com.project.web.returns;

import com.project.dao.ReturnDAO;
import com.project.model.ReturnTransaction;
import java.io.IOException;
// SQLException removed; use generic exception handling after JPA migration
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ReturnListServlet", urlPatterns = {"/returns"})
public class ReturnListServlet extends HttpServlet {

    private final ReturnDAO returnDAO = new ReturnDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<ReturnTransaction> returns = returnDAO.findAll();
            request.setAttribute("returns", returns);
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Khong tai duoc danh sach doi tra: " + ex.getMessage());
        }

        request.getRequestDispatcher("/returns/list.jsp").forward(request, response);
    }
}
