package com.project.web.book;

import com.project.dao.BookDAO;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        } catch (SQLException ex) {
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
