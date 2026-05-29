package com.project.api;

import com.project.dao.ReportDAO;
import com.project.model.dto.RevenueByDate;
import com.project.model.dto.TopBookReportItem;
import java.math.BigDecimal;
import java.sql.Date;
// SQLException removed
import java.time.LocalDate;
import java.util.ArrayList;
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
            @RequestParam(value = "date", required = false) String dateRaw,
            @RequestParam(value = "from", required = false) String fromRaw,
            @RequestParam(value = "to", required = false) String toRaw,
            @RequestParam(value = "topN", required = false, defaultValue = "10") int topN) {

        LocalDate today = LocalDate.now();
        LocalDate selectedDate = parseLocalDate(dateRaw);
        LocalDate from = parseLocalDate(fromRaw);
        LocalDate to = parseLocalDate(toRaw);
        if (selectedDate == null) {
            selectedDate = today;
        }
        if (from == null) {
            from = selectedDate.minusDays(6);
        }
        if (to == null) {
            to = selectedDate;
        }

        Date fromDate = Date.valueOf(from);
        Date toDate = Date.valueOf(to);
        Date selectedSqlDate = Date.valueOf(selectedDate);

        try {
            List<RevenueByDate> revenueRows = reportDAO.findRevenueByDate(fromDate, toDate);
            List<TopBookReportItem> topBooks = reportDAO.findTopBooks(fromDate, toDate, topN <= 0 ? 10 : topN);
            BigDecimal dayRevenue = reportDAO.sumRevenueOnDate(selectedSqlDate);
            BigDecimal monthRevenue = reportDAO.sumRevenueForMonth(selectedSqlDate);
            BigDecimal yearRevenue = reportDAO.sumRevenueForYear(selectedSqlDate);
            List<RevenueByDate> chart7Days = reportDAO.findRevenueByDate(Date.valueOf(selectedDate.minusDays(6)), selectedSqlDate);
            Map<String, Object> body = new LinkedHashMap<String, Object>();
            body.put("date", selectedDate.toString());
            body.put("from", from.toString());
            body.put("to", to.toString());
            body.put("dayRevenue", dayRevenue);
            body.put("monthRevenue", monthRevenue);
            body.put("yearRevenue", yearRevenue);
            body.put("chart7Days", chart7Days);
            body.put("revenueRows", revenueRows);
            body.put("topBooks", topBooks);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            Map<String, Object> body = new LinkedHashMap<String, Object>();
            body.put("date", selectedDate.toString());
            body.put("from", from.toString());
            body.put("to", to.toString());
            body.put("dayRevenue", java.math.BigDecimal.ZERO);
            body.put("monthRevenue", java.math.BigDecimal.ZERO);
            body.put("yearRevenue", java.math.BigDecimal.ZERO);
            body.put("chart7Days", Collections.emptyList());
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
