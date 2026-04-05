package com.project.web.purchase;

import com.project.dao.BookDAO;
import com.project.dao.PurchaseDAO;
import com.project.dao.PurchaseDAO.NewPurchaseItem;
import com.project.model.AuthUser;
import com.project.model.Book;
import com.project.web.auth.AuthSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "PurchaseFormServlet", urlPatterns = {"/purchases/new"})
public class PurchaseFormServlet extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();
    private final PurchaseDAO purchaseDAO = new PurchaseDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        populateForm(request);
        request.getRequestDispatcher("/purchases/form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        try {
            Integer maNV = extractEmployeeId(request);
            String[] maSachValues = request.getParameterValues("maSach");
            String[] soLuongValues = request.getParameterValues("soLuong");
            String[] donGiaValues = request.getParameterValues("donGia");
            List<NewPurchaseItem> items = new ArrayList<NewPurchaseItem>();

            if (maSachValues != null && soLuongValues != null && donGiaValues != null) {
                int length = Math.min(maSachValues.length, Math.min(soLuongValues.length, donGiaValues.length));
                for (int i = 0; i < length; i++) {
                    if (isBlank(maSachValues[i]) || isBlank(soLuongValues[i]) || isBlank(donGiaValues[i])) {
                        continue;
                    }
                    items.add(new NewPurchaseItem(
                            Integer.parseInt(maSachValues[i]),
                            Integer.parseInt(soLuongValues[i]),
                            new BigDecimal(donGiaValues[i])));
                }
            }

            int maPN = purchaseDAO.createPurchase(maNV, items);
            response.sendRedirect(request.getContextPath() + "/purchases/detail?id=" + maPN + "&msg=created");
        } catch (Exception ex) {
            request.setAttribute("errorMessage", ex.getMessage());
            populateForm(request);
            request.getRequestDispatcher("/purchases/form.jsp").forward(request, response);
        }
    }

    private void populateForm(HttpServletRequest request) {
        try {
            List<Book> books = bookDAO.findAll(null);
            request.setAttribute("books", books);
        } catch (SQLException ex) {
            request.setAttribute("errorMessage", "Khong tai duoc danh sach sach: " + ex.getMessage());
        }
    }

    private Integer extractEmployeeId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object authObj = session.getAttribute(AuthSession.AUTH_USER);
        if (!(authObj instanceof AuthUser)) {
            return null;
        }

        AuthUser user = (AuthUser) authObj;
        String maNV = user.getMaNV();
        if (isBlank(maNV)) {
            return null;
        }

        try {
            return Integer.valueOf(maNV);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}