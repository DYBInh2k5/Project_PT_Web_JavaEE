package com.project.web.purchase;

import com.project.dao.PurchaseDAO;
import com.project.model.PurchaseReceipt;
import com.project.model.PurchaseReceiptItem;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "PurchaseDetailServlet", urlPatterns = {"/purchases/detail"})
public class PurchaseDetailServlet extends HttpServlet {

    private final PurchaseDAO purchaseDAO = new PurchaseDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Khong tim thay phieu nhap.");
            } else {
                int id = Integer.parseInt(idParam);
                PurchaseReceipt purchase = purchaseDAO.findById(id);
                List<PurchaseReceiptItem> items = purchaseDAO.findItemsByPurchaseId(id);
                request.setAttribute("purchase", purchase);
                request.setAttribute("items", items);
            }
        } catch (SQLException | NumberFormatException ex) {
            request.setAttribute("errorMessage", "Khong tai duoc chi tiet phieu nhap: " + ex.getMessage());
        }

        request.getRequestDispatcher("/purchases/detail.jsp").forward(request, response);
    }
}