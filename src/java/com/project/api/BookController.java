package com.project.api;

import com.project.dao.BookDAO;
import com.project.model.Book;
import java.math.BigDecimal;
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
@RequestMapping("/api/books")
public class BookController {

    private final BookDAO bookDAO = new BookDAO();

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(value = "q", required = false) String q) {
        try {
            List<Book> books = bookDAO.findAll(q);
            return ResponseEntity.ok(books);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(wrapError("Khong tai duoc danh sach sach: " + ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") int id) {
        try {
            Book book = bookDAO.findById(id);
            if (book == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(wrapError("Khong tim thay sach."));
            }
            return ResponseEntity.ok(book);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(wrapError(ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Book book) {
        String validationError = validateBook(book);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(wrapError(validationError));
        }

        try {
            bookDAO.insert(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("message", "Tao sach thanh cong."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(wrapError(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") int id, @RequestBody Book book) {
        book.setMaSach(id);
        String validationError = validateBook(book);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(wrapError(validationError));
        }

        try {
            bookDAO.update(book);
            return ResponseEntity.ok(Collections.singletonMap("message", "Cap nhat sach thanh cong."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(wrapError(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id) {
        try {
            bookDAO.delete(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Xoa sach thanh cong."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(wrapError(ex.getMessage()));
        }
    }

    private String validateBook(Book book) {
        if (book == null) {
            return "Du lieu sach khong hop le.";
        }
        if (book.getTenSach() == null || book.getTenSach().trim().isEmpty()) {
            return "Ten sach khong duoc de trong.";
        }
        if (book.getDonGia() != null && book.getDonGia().compareTo(BigDecimal.ZERO) < 0) {
            return "Don gia phai lon hon hoac bang 0.";
        }
        if (book.getSoLuong() != null && book.getSoLuong().intValue() < 0) {
            return "So luong phai lon hon hoac bang 0.";
        }
        return null;
    }

    private Map<String, String> wrapError(String message) {
        return Collections.singletonMap("error", message);
    }
}
