package com.project.config;

import com.project.web.filter.AuthFilter;
import com.project.web.filter.EncodingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ServletFilterConfig — Cấu hình Spring Boot để đăng ký các bộ lọc (filter) tùy chỉnh.
 * EncodingFilter được đặt thứ tự 1 (chạy trước) để đảm bảo mã hóa UTF-8,
 * AuthFilter được đặt thứ tự 2 để kiểm tra quyền truy cập admin.
 */
@Configuration
public class ServletFilterConfig {

    @Bean
    public FilterRegistrationBean<EncodingFilter> encodingFilterRegistration() {
        FilterRegistrationBean<EncodingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new EncodingFilter());
        registration.addUrlPatterns("/*");
        registration.setName("EncodingFilter");
        registration.setOrder(1); // Chạy trước: thiết lập encoding trước
        return registration;
    }

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistration() {
        FilterRegistrationBean<AuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AuthFilter());
        registration.addUrlPatterns("/*");
        registration.setName("AuthFilter");
        registration.setOrder(2); // Chạy sau EncodingFilter
        return registration;
    }
}
