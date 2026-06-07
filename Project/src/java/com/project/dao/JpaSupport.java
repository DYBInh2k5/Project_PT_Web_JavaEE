// ===== Lớp hỗ trợ JPA (JpaSupport) — cung cấp EntityManager cho toàn bộ DAO =====
// Singleton Pattern: chỉ tạo một EntityManagerFactory duy nhất cho cả ứng dụng
package com.project.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JpaSupport {

    // persistence.xml phải có persistence-unit tên "bookstorePU"
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY =
            Persistence.createEntityManagerFactory("bookstorePU");

    // private constructor — không cho khởi tạo instance
    private JpaSupport() {
    }

    // Mỗi lần gọi tạo một EntityManager mới (không thread-safe, cần đóng sau khi dùng)
    public static EntityManager createEntityManager() {
        return ENTITY_MANAGER_FACTORY.createEntityManager();
    }
}