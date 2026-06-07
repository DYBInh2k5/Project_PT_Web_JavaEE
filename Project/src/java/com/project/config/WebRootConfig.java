package com.project.config;

import java.io.File;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * WebRootConfig — Cấu hình để Spring Boot sử dụng thư mục "web" làm document root
 * thay vì thư mục mặc định (src/main/webapp). Điều này cho phép phục vụ file tĩnh
 * (HTML, CSS, JS, hình ảnh) từ thư mục "web" ở cấp độ dự án.
 */
@Configuration
public class WebRootConfig implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        File webRoot = new File("web");
        if (webRoot.isDirectory()) {
            factory.setDocumentRoot(webRoot);
        }
    }
}
