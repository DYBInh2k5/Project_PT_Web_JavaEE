package com.project.web.book;

import com.project.dao.BookDAO;
import com.project.model.Book;
import java.io.IOException;
// SQLException removed; use generic exception handling after JPA migration
import java.util.Collections;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "BookListServlet", urlPatterns = {"/books"})
public class BookListServlet extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String q = request.getParameter("q");
        List<Book> books;

        try {
            books = bookDAO.findAll(q);
        } catch (Exception ex) {
            books = Collections.emptyList();
            request.setAttribute("errorMessage", "Khong tai duoc danh sach sach: " + ex.getMessage());
        }

        request.setAttribute("books", books);
        request.setAttribute("q", q == null ? "" : q);
        request.getRequestDispatcher("/books/list.jsp").forward(request, response);
    }
}
