package com.project.web.mvc;

import com.project.dao.BookDAO;
import com.project.model.Book;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ThymeleafBookController {

    private final BookDAO bookDAO = new BookDAO();

    @GetMapping({"/mvc/books", "/mvc"})
    public String books(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "12") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "newest") String sort,
            Model model) {

        List<Book> books = bookDAO.findAll(q);
        books.sort(Comparator.comparing(Book::getMaSach, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        if ("price_asc".equalsIgnoreCase(sort)) {
            books.sort(Comparator.comparing(Book::getDonGia, Comparator.nullsLast(Comparator.naturalOrder())));
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            books.sort(Comparator.comparing(Book::getDonGia, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        }

        int safeSize = size <= 0 ? 12 : Math.min(size, 60);
        int totalItems = books.size();
        int totalPages = totalItems == 0 ? 0 : (int) Math.ceil(totalItems / (double) safeSize);
        int safePage = page <= 0 ? 1 : Math.min(page, Math.max(totalPages, 1));
        int fromIndex = Math.max(0, (safePage - 1) * safeSize);
        int toIndex = Math.min(totalItems, fromIndex + safeSize);
        List<Book> pageItems = fromIndex >= toIndex ? Collections.<Book>emptyList() : books.subList(fromIndex, toIndex);

        model.addAttribute("books", pageItems);
        model.addAttribute("page", Integer.valueOf(safePage));
        model.addAttribute("size", Integer.valueOf(safeSize));
        model.addAttribute("totalItems", Integer.valueOf(totalItems));
        model.addAttribute("totalPages", Integer.valueOf(totalPages));
        model.addAttribute("sort", sort);
        model.addAttribute("keyword", q == null ? "" : q);
        return "books";
    }
}
