package com.project.api;

import com.project.dao.CustomerDAO;
import com.project.model.Customer;
import com.project.web.auth.AuthSession;
import java.util.Collections;
import java.util.Map;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer-auth")
public class CustomerAuthController {

    private final CustomerDAO customerDAO = new CustomerDAO();

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request, HttpSession session) {
        if (request == null || isBlank(request.getTenKH()) || isBlank(request.getUsername()) || isBlank(request.getPassword())) {
            return ResponseEntity.badRequest().body(error("Vui long nhap day du thong tin dang ky."));
        }

        try {
            if (customerDAO.findByAccount(request.getUsername()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error("Tai khoan da ton tai."));
            }

            Customer customer = new Customer();
            customer.setTenKH(request.getTenKH().trim());
            customer.setDienThoai(emptyToNull(request.getDienThoai()));
            customer.setEmail(emptyToNull(request.getEmail()));
            customer.setDiaChi(emptyToNull(request.getDiaChi()));
            customer.setTaiKhoan(request.getUsername().trim());
            customer.setMatKhau(request.getPassword());

            int id = customerDAO.insertAndGetId(customer);
            customer.setMaKH(id);
            session.setAttribute(AuthSession.AUTH_CUSTOMER, customer);
            session.setMaxInactiveInterval(30 * 60);
            return ResponseEntity.status(HttpStatus.CREATED).body(customer);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        if (request == null || isBlank(request.getUsername()) || isBlank(request.getPassword())) {
            return ResponseEntity.badRequest().body(error("Vui long nhap day du tai khoan va mat khau."));
        }

        try {
            Customer customer = customerDAO.loginCustomer(request.getUsername(), request.getPassword());
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error("Sai tai khoan hoac mat khau."));
            }
            session.setAttribute(AuthSession.AUTH_CUSTOMER, customer);
            session.setMaxInactiveInterval(30 * 60);
            return ResponseEntity.ok(customer);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpSession session) {
        session.removeAttribute(AuthSession.AUTH_CUSTOMER);
        return Collections.singletonMap("message", "Dang xuat thanh cong.");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Object customer = session.getAttribute(AuthSession.AUTH_CUSTOMER);
        if (customer instanceof Customer) {
            return ResponseEntity.ok(customer);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error("Chua dang nhap."));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Map<String, String> error(String message) {
        return Collections.singletonMap("error", message);
    }

    public static class RegisterRequest {
        private String tenKH;
        private String dienThoai;
        private String email;
        private String diaChi;
        private String username;
        private String password;

        public String getTenKH() {
            return tenKH;
        }

        public void setTenKH(String tenKH) {
            this.tenKH = tenKH;
        }

        public String getDienThoai() {
            return dienThoai;
        }

        public void setDienThoai(String dienThoai) {
            this.dienThoai = dienThoai;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getDiaChi() {
            return diaChi;
        }

        public void setDiaChi(String diaChi) {
            this.diaChi = diaChi;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
