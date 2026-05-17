package com.project.api;

import com.project.dao.PurchaseDAO;
import com.project.dao.PurchaseDAO.NewPurchaseItem;
import com.project.model.PurchaseReceipt;
import com.project.model.PurchaseReceiptItem;
import java.math.BigDecimal;
// SQLException removed
import java.util.ArrayList;
import java.util.Collections;
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
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseDAO purchaseDAO = new PurchaseDAO();

    @GetMapping
    public ResponseEntity<?> list() {
        try {
            return ResponseEntity.ok(purchaseDAO.findAll());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") int id) {
        try {
            PurchaseReceipt purchase = purchaseDAO.findById(id);
            if (purchase == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Khong tim thay phieu nhap."));
            }
            List<PurchaseReceiptItem> items = purchaseDAO.findItemsByPurchaseId(id);
            Map<String, Object> body = new LinkedHashMap<String, Object>();
            body.put("purchase", purchase);
            body.put("items", items);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PurchaseCreateRequest request) {
        try {
            List<NewPurchaseItem> items = new ArrayList<NewPurchaseItem>();
            if (request != null && request.getItems() != null) {
                for (PurchaseItemRequest row : request.getItems()) {
                    if (row == null || row.getMaSach() == null || row.getSoLuong() == null || row.getDonGia() == null) {
                        continue;
                    }
                    items.add(new NewPurchaseItem(row.getMaSach().intValue(), row.getSoLuong().intValue(), row.getDonGia()));
                }
            }

            int maPN = purchaseDAO.createPurchase(request == null ? null : request.getMaNV(), items);
            return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("maPN", Integer.valueOf(maPN)));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    private Map<String, String> error(String message) {
        return Collections.singletonMap("error", message);
    }

    public static class PurchaseCreateRequest {
        private Integer maNV;
        private List<PurchaseItemRequest> items;

        public Integer getMaNV() {
            return maNV;
        }

        public void setMaNV(Integer maNV) {
            this.maNV = maNV;
        }

        public List<PurchaseItemRequest> getItems() {
            return items;
        }

        public void setItems(List<PurchaseItemRequest> items) {
            this.items = items;
        }
    }

    public static class PurchaseItemRequest {
        private Integer maSach;
        private Integer soLuong;
        private BigDecimal donGia;

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

        public BigDecimal getDonGia() {
            return donGia;
        }

        public void setDonGia(BigDecimal donGia) {
            this.donGia = donGia;
        }
    }
}
