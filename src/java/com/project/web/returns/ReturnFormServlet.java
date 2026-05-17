package com.project.web.returns;

import com.project.dao.InvoiceDAO;
import com.project.dao.ReturnDAO;
import com.project.dao.ReturnDAO.NewReturnItem;
import com.project.model.Invoice;
import com.project.model.InvoiceItem;
import java.io.IOException;
// SQLException removed; use generic exception handling after JPA migration
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ReturnFormServlet", urlPatterns = {"/returns/new"})
public class ReturnFormServlet extends HttpServlet {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final ReturnDAO returnDAO = new ReturnDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        populateForm(request);
        request.getRequestDispatcher("/returns/form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        try {
            int maHD = parseRequiredInt(request.getParameter("maHD"), "Vui long chon hoa don can doi tra.");
            String lyDo = request.getParameter("lyDo");
            String ghiChu = request.getParameter("ghiChu");
            int kieuXuLy = parseOptionalInt(request.getParameter("kieuXuLy"), 1);

            String[] maSachValues = request.getParameterValues("maSach");
            String[] soLuongValues = request.getParameterValues("soLuong");
            List<NewReturnItem> items = new ArrayList<NewReturnItem>();
            if (maSachValues != null && soLuongValues != null) {
                int length = Math.min(maSachValues.length, soLuongValues.length);
                for (int i = 0; i < length; i++) {
                    if (isBlank(maSachValues[i]) || isBlank(soLuongValues[i])) {
                        continue;
                    }
                    items.add(new NewReturnItem(Integer.parseInt(maSachValues[i]), Integer.parseInt(soLuongValues[i])));
                }
            }

            int maDT = returnDAO.createReturn(maHD, lyDo, ghiChu, kieuXuLy, items);
            response.sendRedirect(request.getContextPath() + "/returns/detail?id=" + maDT + "&msg=created");
        } catch (Exception ex) {
            request.setAttribute("errorMessage", ex.getMessage());
            populateForm(request);
            request.getRequestDispatcher("/returns/form.jsp").forward(request, response);
        }
    }

    private void populateForm(HttpServletRequest request) {
        try {
            List<Invoice> invoices = invoiceDAO.findAll();
            request.setAttribute("invoices", invoices);

            Integer selectedMaHD = null;
            String maHDParam = request.getParameter("maHD");
            if (!isBlank(maHDParam)) {
                selectedMaHD = Integer.valueOf(maHDParam);
            } else if (invoices != null && !invoices.isEmpty()) {
                selectedMaHD = invoices.get(0).getMaHD();
            }

            if (selectedMaHD != null) {
                List<InvoiceItem> invoiceItems = returnDAO.findInvoiceItems(selectedMaHD.intValue());
                request.setAttribute("selectedMaHD", selectedMaHD);
                request.setAttribute("invoiceItems", invoiceItems);
            }
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Khong tai duoc du lieu hoa don: " + ex.getMessage());
        }
    }

    private int parseRequiredInt(String value, String message) {
        if (isBlank(value)) {
            throw new IllegalArgumentException(message);
        }
        return Integer.parseInt(value);
    }

    private int parseOptionalInt(String value, int defaultValue) {
        if (isBlank(value)) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
