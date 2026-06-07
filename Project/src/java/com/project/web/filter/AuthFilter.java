package com.project.web.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * AuthFilter — Bộ lọc bảo vệ route /admin/.
 * Chỉ cho phép những người đã đăng nhập (có STORE_ADMIN trong session) mới được truy cập
 * các đường dẫn dành cho admin. Các đường dẫn công khai (public) được bỏ qua qua kiểm tra.
 */
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String contextPath = req.getContextPath();
        String uri = req.getRequestURI();
        String path = uri.substring(contextPath.length());

        // Nếu là đường dẫn công khai, cho phép đi qua mà không cần kiểm tra
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Kiểm tra đăng nhập: cần session có attribute STORE_ADMIN
        HttpSession session = req.getSession(false);
        boolean loggedIn = session != null && session.getAttribute("STORE_ADMIN") != null;

        if (loggedIn) {
            chain.doFilter(request, response);
        } else {
            // Chưa đăng nhập -> chuyển hướng đến trang login admin
            resp.sendRedirect(contextPath + "/admin/login");
        }
    }

    @Override
    public void destroy() {
    }

    /**
     * Xác định các đường dẫn công khai (không cần đăng nhập):
     * - / (trang chủ)
     * - /store/** (cửa hàng, giỏ hàng, đăng nhập khách hàng, ...)
     * - /assets/** (file tĩnh: CSS, JS, hình ảnh)
     * - /admin/login (trang đăng nhập admin)
     */
    private boolean isPublicPath(String path) {
        return "/".equals(path)
                || path.startsWith("/store/")
                || path.equals("/store")
                || path.startsWith("/assets/")
                || path.startsWith("/admin/login")
                || path.equals("/admin/login");
    }
}
