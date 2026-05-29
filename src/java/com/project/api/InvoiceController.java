package com.project.api;

import com.project.dao.BookDAO;
import com.project.dao.CustomerDAO;
import com.project.dao.InvoiceDAO;
import com.project.dao.InvoiceDAO.NewInvoiceItem;
import com.project.dao.PromotionDAO;
import com.project.model.Book;
import com.project.model.Customer;
import com.project.model.Invoice;
import com.project.model.InvoiceItem;
import com.project.model.Promotion;
import java.math.BigDecimal;
// SQLException removed; use unchecked exceptions for validation errors
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final BookDAO bookDAO = new BookDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final PromotionDAO promotionDAO = new PromotionDAO();

    @GetMapping
    public ResponseEntity<?> list() {
        try {
            return ResponseEntity.ok(invoiceDAO.findAll());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") int id) {
        try {
            Invoice invoice = invoiceDAO.findById(id);
            if (invoice == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Khong tim thay hoa don."));
            }
            List<InvoiceItem> items = invoiceDAO.findItemsByInvoiceId(id);
            Map<String, Object> body = new LinkedHashMap<String, Object>();
            body.put("invoice", invoice);
            body.put("items", items);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable("id") int id, @RequestBody InvoiceStatusRequest request) {
        try {
            invoiceDAO.updateStatus(id, request == null ? null : request.getStatus());
            Map<String, Object> body = new LinkedHashMap<String, Object>();
            body.put("maHD", Integer.valueOf(id));
            body.put("status", invoiceDAO.normalizeStatus(request == null ? null : request.getStatus()));
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody InvoiceCreateRequest request) {
        try {
            List<NewInvoiceItem> items = extractItems(request.getItems());
            if (items.isEmpty()) {
                return ResponseEntity.badRequest().body(error("Vui long them it nhat 1 dong sach hop le (so luong > 0)."));
            }

            BigDecimal giamGia = request.getGiamGia() == null ? BigDecimal.ZERO : request.getGiamGia();
            BigDecimal thueVAT = request.getThueVAT() == null ? BigDecimal.ZERO : request.getThueVAT();
            BigDecimal couponDiscount = calculateCouponDiscount(request.getCouponCode(), items);
            BigDecimal finalDiscount = giamGia.add(couponDiscount);

            int maHD = invoiceDAO.createInvoice(request.getMaKH(), request.getMaNV(), finalDiscount, thueVAT, items);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Collections.singletonMap("maHD", Integer.valueOf(maHD)));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @GetMapping("/form-data")
    public ResponseEntity<?> formData() {
        Map<String, Object> body = new HashMap<String, Object>();
        try {
            List<Book> books = bookDAO.findAll(null);
            body.put("books", books);
            Map<Integer, BigDecimal> bookPriceMap = new HashMap<Integer, BigDecimal>();
            for (Book book : books) {
                if (book.getMaSach() != null) {
                    bookPriceMap.put(book.getMaSach(), book.getDonGia() == null ? BigDecimal.ZERO : book.getDonGia());
                }
            }
            body.put("bookPriceMap", bookPriceMap);
        } catch (Exception ex) {
            body.put("books", Collections.emptyList());
            body.put("bookPriceMap", Collections.emptyMap());
            body.put("bookError", ex.getMessage());
        }

        try {
            List<Customer> customers = customerDAO.findAll(null);
            body.put("customers", customers);
        } catch (Exception ex) {
            body.put("customers", Collections.emptyList());
            body.put("customerError", ex.getMessage());
        }

        try {
            List<Promotion> promotions = promotionDAO.findActivePromotions();
            body.put("promotions", promotions);
        } catch (Exception ex) {
            body.put("promotions", Collections.emptyList());
            body.put("promotionError", ex.getMessage());
        }

        return ResponseEntity.ok(body);
    }

    private List<NewInvoiceItem> extractItems(List<InvoiceItemRequest> rows) {
        if (rows == null) {
            return Collections.emptyList();
        }

        List<NewInvoiceItem> items = new ArrayList<NewInvoiceItem>();
        for (InvoiceItemRequest row : rows) {
            if (row == null || row.getMaSach() == null || row.getSoLuong() == null || row.getSoLuong().intValue() <= 0) {
                continue;
            }
            items.add(new NewInvoiceItem(row.getMaSach().intValue(), row.getSoLuong().intValue()));
        }
        return items;
    }

    private BigDecimal calculateCouponDiscount(String couponCode, List<NewInvoiceItem> items) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        Map<Integer, BigDecimal> priceMap = loadBookPriceMap();
        BigDecimal subtotal = calculateSubtotal(items, priceMap);

        Promotion promotion = promotionDAO.findByCode(couponCode.trim());
        if (promotion == null) {
            throw new RuntimeException("Khong tim thay coupon hop le hoac coupon da het han.");
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

    private Map<Integer, BigDecimal> loadBookPriceMap() {
        List<Book> books = bookDAO.findAll(null);
        Map<Integer, BigDecimal> priceMap = new HashMap<Integer, BigDecimal>();
        for (Book book : books) {
            if (book.getMaSach() != null) {
                priceMap.put(book.getMaSach(), book.getDonGia() == null ? BigDecimal.ZERO : book.getDonGia());
            }
        }
        return priceMap;
    }

    private BigDecimal calculateSubtotal(List<NewInvoiceItem> items, Map<Integer, BigDecimal> priceMap) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (NewInvoiceItem item : items) {
            BigDecimal unitPrice = priceMap.get(Integer.valueOf(item.getMaSach()));
            if (unitPrice == null) {
                throw new RuntimeException("Khong tim thay gia sach ma " + item.getMaSach());
            }
            subtotal = subtotal.add(unitPrice.multiply(BigDecimal.valueOf(item.getSoLuong())));
        }
        return subtotal;
    }

    private BigDecimal calculateTang1Discount(Promotion promotion, List<NewInvoiceItem> items, Map<Integer, BigDecimal> priceMap) {

        int buyQty = getTang1BuyQty(promotion);
        int totalQty = 0;
        List<BigDecimal> unitPrices = new ArrayList<BigDecimal>();

        for (NewInvoiceItem item : items) {
            BigDecimal unitPrice = priceMap.get(Integer.valueOf(item.getMaSach()));
            if (unitPrice == null) {
                throw new RuntimeException("Khong tim thay gia sach ma " + item.getMaSach());
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
        int value = promotion.getGiaTri().intValue();
        return value <= 0 ? 2 : value;
    }

    private Map<String, String> error(String message) {
        return Collections.singletonMap("error", message);
    }

    public static class InvoiceCreateRequest {
        private Integer maKH;
        private String maNV;
        private BigDecimal giamGia;
        private BigDecimal thueVAT;
        private String couponCode;
        private List<InvoiceItemRequest> items;

        public Integer getMaKH() {
            return maKH;
        }

        public void setMaKH(Integer maKH) {
            this.maKH = maKH;
        }

        public String getMaNV() {
            return maNV;
        }

        public void setMaNV(String maNV) {
            this.maNV = maNV;
        }

        public BigDecimal getGiamGia() {
            return giamGia;
        }

        public void setGiamGia(BigDecimal giamGia) {
            this.giamGia = giamGia;
        }

        public BigDecimal getThueVAT() {
            return thueVAT;
        }

        public void setThueVAT(BigDecimal thueVAT) {
            this.thueVAT = thueVAT;
        }

        public String getCouponCode() {
            return couponCode;
        }

        public void setCouponCode(String couponCode) {
            this.couponCode = couponCode;
        }

        public List<InvoiceItemRequest> getItems() {
            return items;
        }

        public void setItems(List<InvoiceItemRequest> items) {
            this.items = items;
        }
    }

    public static class InvoiceItemRequest {
        private Integer maSach;
        private Integer soLuong;

        public Integer getMaSach() {
            return maSach;
        }

        public void setMaSach(Integer maSach) {
            this.maSach = maSach;
        }

        public Integer getSoLuong() {
            return soLuong;
        }

        public void setSoLuong(Integer soLuong) {
            this.soLuong = soLuong;
        }
    }

    public static class InvoiceStatusRequest {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
