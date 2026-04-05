package com.project.web.returns;

import com.project.dao.ReturnDAO;
import com.project.model.ReturnTransaction;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ReturnListServlet", urlPatterns = {"/returns"})
public class ReturnListServlet extends HttpServlet {

    private final ReturnDAO returnDAO = new ReturnDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<ReturnTransaction> returns = returnDAO.findAll();
            request.setAttribute("returns", returns);
        } catch (SQLException ex) {
            request.setAttribute("errorMessage", "Khong tai duoc danh sach doi tra: " + ex.getMessage());
        }

        request.getRequestDispatcher("/returns/list.jsp").forward(request, response);
    }
}
