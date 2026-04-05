package com.project.web.filter;

import com.project.db.SqlServerConnection;
import com.project.model.AuthUser;
import com.project.web.auth.AuthSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ActivityLogFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(ActivityLogFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String contextPath = req.getContextPath();
        String path = req.getRequestURI().substring(contextPath.length());

        if (isIgnored(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        AuthUser user = session == null ? null : (AuthUser) session.getAttribute(AuthSession.AUTH_USER);
        long start = System.currentTimeMillis();

        try {
            chain.doFilter(request, response);
        } finally {
            if (user != null) {
                long elapsed = System.currentTimeMillis() - start;
                String activity = String.format("%s %s%s", req.getMethod(), path, req.getQueryString() == null ? "" : ("?" + req.getQueryString()));
                LOGGER.info(String.format(
                        "activity user=%s role=%s method=%s path=%s query=%s elapsedMs=%d",
                        safe(user.getTaiKhoan()),
                        safe(user.getVaiTro()),
                        safe(req.getMethod()),
                        safe(path),
                        safe(req.getQueryString()),
                        elapsed));
                persistActivity(user.getMaNV(), activity);
            }
        }
    }

    @Override
    public void destroy() {
    }

    private boolean isIgnored(String path) {
        return "/".equals(path)
                || "/index.jsp".equals(path)
                || "/index.html".equals(path)
                || "/login".equals(path)
                || "/logout".equals(path)
                || "/db-check".equals(path)
                || path.startsWith("/assets/")
                || path.startsWith("/javax.faces.resource/");
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void persistActivity(String maNV, String activity) {
        if (maNV == null || maNV.trim().isEmpty() || activity == null || activity.trim().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO dbo.LogHoatDong (MaNV, HanhDong, ThoiGian) VALUES (?, ?, ?)";
        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNV.trim());
            ps.setString(2, truncate(activity, 255));
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.FINE, "Khong the ghi LogHoatDong", ex);
        }
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}