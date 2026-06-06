package com.project.web.mvc;

import com.project.dao.BookDAO;
import com.project.dao.CustomerDAO;
import com.project.dao.InvoiceDAO;
import com.project.dao.ReportDAO;
import com.project.model.Book;
import com.project.model.Customer;
import com.project.model.Invoice;
import com.project.model.InvoiceItem;
import com.project.model.dto.RevenueByDate;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    public static final String ADMIN_SESSION_KEY = "STORE_ADMIN";

    private final BookDAO bookDAO = new BookDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final ReportDAO reportDAO = new ReportDAO();

    private boolean isAdmin(HttpSession session) {
        return session.getAttribute(ADMIN_SESSION_KEY) != null;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpSession session, RedirectAttributes ra) {
        com.project.model.AuthUser admin = new com.project.dao.AuthDAO().login(username, password);
        if (admin == null) {
            ra.addFlashAttribute("error", "Invalid credentials");
            return "redirect:/admin/login";
        }
        session.setAttribute(ADMIN_SESSION_KEY, admin);
        return "redirect:/admin/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(ADMIN_SESSION_KEY);
        return "redirect:/admin/login";
    }

    @GetMapping({"", "/"})
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        try {
            com.project.model.dto.DashboardStats stats = new com.project.dao.DashboardDAO().loadStats();
            model.addAttribute("stats", stats);
        } catch (Exception e) {
            model.addAttribute("stats", null);
        }
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String products(@RequestParam(value = "q", required = false) String q,
                           HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        List<Book> books = bookDAO.findAll(q);
        model.addAttribute("books", books);
        model.addAttribute("keyword", q == null ? "" : q);
        return "admin/products";
    }

    @GetMapping("/products/add")
    public String addProductForm(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        model.addAttribute("book", new Book());
        model.addAttribute("editMode", false);
        return "admin/product-form";
    }

    @PostMapping("/products/add")
    public String addProduct(@RequestParam("tenSach") String tenSach,
                             @RequestParam("tacGia") String tacGia,
                             @RequestParam("theLoai") String theLoai,
                             @RequestParam("donGia") BigDecimal donGia,
                             @RequestParam("soLuong") int soLuong,
                             @RequestParam(value = "anhBia", required = false) String anhBia,
                             HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        Book book = new Book();
        book.setTenSach(tenSach);
        book.setTacGia(tacGia);
        book.setTheLoai(theLoai);
        book.setDonGia(donGia);
        book.setSoLuong(soLuong);
        book.setAnhBia(anhBia == null || anhBia.isEmpty() ? "/assets/seed/book-placeholder.svg" : anhBia);
        bookDAO.insert(book);
        ra.addFlashAttribute("success", "Product added successfully");
        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable("id") int id, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        Book book = bookDAO.findById(id);
        if (book == null) {
            return "redirect:/admin/products";
        }
        model.addAttribute("book", book);
        model.addAttribute("editMode", true);
        return "admin/product-form";
    }

    @PostMapping("/products/edit/{id}")
    public String editProduct(@PathVariable("id") int id,
                              @RequestParam("tenSach") String tenSach,
                              @RequestParam("tacGia") String tacGia,
                              @RequestParam("theLoai") String theLoai,
                              @RequestParam("donGia") BigDecimal donGia,
                              @RequestParam("soLuong") int soLuong,
                              @RequestParam(value = "anhBia", required = false) String anhBia,
                              HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        Book book = bookDAO.findById(id);
        if (book == null) return "redirect:/admin/products";
        book.setTenSach(tenSach);
        book.setTacGia(tacGia);
        book.setTheLoai(theLoai);
        book.setDonGia(donGia);
        book.setSoLuong(soLuong);
        if (anhBia != null && !anhBia.isEmpty()) {
            book.setAnhBia(anhBia);
        }
        bookDAO.update(book);
        ra.addFlashAttribute("success", "Product updated successfully");
        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id, HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        bookDAO.delete(id);
        ra.addFlashAttribute("success", "Product deleted successfully");
        return "redirect:/admin/products";
    }

    @GetMapping("/orders")
    public String orders(@RequestParam(value = "status", required = false) String status,
                         HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        List<Invoice> invoices = invoiceDAO.findAll();
        if (status != null && !status.isEmpty()) {
            List<Invoice> filtered = new ArrayList<>();
            for (Invoice inv : invoices) {
                if (status.equalsIgnoreCase(inv.getTrangThai())) {
                    filtered.add(inv);
                }
            }
            invoices = filtered;
        }
        model.addAttribute("invoices", invoices);
        model.addAttribute("selectedStatus", status == null ? "" : status);
        return "admin/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable("id") int id, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        Invoice invoice = invoiceDAO.findById(id);
        if (invoice == null) return "redirect:/admin/orders";
        List<InvoiceItem> items = invoiceDAO.findItemsByInvoiceId(id);
        model.addAttribute("invoice", invoice);
        model.addAttribute("items", items);
        return "admin/order-detail";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable("id") int id,
                                    @RequestParam("status") String status,
                                    HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        try {
            invoiceDAO.updateStatus(id, status);
            ra.addFlashAttribute("success", "Order status updated to " + status);
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to update status: " + e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }

    @GetMapping("/revenue")
    public String revenue(@RequestParam(value = "date", required = false) String dateStr,
                          HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        Date selectedDate;
        if (dateStr == null || dateStr.isEmpty()) {
            selectedDate = Date.valueOf(LocalDate.now());
        } else {
            try {
                selectedDate = Date.valueOf(dateStr);
            } catch (Exception e) {
                selectedDate = Date.valueOf(LocalDate.now());
            }
        }

        BigDecimal dayRevenue = reportDAO.sumRevenueOnDate(selectedDate);
        BigDecimal monthRevenue = reportDAO.sumRevenueForMonth(selectedDate);
        BigDecimal yearRevenue = reportDAO.sumRevenueForYear(selectedDate);

        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);
        cal.add(Calendar.DAY_OF_YEAR, -6);
        Date weekStart = new Date(cal.getTimeInMillis());
        List<RevenueByDate> weekData = reportDAO.findRevenueByDate(weekStart, selectedDate);

        List<String> chartLabels = new ArrayList<>();
        List<BigDecimal> chartValues = new ArrayList<>();
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd");
        Calendar iter = Calendar.getInstance();
        iter.setTime(weekStart);
        for (int i = 0; i < 7; i++) {
            Date d = new Date(iter.getTimeInMillis());
            chartLabels.add(fmt.format(d));
            boolean found = false;
            for (RevenueByDate rbd : weekData) {
                if (rbd.getNgay() != null && rbd.getNgay().equals(d)) {
                    chartValues.add(rbd.getDoanhThu());
                    found = true;
                    break;
                }
            }
            if (!found) {
                chartValues.add(BigDecimal.ZERO);
            }
            iter.add(Calendar.DAY_OF_YEAR, 1);
        }

        model.addAttribute("selectedDate", selectedDate.toString());
        model.addAttribute("dayRevenue", dayRevenue);
        model.addAttribute("monthRevenue", monthRevenue);
        model.addAttribute("yearRevenue", yearRevenue);
        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartValues", chartValues);
        return "admin/revenue";
    }
}
