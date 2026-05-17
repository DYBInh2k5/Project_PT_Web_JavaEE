package com.project.api;

import com.project.dao.ReturnDAO;
import com.project.dao.ReturnDAO.NewReturnItem;
import com.project.model.InvoiceItem;
import com.project.model.ReturnItem;
import com.project.model.ReturnTransaction;
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
@RequestMapping("/api/returns")
public class ReturnController {

    private final ReturnDAO returnDAO = new ReturnDAO();

    @GetMapping
    public ResponseEntity<?> list() {
        try {
            List<ReturnTransaction> returns = returnDAO.findAll();
            return ResponseEntity.ok(returns);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") int id) {
        try {
            ReturnTransaction tx = returnDAO.findById(id);
            if (tx == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Khong tim thay phieu doi tra."));
            }
            List<ReturnItem> items = returnDAO.findItemsByReturnId(id);
            Map<String, Object> body = new LinkedHashMap<String, Object>();
            body.put("transaction", tx);
            body.put("items", items);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @GetMapping("/invoice/{invoiceId}/items")
    public ResponseEntity<?> invoiceItems(@PathVariable("invoiceId") int invoiceId) {
        try {
            List<InvoiceItem> items = returnDAO.findInvoiceItems(invoiceId);
            return ResponseEntity.ok(items);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ReturnCreateRequest request) {
        try {
            if (request == null || request.getMaHD() == null) {
                return ResponseEntity.badRequest().body(error("Vui long chon hoa don can doi tra."));
            }

            List<NewReturnItem> items = new ArrayList<NewReturnItem>();
            if (request.getItems() != null) {
                for (ReturnItemRequest row : request.getItems()) {
                    if (row == null || row.getMaSach() == null || row.getSoLuong() == null) {
                        continue;
                    }
                    items.add(new NewReturnItem(row.getMaSach().intValue(), row.getSoLuong().intValue()));
                }
            }

            int maDT = returnDAO.createReturn(
                    request.getMaHD().intValue(),
                    request.getLyDo(),
                    request.getGhiChu(),
                    request.getKieuXuLy() == null ? 1 : request.getKieuXuLy().intValue(),
                    items);

            return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("maDT", Integer.valueOf(maDT)));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    private Map<String, String> error(String message) {
        return Collections.singletonMap("error", message);
    }

    public static class ReturnCreateRequest {
        private Integer maHD;
        private String lyDo;
        private String ghiChu;
        private Integer kieuXuLy;
        private List<ReturnItemRequest> items;

        public Integer getMaHD() {
            return maHD;
        }

        public void setMaHD(Integer maHD) {
            this.maHD = maHD;
        }

        public String getLyDo() {
            return lyDo;
        }

        public void setLyDo(String lyDo) {
            this.lyDo = lyDo;
        }

        public String getGhiChu() {
            return ghiChu;
        }

        public void setGhiChu(String ghiChu) {
            this.ghiChu = ghiChu;
        }

        public Integer getKieuXuLy() {
            return kieuXuLy;
        }

        public void setKieuXuLy(Integer kieuXuLy) {
            this.kieuXuLy = kieuXuLy;
        }

        public List<ReturnItemRequest> getItems() {
            return items;
        }

        public void setItems(List<ReturnItemRequest> items) {
            this.items = items;
        }
    }

    public static class ReturnItemRequest {
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
}
