package com.project.web.mvc;

import com.project.dao.BookDAO;
import com.project.dao.CustomerDAO;
import com.project.dao.InvoiceDAO;
import com.project.dao.InvoiceDAO.NewInvoiceItem;
import com.project.model.Book;
import com.project.model.Customer;
import com.project.model.Invoice;
import com.project.model.InvoiceItem;
import com.project.model.dto.ShopCartItem;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/store")
public class StoreController {

    public static final String CUSTOMER_SESSION_KEY = "STORE_CUSTOMER";
    public static final String CART_SESSION_KEY = "STORE_CART";

    private final BookDAO bookDAO = new BookDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    @GetMapping({"", "/"})
    public String index(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            Model model) {
        List<Book> all = bookDAO.findAll(keyword);
        if (category != null && !category.isEmpty()) {
            List<Book> filtered = new ArrayList<>();
            for (Book b : all) {
                if (category.equalsIgnoreCase(b.getTheLoai())) filtered.add(b);
            }
            all = filtered;
        }
        if ("asc".equalsIgnoreCase(sort)) {
            all.sort(Comparator.comparing(Book::getDonGia, Comparator.nullsLast(Comparator.naturalOrder())));
        } else if ("desc".equalsIgnoreCase(sort)) {
            all.sort(Comparator.comparing(Book::getDonGia, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        }
        int size = 12;
        int totalItems = all.size();
        int totalPages = Math.max(1, (int) Math.ceil(totalItems / (double) size));
        int safePage = Math.max(0, Math.min(page, totalPages - 1));
        int from = safePage * size;
        int to = Math.min(totalItems, from + size);
        List<Book> pageItems = from >= to ? Collections.emptyList() : all.subList(from, to);

        List<String> categories = new ArrayList<>();
        for (Book b : bookDAO.findAll(null)) {
            if (b.getTheLoai() != null && !categories.contains(b.getTheLoai())) {
                categories.add(b.getTheLoai());
            }
        }
        Collections.sort(categories);

        model.addAttribute("books", pageItems);
        model.addAttribute("currentPage", safePage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("categories", categories);
        return "shop/index";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam("id") int id, Model model) {
        Book book = bookDAO.findById(id);
        model.addAttribute("book", book);
        return "shop/detail";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "shop/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("account") String account,
                        @RequestParam("password") String password,
                        HttpSession session, RedirectAttributes ra) {
        Customer customer = customerDAO.loginCustomer(account, password);
        if (customer == null) {
            return "redirect:/store/login?error=1";
        }
        session.setAttribute(CUSTOMER_SESSION_KEY, customer);
        String redirect = (String) session.getAttribute("LOGIN_REDIRECT");
        if (redirect != null) {
            session.removeAttribute("LOGIN_REDIRECT");
            return "redirect:" + redirect;
        }
        return "redirect:/store/";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "shop/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam("hoTen") String hoTen,
                           @RequestParam("account") String account,
                           @RequestParam("password") String password,
                           @RequestParam("email") String email,
                           @RequestParam(value = "diaChi", required = false) String diaChi,
                           @RequestParam(value = "soDienThoai", required = false) String soDienThoai,
                           HttpSession session, RedirectAttributes ra) {
        Customer existing = customerDAO.findByAccount(account);
        if (existing != null) {
            ra.addFlashAttribute("error", "Account already exists");
            return "redirect:/store/register";
        }
        Customer customer = new Customer();
        customer.setTenKH(hoTen);
        customer.setDienThoai(soDienThoai);
        customer.setEmail(email);
        customer.setDiaChi(diaChi);
        customer.setTaiKhoan(account);
        customer.setMatKhau(password);
        try {
            customerDAO.insert(customer);
            Customer loggedIn = customerDAO.loginCustomer(account, password);
            if (loggedIn != null) {
                session.setAttribute(CUSTOMER_SESSION_KEY, loggedIn);
            }
            return "redirect:/store/?registered=1";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/store/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(CUSTOMER_SESSION_KEY);
        return "redirect:/store/";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("id") int id,
                            @RequestParam(value = "qty", defaultValue = "1") int qty,
                            HttpSession session) {
        Map<Integer, Integer> cart = getCart(session);
        cart.put(id, cart.getOrDefault(id, 0) + Math.max(1, qty));
        return "redirect:/store/cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam("id") int id,
                             @RequestParam("quantity") int quantity,
                             HttpSession session) {
        Map<Integer, Integer> cart = getCart(session);
        if (quantity <= 0) {
            cart.remove(id);
        } else {
            cart.put(id, quantity);
        }
        return "redirect:/store/cart";
    }

    @GetMapping("/cart/remove")
    public String removeFromCart(@RequestParam("id") int id, HttpSession session) {
        Map<Integer, Integer> cart = getCart(session);
        cart.remove(id);
        return "redirect:/store/cart";
    }

    @GetMapping("/cart")
    public String cart(HttpSession session, Model model) {
        Map<Integer, Integer> cart = getCart(session);
        List<ShopCartItem> items = buildCartItems(cart);
        BigDecimal total = BigDecimal.ZERO;
        for (ShopCartItem item : items) {
            total = total.add(item.getThanhTien());
        }
        if (items.isEmpty()) {
            model.addAttribute("emptyCart", "Your cart is empty");
        }
        model.addAttribute("cartItems", items);
        model.addAttribute("total", total);
        return "shop/cart";
    }

    @GetMapping("/checkout")
    public String checkoutForm(HttpSession session, Model model, RedirectAttributes ra) {
        Customer customer = (Customer) session.getAttribute(CUSTOMER_SESSION_KEY);
        if (customer == null) {
            session.setAttribute("LOGIN_REDIRECT", "/store/checkout");
            return "redirect:/store/login";
        }
        Map<Integer, Integer> cart = getCart(session);
        List<ShopCartItem> items = buildCartItems(cart);
        if (items.isEmpty()) {
            model.addAttribute("emptyCart", "Your cart is empty");
            return "shop/checkout";
        }
        BigDecimal total = BigDecimal.ZERO;
        for (ShopCartItem item : items) {
            total = total.add(item.getThanhTien());
        }
        model.addAttribute("cartItems", items);
        model.addAttribute("total", total);
        return "shop/checkout";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam("diaChi") String diaChi,
                           @RequestParam(value = "ghiChu", required = false) String ghiChu,
                           HttpSession session, RedirectAttributes ra) {
        Customer customer = (Customer) session.getAttribute(CUSTOMER_SESSION_KEY);
        if (customer == null) {
            return "redirect:/store/login";
        }
        Map<Integer, Integer> cart = getCart(session);
        if (cart.isEmpty()) {
            ra.addFlashAttribute("error", "Cart is empty");
            return "redirect:/store/cart";
        }
        List<NewInvoiceItem> invoiceItems = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : cart.entrySet()) {
            invoiceItems.add(new NewInvoiceItem(e.getKey(), e.getValue()));
        }
        try {
            int maHD = invoiceDAO.createInvoice(customer.getMaKH(), null, BigDecimal.ZERO, BigDecimal.ZERO, invoiceItems);
            session.removeAttribute(CART_SESSION_KEY);
            return "redirect:/store/order?code=" + maHD;
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Checkout failed: " + ex.getMessage());
            return "redirect:/store/checkout";
        }
    }

    @GetMapping("/my-orders")
    public String myOrders(HttpSession session, Model model, RedirectAttributes ra) {
        Customer customer = (Customer) session.getAttribute(CUSTOMER_SESSION_KEY);
        if (customer == null) {
            return "redirect:/store/login";
        }
        List<Invoice> orders = invoiceDAO.findByCustomerId(customer.getMaKH());
        model.addAttribute("orders", orders);
        return "shop/my-orders";
    }

    @GetMapping("/order")
    public String orderDetail(@RequestParam("code") int code,
                              HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute(CUSTOMER_SESSION_KEY);
        Invoice invoice = invoiceDAO.findById(code);
        if (invoice == null) {
            model.addAttribute("errorMessage", "Order not found");
            return "shop/order-detail";
        }
        boolean isOwner = customer != null && invoice.getMaKH() != null
                && invoice.getMaKH().equals(customer.getMaKH());
        if (!isOwner) {
            model.addAttribute("errorMessage", "You can only view your own orders");
            return "shop/order-detail";
        }
        List<InvoiceItem> items = invoiceDAO.findItemsByInvoiceId(code);
        model.addAttribute("invoice", invoice);
        model.addAttribute("items", items);
        return "shop/order-detail";
    }

    @GetMapping("/order-lookup")
    public String orderLookup() {
        return "shop/order-lookup";
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, Integer> getCart(HttpSession session) {
        Object raw = session.getAttribute(CART_SESSION_KEY);
        if (raw instanceof Map) {
            return (Map<Integer, Integer>) raw;
        }
        Map<Integer, Integer> cart = new LinkedHashMap<>();
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }

    private List<ShopCartItem> buildCartItems(Map<Integer, Integer> cart) {
        List<ShopCartItem> items = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            Book book = bookDAO.findById(entry.getKey());
            if (book == null || entry.getValue() <= 0) continue;
            ShopCartItem item = new ShopCartItem();
            item.setMaSach(book.getMaSach());
            item.setTenSach(book.getTenSach());
            item.setTacGia(book.getTacGia());
            item.setDonGia(book.getDonGia());
            item.setSoLuong(entry.getValue());
            item.setTonKho(book.getSoLuong());
            item.setAnhBia(book.getAnhBia());
            items.add(item);
        }
        return items;
    }
}
