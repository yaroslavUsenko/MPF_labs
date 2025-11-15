package sumdu.edu.ua.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

@Controller
@RequestMapping
public class BooksController {

    private final CatalogRepositoryPort bookRepo;
    private final CommentRepositoryPort commentRepo;

    public BooksController(CatalogRepositoryPort bookRepo, CommentRepositoryPort commentRepo) {
        this.bookRepo = bookRepo;
        this.commentRepo = commentRepo;
    }

    @GetMapping("/books")
    public String getBooks(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            Model model) {
        
        var result = bookRepo.search(q, new PageRequest(page, size));
        List<Book> books = result.getItems();
        int totalPages = (int) Math.ceil((double) result.getTotal() / size);

        model.addAttribute("books", books);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("q", q != null ? q : "");
        model.addAttribute("totalPages", totalPages);

        return "books";
    }

    @GetMapping("/books/{id}")
    public String getBook(@PathVariable long id, Model model) {
        Book book = bookRepo.findById(id);
        model.addAttribute("book", book);
        return "book-comments";
    }

    @GetMapping("/comments")
    public String getComments(
            @RequestParam long bookId,
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            Model model) {
        
        Book book = bookRepo.findById(bookId);
        var result = commentRepo.list(bookId, author, null, new PageRequest(page, size));
        List<Comment> comments = result.getItems();
        int totalPages = (int) Math.ceil((double) result.getTotal() / size);

        model.addAttribute("book", book);
        model.addAttribute("comments", comments);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("author", author != null ? author : "");
        model.addAttribute("totalPages", totalPages);

        return "book-comments";
    }

    @PostMapping("/books")
    public String addBook(
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam(required = false) String pubDate,
            @RequestParam(required = false) String pubYear,
            RedirectAttributes redirectAttributes) {

        if (title == null || title.isBlank() || author == null || author.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "title & author required");
            return "redirect:/books";
        }

        int year;
        try {
            year = parsePubYear(pubYear, pubDate);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/books";
        }

        if (year <= 0 || year > 9999) {
            redirectAttributes.addFlashAttribute("error", "invalid pubYear");
            return "redirect:/books";
        }

        try {
            bookRepo.add(title.trim(), author.trim(), year);
            return "redirect:/books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot add book: " + e.getMessage());
            return "redirect:/books";
        }
    }

    @PostMapping("/comments")
    public String addComment(
            @RequestParam long bookId,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) String _method,
            @RequestParam(required = false) Long commentId,
            RedirectAttributes redirectAttributes) {

        // Видалення коментаря
        if ("delete".equalsIgnoreCase(_method) && commentId != null) {
            try {
                commentRepo.delete(bookId, commentId);
                return "redirect:/comments?bookId=" + bookId;
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Cannot delete comment: " + e.getMessage());
                return "redirect:/comments?bookId=" + bookId;
            }
        }

        // Додавання коментаря
        if (author == null || author.isBlank() || text == null || text.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "author & text required");
            return "redirect:/comments?bookId=" + bookId;
        }

        try {
            commentRepo.add(bookId, author.trim(), text.trim());
            return "redirect:/comments?bookId=" + bookId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot add comment: " + e.getMessage());
            return "redirect:/comments?bookId=" + bookId;
        }
    }

    private int parsePubYear(String pubYear, String pubDate) {
        if (pubYear != null && !pubYear.isBlank()) {
            try {
                return Integer.parseInt(pubYear.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("invalid pubYear");
            }
        }

        if (pubDate != null && !pubDate.isBlank()) {
            if (pubDate.length() >= 4) {
                String yearPart = pubDate.substring(0, 4);
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
