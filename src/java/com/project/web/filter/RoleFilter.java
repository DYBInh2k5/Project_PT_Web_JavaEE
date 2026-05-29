package com.project.web.filter;

import com.project.model.AuthUser;
import com.project.web.auth.AuthSession;
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

public class RoleFilter implements Filter {

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

        if (!requiresAdmin(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(contextPath + "/login");
            return;
        }

        Object authObj = session.getAttribute(AuthSession.AUTH_USER);
        if (!(authObj instanceof AuthUser)) {
            resp.sendRedirect(contextPath + "/login");
            return;
        }

        AuthUser user = (AuthUser) authObj;
        if (!isAdmin(user.getVaiTro())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    private boolean requiresAdmin(String path) {
        return "/books/delete".equals(path)
                || "/customers/delete".equals(path)
                || "/promotions/new".equals(path)
                || "/promotions/edit".equals(path)
                || "/promotions/delete".equals(path)
                || "/api/books".equals(path)
                || path.startsWith("/api/books/")
                || "/api/customers".equals(path)
                || path.startsWith("/api/customers/")
                || "/api/invoices".equals(path)
                || path.startsWith("/api/invoices/")
                || "/api/promotions".equals(path)
                || path.startsWith("/api/promotions/")
                || "/api/purchases".equals(path)
                || path.startsWith("/api/purchases/")
                || "/api/returns".equals(path)
                || path.startsWith("/api/returns/")
                || "/api/dashboard".equals(path)
                || "/api/reports".equals(path)
                || path.startsWith("/api/reports/")
                || path.startsWith("/reports/");
    }

    private boolean isAdmin(String role) {
        return role != null && "admin".equalsIgnoreCase(role.trim());
    }
}
