package sumdu.edu.ua.web;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sumdu.edu.ua.config.Beans;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.core.service.CommentService;

public class CommentsServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(CommentsServlet.class);

    private final CatalogRepositoryPort bookRepo = Beans.getBookRepo();
    private final CommentRepositoryPort commentRepo = Beans.getCommentRepo();
    private final CommentService commentService = Beans.commentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String bookIdStr = req.getParameter("bookId");
        if (bookIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/books");
            return;
        }

        try {
            long bookId = Long.parseLong(bookIdStr);
            Book book = bookRepo.findById(bookId);

            if (book == null) {
                log.warn("Book not found: id={}", bookId);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
                return;
            }

        String author = req.getParameter("author");
        int page = parseInt(req.getParameter("page"), 0);
        int size = parseInt(req.getParameter("size"), 3);
        String since = req.getParameter("since");

        PageRequest pageRequest = new PageRequest(page, size);
        var commentPage = commentRepo.list(bookId, author, null, pageRequest);
        List<Comment> comments = commentPage.getItems();
        long total = commentPage.getTotal();

        req.setAttribute("book", book);
        req.setAttribute("comments", comments);
        req.setAttribute("author", author);
        req.setAttribute("page", page);
        req.setAttribute("size", size);
        req.setAttribute("total", total);
        req.setAttribute("totalPages", (int)((total + size -1) / size));
            req.getRequestDispatcher("/WEB-INF/views/book-comments.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            log.warn("Bad request: invalid bookId format");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid bookId");
        } catch (Exception e) {
            log.error("Error loading book details", e);
            throw new ServletException("Cannot load book details", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        String method = req.getParameter("_method");

        if ("delete".equalsIgnoreCase(method)) {
            try {
                long bookId = Long.parseLong(req.getParameter("bookId"));
                long commentId = Long.parseLong(req.getParameter("commentId"));

                // Отримати коментар, перевірити 24 години
                Comment comment = commentRepo.getById(commentId);
                if (comment == null) {
                    log.warn("Comment not found: id={}", commentId);
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Comment not found");
                    return;
                }

                try {
                    commentService.delete(bookId, commentId, comment.getCreatedAt());
                    resp.sendRedirect(req.getContextPath() + "/comments?bookId=" + bookId);
                } catch (IllegalStateException e) {
                    log.warn("Cannot delete comment: {}", e.getMessage());
                    resp.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
                }
            } catch (NumberFormatException e) {
                log.warn("Bad request: invalid ID format");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID");
            } catch (Exception e) {
                log.error("Error deleting comment", e);
                throw new ServletException("Cannot delete comment", e);
            }
            return;
        }

        // Додавання коментаря
        try {
            long bookId = Long.parseLong(req.getParameter("bookId"));
            String author = req.getParameter("author");
            String text = req.getParameter("text");

            // Перевірити наявність книги
            Book book = bookRepo.findById(bookId);
            if (book == null) {
                log.warn("Book not found: id={}", bookId);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
                return;
            }

            if (author == null || author.isBlank() || text == null || text.isBlank()) {
                log.warn("Bad request: missing author or text");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "author & text required");
                return;
            }

            commentRepo.add(bookId, author.trim(), text.trim());
            log.info("Comment added for book={}, author='{}'", bookId, author);
            resp.sendRedirect(req.getContextPath() + "/comments?bookId=" + bookId);
        } catch (NumberFormatException e) {
            log.warn("Bad request: invalid bookId format");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid bookId");
        } catch (Exception e) {
            log.error("Error saving comment", e);
            throw new ServletException("Cannot save comment", e);
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
