package com.project.api;

import com.project.dao.ReportDAO;
import com.project.model.dto.RevenueByDate;
import com.project.model.dto.TopBookReportItem;
import java.sql.Date;
// SQLException removed
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportDAO reportDAO = new ReportDAO();

    @GetMapping("/revenue")
    public ResponseEntity<?> revenue(
            @RequestParam(value = "from", required = false) String fromRaw,
            @RequestParam(value = "to", required = false) String toRaw,
            @RequestParam(value = "topN", required = false, defaultValue = "10") int topN) {

        LocalDate today = LocalDate.now();
        LocalDate from = parseLocalDate(fromRaw);
        LocalDate to = parseLocalDate(toRaw);
        if (from == null) {
            from = today.minusDays(30);
        }
        if (to == null) {
            to = today;
        }

        Date fromDate = Date.valueOf(from);
        Date toDate = Date.valueOf(to);

        try {
            List<RevenueByDate> revenueRows = reportDAO.findRevenueByDate(fromDate, toDate);
            List<TopBookReportItem> topBooks = reportDAO.findTopBooks(fromDate, toDate, topN <= 0 ? 10 : topN);
            Map<String, Object> body = new LinkedHashMap<String, Object>();
            body.put("from", from.toString());
            body.put("to", to.toString());
            body.put("revenueRows", revenueRows);
            body.put("topBooks", topBooks);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            Map<String, Object> body = new LinkedHashMap<String, Object>();
            body.put("from", from.toString());
            body.put("to", to.toString());
            body.put("revenueRows", Collections.emptyList());
            body.put("topBooks", Collections.emptyList());
            body.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    private LocalDate parseLocalDate(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(raw.trim());
        } catch (Exception ex) {
            return null;
        }
    }
}
