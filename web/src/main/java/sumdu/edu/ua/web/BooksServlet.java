package sumdu.edu.ua.web;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

public class BooksServlet extends HttpServlet {

    private final CatalogRepositoryPort bookRepo;

    public BooksServlet(CatalogRepositoryPort bookRepo) {
        this.bookRepo = bookRepo;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get pagination parameters
        int page = 0;
        int size = 3; // 3 books per page
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
        
        String query = req.getParameter("q");
        
        var result = bookRepo.search(query, new PageRequest(page, size));
        List<Book> books = result.getItems();
        int totalPages = (int) Math.ceil((double) result.getTotal() / size);

        req.setAttribute("books", books);
        req.setAttribute("page", page);
        req.setAttribute("size", size);
        req.setAttribute("q", query != null ? query : "");
        req.setAttribute("totalPages", totalPages);
        req.getRequestDispatcher("/WEB-INF/views/books.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");

        String title = req.getParameter("title");
        String author = req.getParameter("author");
        int pubYear;
        try {
            pubYear = parsePubYear(req);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        if (title == null || title.isBlank() || author == null || author.isBlank() || pubYear <= 0 || pubYear > 9999) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "title & author & pubYear required");
            return;
        }

        try {
            bookRepo.add(title, author, pubYear);
            resp.sendRedirect(req.getContextPath() + "/books");
        } catch (Exception e) {
            throw new ServletException("Cannot add book", e);
        }
    }

    private int parsePubYear(HttpServletRequest req) {
        String py = req.getParameter("pubYear");
        if (py != null && !py.isBlank()) {
            try {
                return Integer.parseInt(py.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("invalid pubYear");
            }
        }

        // try to extract year from date input (e.g. 2023-05-27)
        String pd = req.getParameter("pubDate");
        if (pd != null && !pd.isBlank()) {
            // expected format YYYY-MM-DD
            if (pd.length() >= 4) {
                String yearPart = pd.substring(0, 4);
                try {
                    return Integer.parseInt(yearPart);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("invalid pubDate format");
                }
            } else {
                throw new IllegalArgumentException("invalid pubDate format");
            }
        }

        throw new IllegalArgumentException("pubYear or pubDate required");
    }
}
