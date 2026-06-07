// ===== Lớp khởi động chính của ứng dụng Spring Boot =====
// Đây là entry point — nơi ứng dụng bắt đầu chạy
package com.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

// @SpringBootApplication: đánh dấu đây là ứng dụng Spring Boot, tự động cấu hình
// @ServletComponentScan: quét để tìm các Filter/Servlet theo chuẩn Jakarta EE
@SpringBootApplication
@ServletComponentScan
public class BookstoreApiApplication extends SpringBootServletInitializer {

    // Hàm main — điểm vào của chương trình Java
    public static void main(String[] args) {
        // SpringApplication.run khởi động toàn bộ ứng dụng
        SpringApplication.run(BookstoreApiApplication.class, args);
    }
}
