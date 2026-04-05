package com.project.web.filter;

import com.project.model.AuthUser;
import com.project.web.auth.AuthSession;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
                || path.startsWith("/reports/");
    }

    private boolean isAdmin(String role) {
        return role != null && "admin".equalsIgnoreCase(role.trim());
    }
}
