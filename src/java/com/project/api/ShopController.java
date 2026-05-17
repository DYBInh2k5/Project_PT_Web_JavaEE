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
import com.project.model.dto.ShopCartItem;
import com.project.model.dto.ShopOrderSummary;
import com.project.web.shop.ShopCartSupport;
import com.project.web.shop.ShopOrderAccessSupport;
import com.project.web.shop.ShopOrderCodeUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
// SQLException removed; use unchecked exceptions for validation errors
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    private final BookDAO bookDAO = new BookDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final PromotionDAO promotionDAO = new PromotionDAO();

    @GetMapping("/books")
    public ResponseEntity<?> books(@RequestParam(value = "q", required = false) String q, HttpSession session) {
        try {
            List<Book> books = bookDAO.findAll(q);
            Map<String, Object> body = new LinkedHashMap<String, Object>();
            body.put("books", books);
            body.put("cartCount", Integer.valueOf(ShopCartSupport.getCartCount(ShopCartSupport.getCart(session))));
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<?> bookDetail(@PathVariable("id") int id, HttpSession session) {
        try {
            Book book = bookDAO.findById(id);
            if (book == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Khong tim thay sach."));
            }
            Map<String, Object> body = new LinkedHashMap<String, Object>();
            body.put("book", book);
            body.put("cartCount", Integer.valueOf(ShopCartSupport.getCartCount(ShopCartSupport.getCart(session))));
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @GetMapping("/cart")
    public ResponseEntity<?> cart(HttpSession session) {
        Map<Integer, Integer> cart = ShopCartSupport.getCart(session);
        try {
            List<ShopCartItem> items = ShopCartSupport.buildCartItems(bookDAO, cart);
            BigDecimal total = BigDecimal.ZERO;
            for (ShopCartItem item : items) {
                total = total.add(item.getThanhTien());
            }
            Map<String, Object> body = new LinkedHashMap<String, Object>();
            body.put("items", items);
            body.put("total", total);
            body.put("cartCount", Integer.valueOf(ShopCartSupport.getCartCount(cart)));
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @PostMapping("/cart/add")
    public ResponseEntity<?> addToCart(@RequestBody CartAddRequest request, HttpSession session) {
        if (request == null || request.getId() == null) {
            return ResponseEntity.badRequest().body(error("Thieu ma sach."));
        }
        int qty = request.getQty() == null || request.getQty().intValue() <= 0 ? 1 : request.getQty().intValue();
        Map<Integer, Integer> cart = ShopCartSupport.getCart(session);
        Integer oldQty = cart.get(request.getId());
        int newQty = (oldQty == null ? 0 : oldQty.intValue()) + qty;
        if (newQty > 999) {
            newQty = 999;
        }
        cart.put(request.getId(), Integer.valueOf(newQty));
        return cart(session);
    }

    @PutMapping("/cart/{id}")
    public ResponseEntity<?> updateCart(@PathVariable("id") int id, @RequestBody CartUpdateRequest request, HttpSession session) {
        Map<Integer, Integer> cart = ShopCartSupport.getCart(session);
        if (request == null || request.getQty() == null || request.getQty().intValue() <= 0) {
            cart.remove(Integer.valueOf(id));
        } else {
            int qty = request.getQty().intValue();
            if (qty > 999) {
                qty = 999;
            }
            cart.put(Integer.valueOf(id), Integer.valueOf(qty));
        }
        return cart(session);
    }

    @DeleteMapping("/cart/{id}")
    public ResponseEntity<?> removeCart(@PathVariable("id") int id, HttpSession session) {
        ShopCartSupport.getCart(session).remove(Integer.valueOf(id));
        return cart(session);
    }

    @GetMapping("/checkout")
    public ResponseEntity<?> checkoutData(HttpSession session) {
        Map<Integer, Integer> cart = ShopCartSupport.getCart(session);
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("cartCount", Integer.valueOf(ShopCartSupport.getCartCount(cart)));

        try {
            List<ShopCartItem> items = ShopCartSupport.buildCartItems(bookDAO, cart);
            body.put("items", items);
            body.put("subtotal", calculateSubtotal(items));
            body.put("promotions", promotionDAO.findActivePromotions());
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            body.put("items", Collections.emptyList());
            body.put("subtotal", BigDecimal.ZERO);
            body.put("promotions", Collections.emptyList());
            body.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest request, HttpSession session) {
        Map<Integer, Integer> cart = ShopCartSupport.getCart(session);
        try {
            List<ShopCartItem> cartItems = ShopCartSupport.buildCartItems(bookDAO, cart);
            if (cartItems.isEmpty()) {
                return ResponseEntity.badRequest().body(error("Gio hang dang trong. Vui long them sach truoc khi thanh toan."));
            }

            List<NewInvoiceItem> newInvoiceItems = buildInvoiceItems(cartItems);
            BigDecimal discount = calculateCouponDiscount(request == null ? null : request.getCouponCode(), cartItems);
            Integer maKH = buildCustomerIfProvided(request);
            int maHD = invoiceDAO.createInvoice(maKH, null, discount, BigDecimal.ZERO, newInvoiceItems);

            cart.clear();
            ShopCartSupport.addOrderToHistory(session, maHD);
            String orderCode = ShopOrderCodeUtil.encode(maHD);

            Map<String, Object> result = new LinkedHashMap<String, Object>();
            result.put("success", Boolean.TRUE);
            result.put("invoiceId", Integer.valueOf(maHD));
            result.put("orderCode", orderCode);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @GetMapping("/order-lookup")
    public ResponseEntity<?> orderLookup(
            @RequestParam(value = "invoiceId", required = false) Integer invoiceId,
            @RequestParam(value = "phone", required = false) String phoneRaw,
            @RequestParam(value = "email", required = false) String emailRaw,
            HttpSession session) {

        String phone = trimToNull(phoneRaw);
        String email = trimToNull(emailRaw);
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("cartCount", Integer.valueOf(ShopCartSupport.getCartCount(ShopCartSupport.getCart(session))));
        body.put("invoiceId", invoiceId);
        body.put("phone", phone == null ? "" : phone);
        body.put("email", email == null ? "" : email);

        if (invoiceId != null && (phone != null || email != null)) {
            try {
                Invoice invoice = invoiceDAO.findByIdForCustomerLookup(invoiceId.intValue(), phone, email);
                if (invoice == null) {
                    body.put("error", "Khong tim thay don hang phu hop voi thong tin da nhap.");
                } else {
                    List<InvoiceItem> items = invoiceDAO.findItemsByInvoiceId(invoiceId.intValue());
                    body.put("invoice", invoice);
                    body.put("items", items);
                    body.put("orderCode", ShopOrderCodeUtil.encode(invoiceId.intValue()));
                }
            } catch (Exception ex) {
                body.put("error", ex.getMessage());
                body.put("items", Collections.emptyList());
            }
        }

        return ResponseEntity.ok(body);
    }

    @GetMapping("/order")
    public ResponseEntity<?> orderDetail(
            @RequestParam("code") String code,
            @RequestParam(value = "phone", required = false) String phoneRaw,
            @RequestParam(value = "email", required = false) String emailRaw,
            HttpSession session) {

        Integer maHD = ShopOrderCodeUtil.decode(code);
        if (maHD == null) {
            return ResponseEntity.badRequest().body(error("Ma don khong hop le."));
        }

        String phone = trimToNull(phoneRaw);
        String email = trimToNull(emailRaw);
        boolean canAccess = ShopOrderAccessSupport.isOrderInHistory(session, maHD) || ShopOrderAccessSupport.hasCode(session, code);

        try {
            if (!canAccess && (phone != null || email != null)) {
                Invoice verified = invoiceDAO.findByIdForCustomerLookup(maHD.intValue(), phone, email);
                if (verified != null) {
                    ShopOrderAccessSupport.grantCode(session, code);
                    canAccess = true;
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error("Thong tin xac thuc khong dung voi don hang."));
                }
            }

            if (!canAccess) {
                Map<String, Object> body = new LinkedHashMap<String, Object>();
                body.put("verificationRequired", Boolean.TRUE);
                body.put("orderCode", code);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
            }

            Invoice invoice = invoiceDAO.findById(maHD.intValue());
            if (invoice == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Khong tim thay don hang."));
            }

            List<InvoiceItem> items = invoiceDAO.findItemsByInvoiceId(maHD.intValue());
            Map<String, Object> body = new LinkedHashMap<String, Object>();
            body.put("invoice", invoice);
            body.put("items", items);
            body.put("orderCode", ShopOrderCodeUtil.encode(maHD.intValue()));
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @GetMapping("/my-orders")
    public ResponseEntity<?> myOrders(HttpSession session) {
        List<Integer> historyIds = ShopCartSupport.getOrderHistory(session);
        if (historyIds.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        try {
            List<Invoice> invoices = invoiceDAO.findByIds(historyIds);
            List<ShopOrderSummary> orders = new ArrayList<ShopOrderSummary>();
            for (Invoice invoice : invoices) {
                if (invoice == null || invoice.getMaHD() == null) {
                    continue;
                }
                List<InvoiceItem> items = invoiceDAO.findItemsByInvoiceId(invoice.getMaHD().intValue());
                int itemCount = 0;
                for (InvoiceItem item : items) {
                    if (item.getSoLuong() != null && item.getSoLuong().intValue() > 0) {
                        itemCount += item.getSoLuong().intValue();
                    }
                }
                ShopOrderSummary summary = new ShopOrderSummary();
                summary.setInvoice(invoice);
                summary.setItemCount(itemCount);
                summary.setOrderCode(ShopOrderCodeUtil.encode(invoice.getMaHD().intValue()));
                orders.add(summary);
            }
            return ResponseEntity.ok(orders);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @GetMapping("/order/pdf")
    public ResponseEntity<?> orderPdf(
            @RequestParam("code") String code,
            @RequestParam(value = "phone", required = false) String phoneRaw,
            @RequestParam(value = "email", required = false) String emailRaw,
            HttpSession session) {

        Integer maHD = ShopOrderCodeUtil.decode(code);
        if (maHD == null) {
            return ResponseEntity.badRequest().body(error("Invalid order code"));
        }

        boolean canAccess = ShopOrderAccessSupport.isOrderInHistory(session, maHD) || ShopOrderAccessSupport.hasCode(session, code);
        if (!canAccess) {
            String phone = trimToNull(phoneRaw);
            String email = trimToNull(emailRaw);
            if (phone != null || email != null) {
                try {
                    Invoice verified = invoiceDAO.findByIdForCustomerLookup(maHD.intValue(), phone, email);
                    if (verified != null) {
                        ShopOrderAccessSupport.grantCode(session, code);
                        canAccess = true;
                    }
                } catch (Exception ex) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("Cannot verify order"));
                }
            }
        }

        if (!canAccess) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error("Order access denied"));
        }

        try {
            Invoice invoice = invoiceDAO.findById(maHD.intValue());
            if (invoice == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Order not found"));
            }

            List<InvoiceItem> items = invoiceDAO.findItemsByInvoiceId(maHD.intValue());
            List<String> lines = buildLines(invoice, items, ShopOrderCodeUtil.encode(maHD.intValue()));
            byte[] pdf = buildSimplePdf(lines);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "order-" + maHD + ".pdf");
            return new ResponseEntity<byte[]>(pdf, headers, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("Cannot generate PDF"));
        }
    }

    private List<NewInvoiceItem> buildInvoiceItems(List<ShopCartItem> cartItems) {
        List<NewInvoiceItem> items = new ArrayList<NewInvoiceItem>();
        for (ShopCartItem item : cartItems) {
            if (item.getSoLuong() == null || item.getSoLuong().intValue() <= 0) {
                continue;
            }
            int tonKho = item.getTonKho() == null ? 0 : item.getTonKho().intValue();
            if (item.getSoLuong().intValue() > tonKho) {
                throw new RuntimeException("Sach \"" + item.getTenSach() + "\" khong du ton kho.");
            }
            items.add(new NewInvoiceItem(item.getMaSach().intValue(), item.getSoLuong().intValue()));
        }
        if (items.isEmpty()) {
            throw new RuntimeException("Khong co dong sach hop le trong gio hang.");
        }
        return items;
    }

    private Integer buildCustomerIfProvided(CheckoutRequest request) {
        if (request == null) {
            return null;
        }

        String fullName = trimToNull(request.getFullName());
        String phone = trimToNull(request.getPhone());
        String email = trimToNull(request.getEmail());
        String address = trimToNull(request.getAddress());
        if (fullName == null) {
            return null;
        }

        Customer existing = customerDAO.findByPhoneOrEmail(phone, email);
        if (existing != null) {
            existing.setTenKH(fullName);
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
        return Integer.valueOf(customerDAO.insertAndGetId(customer));
    }

    private BigDecimal calculateCouponDiscount(String couponCode, List<ShopCartItem> items) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        Promotion promotion = promotionDAO.findByCode(couponCode.trim());
        if (promotion == null) {
            throw new RuntimeException("Khong tim thay coupon hop le hoac coupon da het han.");
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
            int qty = item.getSoLuong() == null ? 0 : item.getSoLuong().intValue();
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

    private List<String> buildLines(Invoice invoice, List<InvoiceItem> items, String orderCode) {
        List<String> lines = new ArrayList<String>();
        lines.add("BOOKORA ORDER INVOICE");
        lines.add("Order code: " + orderCode);
        lines.add("Invoice id: " + invoice.getMaHD());
        lines.add("Date: " + invoice.getNgayLap());
        lines.add("Customer: " + safe(invoice.getTenKH()));
        lines.add("Phone: " + safe(invoice.getDienThoaiKH()));
        lines.add("Email: " + safe(invoice.getEmailKH()));
        lines.add("---------------------------------------------");
        if (items == null || items.isEmpty()) {
            lines.add("No items");
        } else {
            for (InvoiceItem item : items) {
                lines.add(safe(item.getTenSach()) + " | Qty: " + item.getSoLuong() + " | Unit: " + item.getDonGia() + " | Total: " + item.getThanhTien());
            }
        }
        lines.add("---------------------------------------------");
        lines.add("Discount: " + (invoice.getGiamGia() == null ? 0 : invoice.getGiamGia()));
        lines.add("VAT: " + (invoice.getThueVAT() == null ? 0 : invoice.getThueVAT()));
        lines.add("Total: " + (invoice.getTongTien() == null ? 0 : invoice.getTongTien()));
        return lines;
    }

    private byte[] buildSimplePdf(List<String> lines) {
        try {
            StringBuilder content = new StringBuilder();
            content.append("BT\n");
            content.append("/F1 11 Tf\n");
            content.append("14 TL\n");
            content.append("50 790 Td\n");

            int count = 0;
            for (String line : lines) {
                if (count > 52) {
                    break;
                }
                content.append("(").append(escapePdfText(line)).append(") Tj\nT*\n");
                count++;
            }
            content.append("ET\n");

            byte[] streamBytes = content.toString().getBytes(StandardCharsets.ISO_8859_1);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            List<Integer> offsets = new ArrayList<Integer>();

            out.write("%PDF-1.4\n".getBytes(StandardCharsets.ISO_8859_1));
            offsets.add(Integer.valueOf(out.size()));
            out.write("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));
            offsets.add(Integer.valueOf(out.size()));
            out.write("2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));
            offsets.add(Integer.valueOf(out.size()));
            out.write("3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));
            offsets.add(Integer.valueOf(out.size()));
            out.write("4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));
            offsets.add(Integer.valueOf(out.size()));
            out.write(("5 0 obj\n<< /Length " + streamBytes.length + " >>\nstream\n").getBytes(StandardCharsets.ISO_8859_1));
            out.write(streamBytes);
            out.write("endstream\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));

            int xrefOffset = out.size();
            out.write(("xref\n0 " + (offsets.size() + 1) + "\n").getBytes(StandardCharsets.ISO_8859_1));
            out.write("0000000000 65535 f \n".getBytes(StandardCharsets.ISO_8859_1));
            for (Integer offset : offsets) {
                out.write(String.format("%010d 00000 n \n", offset).getBytes(StandardCharsets.ISO_8859_1));
            }
            out.write(("trailer\n<< /Size " + (offsets.size() + 1) + " /Root 1 0 R >>\nstartxref\n" + xrefOffset + "\n%%EOF").getBytes(StandardCharsets.ISO_8859_1));
            return out.toByteArray();
        } catch (IOException ex) {
            return new byte[0];
        }
    }

    private String escapePdfText(String text) {
        if (text == null) {
            return "";
        }
        String normalized = text.replaceAll("[^\\x20-\\x7E]", "?");
        return normalized.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private String safe(String v) {
        return v == null ? "" : v;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    private Map<String, String> error(String message) {
        return Collections.singletonMap("error", message);
    }

    public static class CartAddRequest {
        private Integer id;
        private Integer qty;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getQty() {
            return qty;
        }

        public void setQty(Integer qty) {
            this.qty = qty;
        }
    }

    public static class CartUpdateRequest {
        private Integer qty;

        public Integer getQty() {
            return qty;
        }

        public void setQty(Integer qty) {
            this.qty = qty;
        }
    }

    public static class CheckoutRequest {
        private String fullName;
        private String phone;
        private String email;
        private String address;
        private String couponCode;

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCouponCode() {
            return couponCode;
        }

        public void setCouponCode(String couponCode) {
            this.couponCode = couponCode;
        }
    }
}
