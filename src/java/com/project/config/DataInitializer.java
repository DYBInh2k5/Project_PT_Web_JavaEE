package com.project.config;

import com.project.dao.BookDAO;
import com.project.dao.CustomerDAO;
import com.project.dao.JpaSupport;
import com.project.model.Book;
import com.project.model.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final String DEFAULT_BOOK_IMAGE = "/assets/seed/book-placeholder.svg";

    private final BookDAO bookDAO = new BookDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    public void run(String... args) {
        ensureInvoiceStatusColumn();
        ensureAdminAccount();
        ensureCustomers();
        ensureBooks();
    }

    private void ensureInvoiceStatusColumn() {
        EntityManager em = JpaSupport.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Object result = em.createNativeQuery("SELECT COL_LENGTH('dbo.HoaDon', 'TrangThai')")
                    .getSingleResult();
            if (result == null) {
                em.createNativeQuery("ALTER TABLE dbo.HoaDon ADD TrangThai NVARCHAR(30) NULL")
                        .executeUpdate();
                em.createNativeQuery("UPDATE dbo.HoaDon SET TrangThai = 'NEW' WHERE TrangThai IS NULL")
                        .executeUpdate();
                em.createNativeQuery("ALTER TABLE dbo.HoaDon ADD CONSTRAINT DF_HoaDon_TrangThai DEFAULT 'NEW' FOR TrangThai")
                        .executeUpdate();
            }
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
        } finally {
            em.close();
        }
    }

    private void ensureAdminAccount() {
        if (countRows("SELECT COUNT(*) FROM dbo.NhanVien WHERE TaiKhoan = ?", "admin") > 0) {
            return;
        }

        EntityManager em = JpaSupport.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery("INSERT INTO dbo.NhanVien (HoTen, TaiKhoan, MatKhau, VaiTro) VALUES (?, ?, ?, ?)")
                    .setParameter(1, "Administrator")
                    .setParameter(2, "admin")
                    .setParameter(3, "123456")
                    .setParameter(4, "Admin")
                    .executeUpdate();
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
        } finally {
            em.close();
        }
    }

    private void ensureCustomers() {
        seedCustomer("Nguyen Van A", "0909000001", "a@example.com", "Ha Noi");
        seedCustomer("Tran Thi B", "0909000002", "b@example.com", "Ho Chi Minh");
    }

    private void seedCustomer(String tenKH, String dienThoai, String email, String diaChi) {
        if (customerDAO.findByPhoneOrEmail(dienThoai, email) != null) {
            return;
        }

        Customer customer = new Customer();
        customer.setTenKH(tenKH);
        customer.setDienThoai(dienThoai);
        customer.setEmail(email);
        customer.setDiaChi(diaChi);
        customerDAO.insertAndGetId(customer);
    }

    private void ensureBooks() {
        List<Book> existing = bookDAO.findAll(null);
        if (existing.size() >= 30) {
            return;
        }

        Set<String> existingTitles = new HashSet<String>();
        for (Book book : existing) {
            if (book.getTenSach() != null) {
                existingTitles.add(book.getTenSach().trim().toLowerCase());
            }
        }

        for (BookSeed seed : createBookSeeds()) {
            if (existingTitles.contains(seed.title.toLowerCase())) {
                continue;
            }
            Book book = new Book();
            book.setTenSach(seed.title);
            book.setTacGia(seed.author);
            book.setTheLoai(seed.category);
            book.setDonGia(seed.price);
            book.setSoLuong(seed.stock);
            book.setAnhBia(DEFAULT_BOOK_IMAGE);
            bookDAO.insert(book);
            existingTitles.add(seed.title.toLowerCase());
            if (bookDAO.findAll(null).size() >= 30) {
                break;
            }
        }
    }

    private int countRows(String sql, Object param) {
        EntityManager em = JpaSupport.createEntityManager();
        try {
            Object value = em.createNativeQuery(sql)
                    .setParameter(1, param)
                    .getSingleResult();
            return value == null ? 0 : ((Number) value).intValue();
        } finally {
            em.close();
        }
    }

    private List<BookSeed> createBookSeeds() {
        List<BookSeed> seeds = new ArrayList<BookSeed>();
        seeds.add(new BookSeed("Java Co Ban", "Nguyen Van A", "Lap trinh", new BigDecimal("89000"), 50));
        seeds.add(new BookSeed("Java Nang Cao", "Nguyen Van A", "Lap trinh", new BigDecimal("99000"), 45));
        seeds.add(new BookSeed("Spring Boot 101", "Le Minh", "Lap trinh", new BigDecimal("129000"), 40));
        seeds.add(new BookSeed("Spring Boot MVC", "Le Minh", "Lap trinh", new BigDecimal("139000"), 35));
        seeds.add(new BookSeed("JPA and Hibernate", "Pham Nam", "Lap trinh", new BigDecimal("149000"), 38));
        seeds.add(new BookSeed("Thymeleaf Mastery", "Pham Nam", "Lap trinh", new BigDecimal("119000"), 32));
        seeds.add(new BookSeed("SQL Server Essentials", "Tran Anh", "Co so du lieu", new BigDecimal("99000"), 30));
        seeds.add(new BookSeed("Database Design", "Tran Anh", "Co so du lieu", new BigDecimal("109000"), 30));
        seeds.add(new BookSeed("Clean Code", "Robert C. Martin", "Ky nang lap trinh", new BigDecimal("159000"), 25));
        seeds.add(new BookSeed("Refactoring", "Martin Fowler", "Ky nang lap trinh", new BigDecimal("169000"), 22));
        seeds.add(new BookSeed("Design Patterns", "Erich Gamma", "Ky nang lap trinh", new BigDecimal("179000"), 20));
        seeds.add(new BookSeed("Effective Java", "Joshua Bloch", "Ky nang lap trinh", new BigDecimal("189000"), 18));
        seeds.add(new BookSeed("Head First Java", "Kathy Sierra", "Lap trinh", new BigDecimal("139000"), 28));
        seeds.add(new BookSeed("Core Java Volume I", "Cay Horstmann", "Lap trinh", new BigDecimal("159000"), 24));
        seeds.add(new BookSeed("Core Java Volume II", "Cay Horstmann", "Lap trinh", new BigDecimal("169000"), 21));
        seeds.add(new BookSeed("Thinking in Java", "Bruce Eckel", "Lap trinh", new BigDecimal("149000"), 22));
        seeds.add(new BookSeed("Pro Spring", "Rob Harrop", "Lap trinh", new BigDecimal("199000"), 16));
        seeds.add(new BookSeed("Microservices Patterns", "Chris Richardson", "He thong", new BigDecimal("209000"), 15));
        seeds.add(new BookSeed("REST API Design", "Mark Masse", "Web", new BigDecimal("129000"), 27));
        seeds.add(new BookSeed("Web Performance", "Ilya Grigorik", "Web", new BigDecimal("139000"), 23));
        seeds.add(new BookSeed("Bootstrap Handbook", "Mark Otto", "Web", new BigDecimal("99000"), 34));
        seeds.add(new BookSeed("HTML and CSS", "Jon Duckett", "Web", new BigDecimal("85000"), 36));
        seeds.add(new BookSeed("JavaScript Basics", "Ethan Brown", "Web", new BigDecimal("95000"), 33));
        seeds.add(new BookSeed("Node.js in Action", "Mike Cantelon", "Web", new BigDecimal("179000"), 17));
        seeds.add(new BookSeed("Security in Practice", "John R. Vacca", "An ninh", new BigDecimal("149000"), 19));
        seeds.add(new BookSeed("Software Engineering", "Ian Sommerville", "Phan mem", new BigDecimal("179000"), 18));
        seeds.add(new BookSeed("Project Management", "Kathy Schwalbe", "Quan tri", new BigDecimal("129000"), 20));
        seeds.add(new BookSeed("UX Design", "Don Norman", "Thiet ke", new BigDecimal("119000"), 29));
        seeds.add(new BookSeed("Algorithms", "Robert Sedgewick", "Thuat toan", new BigDecimal("199000"), 14));
        seeds.add(new BookSeed("Data Structures", "Mark Allen Weiss", "Thuat toan", new BigDecimal("189000"), 15));
        return seeds;
    }

    private static final class BookSeed {
        private final String title;
        private final String author;
        private final String category;
        private final BigDecimal price;
        private final int stock;

        private BookSeed(String title, String author, String category, BigDecimal price, int stock) {
            this.title = title;
            this.author = author;
            this.category = category;
            this.price = price;
            this.stock = stock;
        }
    }
}
