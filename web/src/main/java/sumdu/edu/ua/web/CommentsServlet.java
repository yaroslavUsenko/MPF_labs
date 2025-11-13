package sumdu.edu.ua.web;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

public class CommentsServlet extends HttpServlet {
    private final CatalogRepositoryPort bookRepo;
    private final CommentRepositoryPort commentRepo;

    public CommentsServlet(CatalogRepositoryPort bookRepo, CommentRepositoryPort commentRepo) {
        this.bookRepo = bookRepo;
        this.commentRepo = commentRepo;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String bookIdStr = req.getParameter("bookId");
        if (bookIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/books");
            return;
        }

        long bookId = Long.parseLong(bookIdStr);
        Book book = bookRepo.findById(bookId);

        // Get pagination parameters
        int page = 0;
        int size = 3; // 3 comments per page
        String pageStr = req.getParameter("page");
        String sizeStr = req.getParameter("size");
        
        if (pageStr != null && !pageStr.isBlank()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                page = 0;
            }
        }
        
        if (sizeStr != null && !sizeStr.isBlank()) {
            try {
                size = Integer.parseInt(sizeStr);
            } catch (NumberFormatException e) {
                size = 3;
            }
        }

        String author = req.getParameter("author");
        
        var result = commentRepo.list(bookId, author, null, new PageRequest(page, size));
        List<Comment> comments = result.getItems();
        int totalPages = (int) Math.ceil((double) result.getTotal() / size);

        req.setAttribute("book", book);
        req.setAttribute("comments", comments);
        req.setAttribute("page", page);
        req.setAttribute("size", size);
        req.setAttribute("author", author != null ? author : "");
        req.setAttribute("totalPages", totalPages);
        req.getRequestDispatcher("/WEB-INF/views/book-comments.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        String method = req.getParameter("_method");

        if ("delete".equalsIgnoreCase(method)) {
            long bookId = Long.parseLong(req.getParameter("bookId"));
            long commentId = Long.parseLong(req.getParameter("commentId"));

            try {
                commentRepo.delete(bookId, commentId);
                resp.sendRedirect(req.getContextPath() + "/comments?bookId=" + bookId);
            } catch (Exception e) {
                throw new ServletException("Cannot delete comment", e);
            }
            return;
        }

        long bookId = Long.parseLong(req.getParameter("bookId"));
        String author = req.getParameter("author");
        String text = req.getParameter("text");

        if (author == null || author.isBlank() || text == null || text.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "author & text required");
            return;
        }

        try {
            commentRepo.add(bookId, author.trim(), text.trim());
            resp.sendRedirect(req.getContextPath() + "/comments?bookId=" + bookId);
        } catch (Exception e) {
            throw new ServletException("Cannot save comment", e);
        }
    }
}
