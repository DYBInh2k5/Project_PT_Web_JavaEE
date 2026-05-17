package com.project.web.returns;

import com.project.dao.ReturnDAO;
import com.project.model.ReturnItem;
import com.project.model.ReturnTransaction;
import java.io.IOException;
// SQLException removed; use generic exception handling after JPA migration
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ReturnDetailServlet", urlPatterns = {"/returns/detail"})
public class ReturnDetailServlet extends HttpServlet {

    private final ReturnDAO returnDAO = new ReturnDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Khong tim thay phieu tra hang.");
            } else {
                int id = Integer.parseInt(idParam);
                ReturnTransaction tx = returnDAO.findById(id);
                List<ReturnItem> items = returnDAO.findItemsByReturnId(id);
                request.setAttribute("transaction", tx);
                request.setAttribute("items", items);
            }
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Khong tai duoc chi tiet doi tra: " + ex.getMessage());
        }

        request.getRequestDispatcher("/returns/detail.jsp").forward(request, response);
    }
}
