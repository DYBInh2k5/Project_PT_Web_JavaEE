package com.project.web.book;

import com.project.dao.BookDAO;
import com.project.model.Book;
import java.io.IOException;
import java.math.BigDecimal;
// SQLException removed; use generic exception handling after JPA migration
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "BookFormServlet", urlPatterns = {"/books/new", "/books/edit"})
public class BookFormServlet extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();
        boolean isEdit = "/books/edit".equals(servletPath);

        if (isEdit) {
            Integer maSach = parseInteger(request.getParameter("id"));
            if (maSach == null) {
                response.sendRedirect(request.getContextPath() + "/books");
                return;
            }

            try {
                Book book = bookDAO.findById(maSach);
                if (book == null) {
                    response.sendRedirect(request.getContextPath() + "/books");
                    return;
                }
                request.setAttribute("book", book);
                request.setAttribute("isEdit", true);
            } catch (Exception ex) {
                request.setAttribute("errorMessage", "Khong tai duoc du lieu sach: " + ex.getMessage());
                request.setAttribute("isEdit", true);
            }
        } else {
            request.setAttribute("book", new Book());
            request.setAttribute("isEdit", false);
        }

        request.getRequestDispatcher("/books/form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String servletPath = request.getServletPath();
        boolean isEdit = "/books/edit".equals(servletPath);

        Book book = buildBookFromRequest(request);

        if (isEdit) {
            Integer maSach = parseInteger(request.getParameter("id"));
            if (maSach == null) {
                response.sendRedirect(request.getContextPath() + "/books");
                return;
            }
            book.setMaSach(maSach);
        }

        String validationError = validateBook(book);
        if (validationError != null) {
            request.setAttribute("errorMessage", validationError);
            request.setAttribute("book", book);
            request.setAttribute("isEdit", isEdit);
            request.getRequestDispatcher("/books/form.jsp").forward(request, response);
            return;
        }

        try {
            if (isEdit) {
                bookDAO.update(book);
                response.sendRedirect(request.getContextPath() + "/books?msg=updated");
            } else {
                bookDAO.insert(book);
                response.sendRedirect(request.getContextPath() + "/books?msg=created");
            }
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Luu sach that bai: " + ex.getMessage());
            request.setAttribute("book", book);
            request.setAttribute("isEdit", isEdit);
            request.getRequestDispatcher("/books/form.jsp").forward(request, response);
        }
    }

    private Book buildBookFromRequest(HttpServletRequest request) {
        Book book = new Book();
        book.setTenSach(request.getParameter("tenSach"));
        book.setTacGia(request.getParameter("tacGia"));
        book.setTheLoai(request.getParameter("theLoai"));
        book.setDonGia(parseBigDecimal(request.getParameter("donGia")));
        book.setSoLuong(parseInteger(request.getParameter("soLuong")));
        book.setAnhBia(request.getParameter("anhBia"));
        return book;
    }

    private String validateBook(Book book) {
        if (book.getTenSach() == null || book.getTenSach().trim().isEmpty()) {
            return "Ten sach khong duoc de trong.";
        }

        if (book.getDonGia() != null && book.getDonGia().compareTo(BigDecimal.ZERO) < 0) {
            return "Don gia phai lon hon hoac bang 0.";
        }

        if (book.getSoLuong() != null && book.getSoLuong() < 0) {
            return "So luong phai lon hon hoac bang 0.";
        }

        return null;
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

    private BigDecimal parseBigDecimal(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        try {
            return new BigDecimal(raw.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
