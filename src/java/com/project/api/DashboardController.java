package com.project.api;

import com.project.dao.DashboardDAO;
import com.project.model.dto.DashboardStats;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardDAO dashboardDAO = new DashboardDAO();

    @GetMapping
    public ResponseEntity<?> getDashboard() {
        try {
            DashboardStats stats = dashboardDAO.loadStats();
            return ResponseEntity.ok(stats);
            } catch (Exception ex) {
            DashboardStats empty = new DashboardStats();
            empty.setRevenue(BigDecimal.ZERO);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(wrap("Khong tai duoc dashboard: " + ex.getMessage(), empty));
        }
    }

    private Map<String, Object> wrap(String message, Object data) {
        Map<String, Object> body = new java.util.LinkedHashMap<String, Object>();
        body.put("error", message);
        body.put("data", data);
        return body;
    }
}
