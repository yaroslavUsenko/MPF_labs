package sumdu.edu.ua.web;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sumdu.edu.ua.config.Beans;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.ErrorResponse;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

public class BooksApiServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(BooksApiServlet.class);

    private final CatalogRepositoryPort bookRepo = Beans.getBookRepo();
    private final ObjectMapper om = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        int page = parseInt(req.getParameter("page"), 0);
        int size = parseInt(req.getParameter("size"), 10);
        String q = req.getParameter("q");
        String sort = req.getParameter("sort");

        try {
            var result = bookRepo.search(q, new PageRequest(page, size, sort));
            resp.setStatus(HttpServletResponse.SC_OK);
            om.writeValue(resp.getWriter(), result);
        } catch (IllegalArgumentException e) {
            log.warn("Bad request in GET /api/books: {}", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ErrorResponse err = new ErrorResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            om.writeValue(resp.getWriter(), err);
        } catch (Exception e) {
            log.error("Server error in GET /api/books", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ErrorResponse err = new ErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error");
            om.writeValue(resp.getWriter(), err);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        try {
            Book book = om.readValue(req.getInputStream(), Book.class);

            if (book.getTitle() == null || book.getTitle().isBlank()
                    || book.getAuthor() == null || book.getAuthor().isBlank()) {
                log.warn("Bad request in POST /api/books: title & author required");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ErrorResponse err = new ErrorResponse(HttpServletResponse.SC_BAD_REQUEST, "title & author required");
                om.writeValue(resp.getWriter(), err);
                return;
            }

            if (book.getPubYear() <= 0) {
                log.warn("Bad request in POST /api/books: invalid pubYear");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ErrorResponse err = new ErrorResponse(HttpServletResponse.SC_BAD_REQUEST, "invalid pubYear");
                om.writeValue(resp.getWriter(), err);
                return;
            }

            Book saved = bookRepo.add(
                    book.getTitle().trim(),
                    book.getAuthor().trim(),
                    book.getPubYear()
            );

            resp.setStatus(HttpServletResponse.SC_CREATED);
            om.writeValue(resp.getWriter(), saved);

        } catch (Exception e) {
            log.error("Server error in POST /api/books", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ErrorResponse err = new ErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error");
            om.writeValue(resp.getWriter(), err);
        }
    }


    private int parseInt(String s, int def) {
        try {
            return (s != null) ? Integer.parseInt(s) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
