package com.project.web.filter;

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

        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        boolean loggedIn = session != null && session.getAttribute(AuthSession.AUTH_USER) != null;

        if (loggedIn) {
            chain.doFilter(request, response);
        } else {
            resp.sendRedirect(contextPath + "/login");
        }
    }

    @Override
    public void destroy() {
    }

    private boolean isPublicPath(String path) {
        return "/".equals(path)
                || "/index.jsp".equals(path)
                || "/index.html".equals(path)
                || "/login".equals(path)
                || "/logout".equals(path)
                || "/db-check".equals(path)
                || "/shop".equals(path)
                || path.startsWith("/shop/")
                || path.startsWith("/assets/")
                || path.startsWith("/javax.faces.resource/");
    }
}
