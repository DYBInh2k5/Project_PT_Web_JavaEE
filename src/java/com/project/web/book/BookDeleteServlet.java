package com.project.web.book;

import com.project.dao.BookDAO;
import java.io.IOException;
// SQLException removed; use generic exception handling after JPA migration
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "BookDeleteServlet", urlPatterns = {"/books/delete"})
public class BookDeleteServlet extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idRaw = request.getParameter("id");
        Integer maSach = parseInteger(idRaw);

        if (maSach == null) {
            response.sendRedirect(request.getContextPath() + "/books?msg=invalid");
            return;
        }

        try {
            bookDAO.delete(maSach);
            response.sendRedirect(request.getContextPath() + "/books?msg=deleted");
        } catch (Exception ex) {
            response.sendRedirect(request.getContextPath() + "/books?msg=delete_failed");
        }
    }

    private Integer parseInteger(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.valueOf(raw.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
