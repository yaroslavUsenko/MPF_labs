package sumdu.edu.ua.web.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

/**
 * Spring MVC контролер для управління книгами.
 * Повертає Thymeleaf шаблони замість JSON.
 */
@Controller
@RequestMapping("/books")
public class BooksController {

    private final CatalogRepositoryPort bookRepo;

    public BooksController(CatalogRepositoryPort bookRepo) {
        this.bookRepo = bookRepo;
    }

    /**
     * Показує список всіх книг з пошуком і пагінацією.
     * GET /books
     */
    @GetMapping
    public String showBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(required = false) String q,
            Model model) {

        // Перевірка коректності параметрів
        if (page < 0) page = 0;
        if (size < 1) size = 3;

        // Пошук книг із пагінацією
        var result = bookRepo.search(q, new PageRequest(page, size));
        List<Book> books = result.getItems();
        int totalPages = (int) Math.ceil((double) result.getTotal() / size);

        // Додавання даних до моделі
        model.addAttribute("books", books);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("q", q != null ? q : "");
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("total", result.getTotal());

        return "books";
    }

    /**
     * Показує форму для додавання нової книги.
     * GET /books/add
     */
    @GetMapping("/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book(0L, "", "", 0));
        return "book-form";
    }

    /**
     * Обробляє додавання нової книги через форму.
     * POST /books
     */
    @PostMapping
    public String addBook(
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam int pubYear,
            Model model) {

        // Валідація вхідних даних
        if (title == null || title.isBlank() || author == null || author.isBlank() || 
            pubYear <= 0 || pubYear > 9999) {
            model.addAttribute("error", "form.error.invalid");
            model.addAttribute("book", new Book(0L, title != null ? title : "", author != null ? author : "", pubYear));
            return "book-form";
        }

        try {
            bookRepo.add(title.trim(), author.trim(), pubYear);
            return "redirect:/books";
        } catch (Exception e) {
            model.addAttribute("error", "form.error.savefailed");
            model.addAttribute("book", new Book(0L, title, author, pubYear));
            return "book-form";
        }
    }
}
