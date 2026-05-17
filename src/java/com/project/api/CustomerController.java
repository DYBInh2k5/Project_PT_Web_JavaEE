package com.project.api;

import com.project.dao.CustomerDAO;
import com.project.model.Customer;
// SQLException removed
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerDAO customerDAO = new CustomerDAO();

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(value = "q", required = false) String q) {
        try {
            List<Customer> customers = customerDAO.findAll(q);
            return ResponseEntity.ok(customers);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") int id) {
        try {
            Customer customer = customerDAO.findById(id);
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Khong tim thay khach hang."));
            }
            return ResponseEntity.ok(customer);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Customer customer) {
        if (customer == null || isBlank(customer.getTenKH())) {
            return ResponseEntity.badRequest().body(error("Ten khach hang khong duoc de trong."));
        }
        try {
            customerDAO.insert(customer);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Collections.singletonMap("message", "Tao khach hang thanh cong."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") int id, @RequestBody Customer customer) {
        if (customer == null || isBlank(customer.getTenKH())) {
            return ResponseEntity.badRequest().body(error("Ten khach hang khong duoc de trong."));
        }
        customer.setMaKH(id);
        try {
            customerDAO.update(customer);
            return ResponseEntity.ok(Collections.singletonMap("message", "Cap nhat khach hang thanh cong."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id) {
        try {
            customerDAO.delete(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Xoa khach hang thanh cong."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    private Map<String, String> error(String message) {
        return Collections.singletonMap("error", message);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
