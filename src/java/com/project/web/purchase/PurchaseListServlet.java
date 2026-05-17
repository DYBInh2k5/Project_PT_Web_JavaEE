package com.project.web.purchase;

import com.project.dao.PurchaseDAO;
import com.project.model.PurchaseReceipt;
import java.io.IOException;
// SQLException removed; use generic exception handling after JPA migration
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "PurchaseListServlet", urlPatterns = {"/purchases"})
public class PurchaseListServlet extends HttpServlet {

    private final PurchaseDAO purchaseDAO = new PurchaseDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<PurchaseReceipt> purchases = purchaseDAO.findAll();
            request.setAttribute("purchases", purchases);
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Khong tai duoc danh sach phieu nhap: " + ex.getMessage());
        }

        request.getRequestDispatcher("/purchases/list.jsp").forward(request, response);
    }
}