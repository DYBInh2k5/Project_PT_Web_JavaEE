package com.project.web.auth;

import com.project.dao.AuthDAO;
import com.project.model.AuthUser;
import java.io.IOException;
// SQLException removed; use generic exception handling after JPA migration
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final AuthDAO authDAO = new AuthDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(AuthSession.AUTH_USER) != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (isBlank(username) || isBlank(password)) {
            request.setAttribute("errorMessage", "Vui long nhap day du tai khoan va mat khau.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try {
            AuthUser user = authDAO.login(username.trim(), password);
            if (user == null) {
                request.setAttribute("errorMessage", "Sai tai khoan hoac mat khau.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            HttpSession session = request.getSession(true);
            session.setAttribute(AuthSession.AUTH_USER, user);
            session.setMaxInactiveInterval(30 * 60);
            response.sendRedirect(request.getContextPath() + "/dashboard");

        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Khong the dang nhap: " + ex.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
