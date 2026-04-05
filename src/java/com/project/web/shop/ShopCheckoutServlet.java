package com.project.web.shop;

import com.project.dao.BookDAO;
import com.project.dao.CustomerDAO;
import com.project.dao.InvoiceDAO;
import com.project.dao.InvoiceDAO.NewInvoiceItem;
import com.project.dao.PromotionDAO;
import com.project.model.Book;
import com.project.model.Customer;
import com.project.model.Promotion;
import com.project.model.dto.ShopCartItem;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ShopCheckoutServlet", urlPatterns = {"/shop/checkout"})
public class ShopCheckoutServlet extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final PromotionDAO promotionDAO = new PromotionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        prepareCheckoutData(request);
        request.getRequestDispatcher("/shop/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        Map<Integer, Integer> cart = ShopCartSupport.getCart(request.getSession(true));

        try {
            List<ShopCartItem> cartItems = ShopCartSupport.buildCartItems(bookDAO, cart);
            if (cartItems.isEmpty()) {
                throw new SQLException("Gio hang dang trong. Vui long them sach truoc khi thanh toan.");
            }

            List<NewInvoiceItem> newInvoiceItems = buildInvoiceItems(cartItems);

            String couponCode = request.getParameter("couponCode");
            BigDecimal discount = calculateCouponDiscount(couponCode, cartItems);

            Integer maKH = buildCustomerIfProvided(request);
            int maHD = invoiceDAO.createInvoice(maKH, null, discount, BigDecimal.ZERO, newInvoiceItems);

            cart.clear();
            ShopCartSupport.addOrderToHistory(request.getSession(true), maHD);
            String orderCode = ShopOrderCodeUtil.encode(maHD);
            response.sendRedirect(request.getContextPath() + "/shop/checkout?success=1&invoiceId=" + maHD + "&orderCode=" + orderCode);
        } catch (SQLException ex) {
            request.setAttribute("errorMessage", "Thanh toan that bai: " + ex.getMessage());
            request.setAttribute("fullName", valueOrEmpty(request.getParameter("fullName")));
            request.setAttribute("phone", valueOrEmpty(request.getParameter("phone")));
            request.setAttribute("email", valueOrEmpty(request.getParameter("email")));
            request.setAttribute("address", valueOrEmpty(request.getParameter("address")));
            request.setAttribute("couponCode", valueOrEmpty(request.getParameter("couponCode")));
            prepareCheckoutData(request);
            request.getRequestDispatcher("/shop/checkout.jsp").forward(request, response);
        }
    }

    private void prepareCheckoutData(HttpServletRequest request) {
        Map<Integer, Integer> cart = ShopCartSupport.getCart(request.getSession(true));
        request.setAttribute("cartCount", ShopCartSupport.getCartCount(cart));

        try {
            List<ShopCartItem> items = ShopCartSupport.buildCartItems(bookDAO, cart);
            request.setAttribute("items", items);
            request.setAttribute("subtotal", calculateSubtotal(items));
            request.setAttribute("promotions", promotionDAO.findActivePromotions());
        } catch (SQLException ex) {
            request.setAttribute("items", Collections.emptyList());
            request.setAttribute("subtotal", BigDecimal.ZERO);
            request.setAttribute("promotions", Collections.emptyList());
            if (request.getAttribute("errorMessage") == null) {
                request.setAttribute("errorMessage", "Khong tai duoc du lieu checkout: " + ex.getMessage());
            }
        }
    }

    private List<NewInvoiceItem> buildInvoiceItems(List<ShopCartItem> cartItems) throws SQLException {
        List<NewInvoiceItem> items = new ArrayList<NewInvoiceItem>();
        for (ShopCartItem item : cartItems) {
            if (item.getSoLuong() == null || item.getSoLuong() <= 0) {
                continue;
            }

            int tonKho = item.getTonKho() == null ? 0 : item.getTonKho();
            if (item.getSoLuong() > tonKho) {
                throw new SQLException("Sach \"" + item.getTenSach() + "\" khong du ton kho.");
            }

            items.add(new NewInvoiceItem(item.getMaSach(), item.getSoLuong()));
        }

        if (items.isEmpty()) {
            throw new SQLException("Khong co dong sach hop le trong gio hang.");
        }
        return items;
    }

    private Integer buildCustomerIfProvided(HttpServletRequest request) throws SQLException {
        String fullName = trimToNull(request.getParameter("fullName"));
        String phone = trimToNull(request.getParameter("phone"));
        String email = trimToNull(request.getParameter("email"));
        String address = trimToNull(request.getParameter("address"));

        if (fullName == null) {
            return null;
        }

        Customer existing = customerDAO.findByPhoneOrEmail(phone, email);
        if (existing != null) {
            existing.setTenKH(fullName != null ? fullName : existing.getTenKH());
            if (phone != null) {
                existing.setDienThoai(phone);
            }
            if (email != null) {
                existing.setEmail(email);
            }
            if (address != null) {
                existing.setDiaChi(address);
            }
            customerDAO.update(existing);
            return existing.getMaKH();
        }

        Customer customer = new Customer();
        customer.setTenKH(fullName);
        customer.setDienThoai(phone);
        customer.setEmail(email);
        customer.setDiaChi(address);
        return customerDAO.insertAndGetId(customer);
    }

    private BigDecimal calculateCouponDiscount(String couponCode, List<ShopCartItem> items) throws SQLException {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        Promotion promotion = promotionDAO.findByCode(couponCode.trim());
        if (promotion == null) {
            throw new SQLException("Khong tim thay coupon hop le hoac coupon da het han.");
        }

        BigDecimal subtotal = calculateSubtotal(items);
        BigDecimal discount;
        String promotionType = promotion.getHinhThuc() == null ? "" : promotion.getHinhThuc().trim();

        if ("TANG1".equalsIgnoreCase(promotionType)) {
            discount = calculateTang1Discount(promotion, items);
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

    private BigDecimal calculateSubtotal(List<ShopCartItem> items) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ShopCartItem item : items) {
            subtotal = subtotal.add(item.getThanhTien());
        }
        return subtotal;
    }

    private BigDecimal calculateTang1Discount(Promotion promotion, List<ShopCartItem> items) {
        int buyQty = getTang1BuyQty(promotion);
        int totalQty = 0;
        List<BigDecimal> unitPrices = new ArrayList<BigDecimal>();

        for (ShopCartItem item : items) {
            BigDecimal unitPrice = item.getDonGia() == null ? BigDecimal.ZERO : item.getDonGia();
            int qty = item.getSoLuong() == null ? 0 : item.getSoLuong();
            totalQty += qty;
            for (int i = 0; i < qty; i++) {
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
        int value = promotion.getGiaTri().intValue();
        return value <= 0 ? 2 : value;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}