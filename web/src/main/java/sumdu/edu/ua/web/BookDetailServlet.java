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
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.ErrorResponse;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

public class BookDetailServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(BookDetailServlet.class);

    private final CatalogRepositoryPort bookRepo = Beans.getBookRepo();
    private final CommentRepositoryPort commentRepo = Beans.getCommentRepo();
    private final ObjectMapper om = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            log.warn("Bad request: missing book ID");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ErrorResponse err = new ErrorResponse(HttpServletResponse.SC_BAD_REQUEST, "Missing book ID");
            om.writeValue(resp.getWriter(), err);
            return;
        }

        try {
            long bookId = Long.parseLong(pathInfo.substring(1));
            Book book = bookRepo.findById(bookId);

            if (book == null) {
                log.warn("Book not found: id={}", bookId);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                ErrorResponse err = new ErrorResponse(HttpServletResponse.SC_NOT_FOUND, "Book not found");
                om.writeValue(resp.getWriter(), err);
                return;
            }

            // Повернути інформацію про книгу з коментарями
            var bookDetail = new BookDetailDTO(book);
            var comments = commentRepo.list(bookId, null, null, new PageRequest(0, 20));
            bookDetail.setComments(comments.getItems());

            resp.setStatus(HttpServletResponse.SC_OK);
            om.writeValue(resp.getWriter(), bookDetail);

        } catch (NumberFormatException e) {
            log.warn("Bad request: invalid book ID format");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ErrorResponse err = new ErrorResponse(HttpServletResponse.SC_BAD_REQUEST, "Invalid book ID");
            om.writeValue(resp.getWriter(), err);
        } catch (Exception e) {
            log.error("Server error in GET /api/books/{id}", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ErrorResponse err = new ErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
            om.writeValue(resp.getWriter(), err);
        }
    }

    // DTO для відповіді з книгою та коментарями
    public static class BookDetailDTO {
        public Book book;
        public java.util.List<Comment> comments;

        public BookDetailDTO(Book book) {
            this.book = book;
            this.comments = new java.util.ArrayList<>();
        }

        public Book getBook() {
            return book;
        }

        public java.util.List<Comment> getComments() {
            return comments;
        }

        public void setComments(java.util.List<Comment> comments) {
            this.comments = comments;
        }
    }
}
