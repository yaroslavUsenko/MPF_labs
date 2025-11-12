package sumdu.edu.ua.web;

import java.io.IOException;
import java.util.List;
import sumdu.edu.ua.core.domain.Page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sumdu.edu.ua.config.Beans;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

public class BooksServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(BooksServlet.class);

    private final CatalogRepositoryPort bookRepo = Beans.getBookRepo();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        try {
            String q = req.getParameter("q");
            int page = parseInt(req.getParameter("page"), 0);
            int size = parseInt(req.getParameter("size"), 3);
            String sort = req.getParameter("sort");

            PageRequest pageRequest = new PageRequest(page, size, sort);
            Page<Book> result = bookRepo.search(q, pageRequest);

            List<Book> books = result.getItems();
            long total = result.getTotal();

            req.setAttribute("books", books);
            req.setAttribute("q", q);
            req.setAttribute("page", page);
            req.setAttribute("size", size);
            req.setAttribute("sort", sort);
            req.setAttribute("total", total);
            int totalPages = (int) ((total + size - 1) / size);
            req.setAttribute("totalPages", totalPages);

            req.getRequestDispatcher("/WEB-INF/views/books.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading books", e);
            throw new ServletException("Cannot load books", e);
        }
    }

    private int parseInt(String s, int def) {
        try {
            return (s != null) ? Integer.parseInt(s) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        String title = req.getParameter("title");
        String author = req.getParameter("author");
        String pubDateStr = req.getParameter("pubDate");

        if (title == null || title.isBlank() || author == null || author.isBlank() || pubDateStr == null || pubDateStr.isBlank()) {
            log.warn("Bad request: missing required fields");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "title & author & pubDate required");
            return;
        }

        try {
            // Витягуємо рік з дати (формат: YYYY-MM-DD)
            int pubYear = Integer.parseInt(pubDateStr.substring(0, 4));
            if (pubYear <= 0 || pubYear > 9999) {
                log.warn("Bad request: invalid pubYear = {}", pubYear);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid pubYear");
                return;
            }

            bookRepo.add(title, author, pubYear);
            resp.sendRedirect(req.getContextPath() + "/books");
        } catch (NumberFormatException e) {
            log.warn("Bad request: invalid date format: {}", pubDateStr);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "date must be in format YYYY-MM-DD");
        } catch (Exception e) {
            log.error("Error adding book", e);
            throw new ServletException("Cannot add book", e);
        }
    }
}
