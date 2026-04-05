package com.project.web.purchase;

import com.project.dao.PurchaseDAO;
import com.project.model.PurchaseReceipt;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "PurchaseListServlet", urlPatterns = {"/purchases"})
public class PurchaseListServlet extends HttpServlet {

    private final PurchaseDAO purchaseDAO = new PurchaseDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<PurchaseReceipt> purchases = purchaseDAO.findAll();
            request.setAttribute("purchases", purchases);
        } catch (SQLException ex) {
            request.setAttribute("errorMessage", "Khong tai duoc danh sach phieu nhap: " + ex.getMessage());
        }

        request.getRequestDispatcher("/purchases/list.jsp").forward(request, response);
    }
}