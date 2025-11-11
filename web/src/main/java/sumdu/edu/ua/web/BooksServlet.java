package sumdu.edu.ua.web;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

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
            PageRequest pageRequest = new PageRequest(0, 20);

            List<Book> books = bookRepo.search(null, pageRequest).getItems();
            req.setAttribute("books", books);

            req.getRequestDispatcher("/WEB-INF/views/books.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading books", e);
            throw new ServletException("Cannot load books", e);
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
