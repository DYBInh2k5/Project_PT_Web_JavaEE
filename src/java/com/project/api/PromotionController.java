package com.project.api;

import com.project.dao.PromotionDAO;
import com.project.model.Promotion;
import java.math.BigDecimal;
import java.sql.Date;
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
@RequestMapping("/api/promotions")
public class PromotionController {

    private final PromotionDAO promotionDAO = new PromotionDAO();

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(value = "q", required = false) String q) {
        try {
            List<Promotion> promotions = promotionDAO.findAll(q);
            return ResponseEntity.ok(promotions);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> active() {
        try {
            return ResponseEntity.ok(promotionDAO.findActivePromotions());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") int id) {
        try {
            Promotion promotion = promotionDAO.findById(id);
            if (promotion == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Khong tim thay khuyen mai."));
            }
            return ResponseEntity.ok(promotion);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PromotionRequest request) {
        Promotion promotion = toPromotion(request);
        String validationError = validatePromotion(promotion);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(error(validationError));
        }

        try {
            if (promotionDAO.existsCouponCode(promotion.getMaCoupon(), null)) {
                return ResponseEntity.badRequest().body(error("Ma coupon da ton tai. Vui long nhap ma khac."));
            }
            promotionDAO.insert(promotion);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Collections.singletonMap("message", "Tao khuyen mai thanh cong."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") int id, @RequestBody PromotionRequest request) {
        Promotion promotion = toPromotion(request);
        promotion.setMaKM(id);
        String validationError = validatePromotion(promotion);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(error(validationError));
        }

        try {
            if (promotionDAO.existsCouponCode(promotion.getMaCoupon(), Integer.valueOf(id))) {
                return ResponseEntity.badRequest().body(error("Ma coupon da ton tai. Vui long nhap ma khac."));
            }
            promotionDAO.update(promotion);
            return ResponseEntity.ok(Collections.singletonMap("message", "Cap nhat khuyen mai thanh cong."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id) {
        try {
            promotionDAO.delete(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Xoa khuyen mai thanh cong."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    private Promotion toPromotion(PromotionRequest request) {
        Promotion promotion = new Promotion();
        if (request == null) {
            return promotion;
        }
        promotion.setTenKM(request.getTenKM());
        promotion.setHinhThuc(request.getHinhThuc());
        promotion.setGiaTri(request.getGiaTri());
        promotion.setMaCoupon(request.getMaCoupon());
        promotion.setNgayBD(parseDate(request.getNgayBD()));
        promotion.setNgayKT(parseDate(request.getNgayKT()));
        return promotion;
    }

    private Date parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Date.valueOf(value.trim());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private String validatePromotion(Promotion promotion) {
        if (promotion.getTenKM() == null || promotion.getTenKM().trim().isEmpty()) {
            return "Ten khuyen mai khong duoc de trong.";
        }
        if (promotion.getHinhThuc() == null || promotion.getHinhThuc().trim().isEmpty()) {
            return "Hinh thuc khuyen mai khong duoc de trong.";
        }
        if (promotion.getGiaTri() == null) {
            return "Gia tri khuyen mai khong duoc de trong.";
        }
        if (promotion.getGiaTri().compareTo(BigDecimal.ZERO) < 0) {
            return "Gia tri khuyen mai phai lon hon hoac bang 0.";
        }
        if (("%".equalsIgnoreCase(promotion.getHinhThuc()) || "COUPON".equalsIgnoreCase(promotion.getHinhThuc()))
                && (promotion.getMaCoupon() == null || promotion.getMaCoupon().trim().isEmpty())) {
            return "Hinh thuc nay can ma coupon de ap dung vao hoa don.";
        }
        if (promotion.getNgayBD() != null && promotion.getNgayKT() != null && promotion.getNgayBD().after(promotion.getNgayKT())) {
            return "Ngay bat dau khong duoc lon hon ngay ket thuc.";
        }
        return null;
    }

    private Map<String, String> error(String message) {
        return Collections.singletonMap("error", message);
    }

    public static class PromotionRequest {
        private String tenKM;
        private String hinhThuc;
        private BigDecimal giaTri;
        private String maCoupon;
        private String ngayBD;
        private String ngayKT;

        public String getTenKM() {
            return tenKM;
        }

        public void setTenKM(String tenKM) {
            this.tenKM = tenKM;
        }

        public String getHinhThuc() {
            return hinhThuc;
        }

        public void setHinhThuc(String hinhThuc) {
            this.hinhThuc = hinhThuc;
        }

        public BigDecimal getGiaTri() {
            return giaTri;
        }

        public void setGiaTri(BigDecimal giaTri) {
            this.giaTri = giaTri;
        }

        public String getMaCoupon() {
            return maCoupon;
        }

        public void setMaCoupon(String maCoupon) {
            this.maCoupon = maCoupon;
        }

        public String getNgayBD() {
            return ngayBD;
        }

        public void setNgayBD(String ngayBD) {
            this.ngayBD = ngayBD;
        }

        public String getNgayKT() {
            return ngayKT;
        }

        public void setNgayKT(String ngayKT) {
            this.ngayKT = ngayKT;
        }
    }
}
