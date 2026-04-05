package com.project.web.invoice;

import com.project.dao.BookDAO;
import com.project.dao.CustomerDAO;
import com.project.dao.InvoiceDAO;
import com.project.dao.InvoiceDAO.NewInvoiceItem;
import com.project.dao.PromotionDAO;
import com.project.model.Book;
import com.project.model.Customer;
import com.project.model.Promotion;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "InvoiceCreateServlet", urlPatterns = {"/invoices/new"})
public class InvoiceCreateServlet extends HttpServlet {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final BookDAO bookDAO = new BookDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final PromotionDAO promotionDAO = new PromotionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        loadFormData(request);
        request.getRequestDispatcher("/invoices/new.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        try {
            Integer maKH = parseInteger(request.getParameter("maKH"));
            String maNV = request.getParameter("maNV");
            BigDecimal giamGia = parseBigDecimal(request.getParameter("giamGia"));
            BigDecimal thueVAT = parseBigDecimal(request.getParameter("thueVAT"));
            String couponCode = request.getParameter("couponCode");

            String[] maSachArr = request.getParameterValues("maSach");
            String[] soLuongArr = request.getParameterValues("soLuong");

            List<NewInvoiceItem> items = extractItems(maSachArr, soLuongArr);
            if (items.isEmpty()) {
                throw new SQLException("Vui long them it nhat 1 dong sach hop le (so luong > 0).");
            }

            BigDecimal couponDiscount = calculateCouponDiscount(couponCode, items);
            BigDecimal finalDiscount = giamGia.add(couponDiscount);

            int maHD = invoiceDAO.createInvoice(maKH, maNV, finalDiscount, thueVAT, items);
            response.sendRedirect(request.getContextPath() + "/invoices/detail?id=" + maHD + "&msg=created");
        } catch (SQLException ex) {
            request.setAttribute("errorMessage", "Tao hoa don that bai: " + ex.getMessage());
            loadFormData(request);
            request.getRequestDispatcher("/invoices/new.jsp").forward(request, response);
        }
    }

    private List<NewInvoiceItem> extractItems(String[] maSachArr, String[] soLuongArr) {
        if (maSachArr == null || soLuongArr == null) {
            return Collections.emptyList();
        }

        int n = Math.min(maSachArr.length, soLuongArr.length);
        List<NewInvoiceItem> items = new ArrayList<NewInvoiceItem>();

        for (int i = 0; i < n; i++) {
            Integer maSach = parseInteger(maSachArr[i]);
            Integer soLuong = parseInteger(soLuongArr[i]);

            if (maSach != null && soLuong != null && soLuong > 0) {
                items.add(new NewInvoiceItem(maSach, soLuong));
            }
        }

        return items;
    }

    private void loadFormData(HttpServletRequest request) {
        try {
            List<Book> books = bookDAO.findAll(null);
            request.setAttribute("books", books);

            Map<Integer, BigDecimal> bookPriceMap = new HashMap<Integer, BigDecimal>();
            for (Book book : books) {
                if (book.getMaSach() != null) {
                    bookPriceMap.put(book.getMaSach(), book.getDonGia() == null ? BigDecimal.ZERO : book.getDonGia());
                }
            }
            request.setAttribute("bookPriceMap", bookPriceMap);
        } catch (SQLException ex) {
            request.setAttribute("books", Collections.emptyList());
            request.setAttribute("errorMessage", "Khong tai duoc du lieu sach: " + ex.getMessage());
        }

        try {
            List<Customer> customers = customerDAO.findAll(null);
            request.setAttribute("customers", customers);
        } catch (SQLException ex) {
            request.setAttribute("customers", Collections.emptyList());
            if (request.getAttribute("errorMessage") == null) {
                request.setAttribute("errorMessage", "Khong tai duoc du lieu khach hang: " + ex.getMessage());
            }
        }

        try {
            List<Promotion> promotions = promotionDAO.findActivePromotions();
            request.setAttribute("promotions", promotions);
        } catch (SQLException ex) {
            request.setAttribute("promotions", Collections.emptyList());
            if (request.getAttribute("errorMessage") == null) {
                request.setAttribute("errorMessage", "Khong tai duoc du lieu khuyen mai: " + ex.getMessage());
            }
        }
    }

    private BigDecimal calculateCouponDiscount(String couponCode, List<NewInvoiceItem> items) throws SQLException {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        Map<Integer, BigDecimal> priceMap = loadBookPriceMap();
        BigDecimal subtotal = calculateSubtotal(items, priceMap);

        Promotion promotion = promotionDAO.findByCode(couponCode.trim());
        if (promotion == null) {
            throw new SQLException("Khong tim thay coupon hop le hoac coupon da het han.");
        }

        BigDecimal discount;
        String promotionType = promotion.getHinhThuc() == null ? "" : promotion.getHinhThuc().trim();

        if ("TANG1".equalsIgnoreCase(promotionType)) {
            discount = calculateTang1Discount(promotion, items, priceMap);
        } else {
            discount = promotionDAO.calculateDiscount(promotion, subtotal);
        }

        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        if (discount.compareTo(subtotal) > 0) {
            return subtotal;
        }
        return discount;
    }

    private Map<Integer, BigDecimal> loadBookPriceMap() throws SQLException {
        List<Book> books = bookDAO.findAll(null);
        Map<Integer, BigDecimal> priceMap = new HashMap<Integer, BigDecimal>();
        for (Book book : books) {
            if (book.getMaSach() != null) {
                priceMap.put(book.getMaSach(), book.getDonGia() == null ? BigDecimal.ZERO : book.getDonGia());
            }
        }
        return priceMap;
    }

    private BigDecimal calculateSubtotal(List<NewInvoiceItem> items, Map<Integer, BigDecimal> priceMap) throws SQLException {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (NewInvoiceItem item : items) {
            BigDecimal unitPrice = priceMap.get(item.getMaSach());
            if (unitPrice == null) {
                throw new SQLException("Khong tim thay gia sach ma " + item.getMaSach());
            }
            subtotal = subtotal.add(unitPrice.multiply(BigDecimal.valueOf(item.getSoLuong())));
        }
        return subtotal;
    }

    private BigDecimal calculateTang1Discount(Promotion promotion, List<NewInvoiceItem> items, Map<Integer, BigDecimal> priceMap)
            throws SQLException {

        int buyQty = getTang1BuyQty(promotion);
        int totalQty = 0;
        List<BigDecimal> unitPrices = new ArrayList<BigDecimal>();

        for (NewInvoiceItem item : items) {
            BigDecimal unitPrice = priceMap.get(item.getMaSach());
            if (unitPrice == null) {
                throw new SQLException("Khong tim thay gia sach ma " + item.getMaSach());
            }

            totalQty += item.getSoLuong();
            for (int i = 0; i < item.getSoLuong(); i++) {
                unitPrices.add(unitPrice);
            }
        }

        int freeCount = totalQty / (buyQty + 1);
        if (freeCount <= 0) {
            return BigDecimal.ZERO;
        }

        Collections.sort(unitPrices);
        BigDecimal discount = BigDecimal.ZERO;
        for (int i = 0; i < freeCount && i < unitPrices.size(); i++) {
            discount = discount.add(unitPrices.get(i));
        }
        return discount;
    }

    private int getTang1BuyQty(Promotion promotion) {
        if (promotion.getGiaTri() == null) {
            return 2;
        }

        try {
            int value = promotion.getGiaTri().intValue();
            return value <= 0 ? 2 : value;
        } catch (Exception ex) {
            return 2;
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

    private BigDecimal parseBigDecimal(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        try {
            return new BigDecimal(raw.trim());
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }
}
