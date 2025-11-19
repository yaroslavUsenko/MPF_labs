package sumdu.edu.ua.web.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

/**
 * Spring MVC контролер для управління коментарями.
 * Повертає Thymeleaf шаблони замість JSON.
 */
@Controller
@RequestMapping("/books")
public class CommentsController {

    private final CatalogRepositoryPort bookRepo;
    private final CommentRepositoryPort commentRepo;

    public CommentsController(CatalogRepositoryPort bookRepo, CommentRepositoryPort commentRepo) {
        this.bookRepo = bookRepo;
        this.commentRepo = commentRepo;
    }

    /**
     * Показує коментарі до книги.
     * GET /books/{bookId}/comments
     */
    @GetMapping("/{bookId}/comments")
    public String showBookComments(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(required = false) String author,
            Model model) {

        // Перевірка коректності параметрів
        if (bookId == null || bookId <= 0) {
            return "redirect:/books";
        }
        if (page < 0) page = 0;
        if (size < 1) size = 3;

        // Отримання книги
        Book book = bookRepo.findById(bookId);
        if (book == null) {
            return "redirect:/books";
        }

        // Отримання коментарів із пагінацією
        var result = commentRepo.list(bookId, author, null, new PageRequest(page, size));
        List<Comment> comments = result.getItems();
        int totalPages = (int) Math.ceil((double) result.getTotal() / size);

        // Додавання даних до моделі
        model.addAttribute("book", book);
        model.addAttribute("comments", comments);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("author", author != null ? author : "");
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("total", result.getTotal());
        model.addAttribute("comment", new Comment(0L, bookId, "", "", null));

        return "book-comments";
    }

    /**
     * Обробляє додавання коментаря.
     * POST /books/{bookId}/comments/add
     */
    @PostMapping("/{bookId}/comments/add")
    public String addComment(
            @PathVariable Long bookId,
            @RequestParam String author,
            @RequestParam String text,
            Model model) {

        // Валідація вхідних даних
        if (author == null || author.isBlank() || text == null || text.isBlank()) {
            model.addAttribute("error", "form.error.invalid");
            return "redirect:/books/" + bookId + "/comments";
        }

        try {
            commentRepo.add(bookId, author.trim(), text.trim());
            return "redirect:/books/" + bookId + "/comments";
        } catch (Exception e) {
            model.addAttribute("error", "form.error.savefailed");
            return "redirect:/books/" + bookId + "/comments";
        }
    }

    /**
     * Обробляє видалення коментаря.
     * DELETE /books/{bookId}/comments/{commentId}
     */
    @PostMapping("/{bookId}/comments/{commentId}/delete")
    public String deleteComment(
            @PathVariable Long bookId,
            @PathVariable Long commentId) {

        try {
            commentRepo.delete(bookId, commentId);
            return "redirect:/books/" + bookId + "/comments";
        } catch (Exception e) {
            return "redirect:/books/" + bookId + "/comments";
        }
    }
}
