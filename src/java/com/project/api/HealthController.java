package com.project.api;

import com.project.dao.JpaSupport;
import jakarta.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/db-check")
    public ResponseEntity<?> dbCheck() {
        EntityManager em = JpaSupport.createEntityManager();
        try {
            Object value = em.createNativeQuery("SELECT DB_NAME() AS dbName").getSingleResult();
            String dbName = value == null ? "unknown" : String.valueOf(value);

            Map<String, Object> result = new LinkedHashMap<String, Object>();
            result.put("status", "Connection OK");
            result.put("currentDatabase", dbName);
            result.put("expectedDatabase", "QLBanSach");
            result.put("match", "QLBanSach".equalsIgnoreCase(dbName));
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Collections.singletonMap("error", ex.getMessage()));
        } finally {
            em.close();
        }
    }
}
