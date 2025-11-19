package sumdu.edu.ua.web;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.Javalin;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.core.service.CommentService;
import sumdu.edu.ua.persistence.jdbc.DbInit;
import sumdu.edu.ua.persistence.jdbc.JdbcBookRepository;
import sumdu.edu.ua.persistence.jdbc.JdbcCommentRepository;

public class JavalinBookApp {
    private static final Logger logger = LoggerFactory.getLogger(JavalinBookApp.class);
    private static final int PORT = 8080;

    public static void main(String[] args) {
        // Ініціалізація БД
        DbInit.init();
        
        // Ініціалізація сховищ та сервісів
        CatalogRepositoryPort catalogRepo = new JdbcBookRepository();
        CommentRepositoryPort commentRepo = new JdbcCommentRepository();
        CommentService commentService = new CommentService(commentRepo);

        // Створення Javalin-додатку
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";                    // URL path
                staticFileConfig.directory = "/public";               // Classpath directory
                staticFileConfig.location = io.javalin.http.staticfiles.Location.CLASSPATH;
            });
        }).start(PORT);

        logger.info("Javalin сервер стартував на порту {}", PORT);

        // ==================== MIDDLEWARE ====================
        // Глобальне логування всіх запитів
        app.before(ctx -> {
            logger.info("-> {} {}", ctx.method(), ctx.path());
        });

        // Встановлення типу контенту для всіх відповідей
        app.before(ctx -> {
            ctx.contentType("application/json; charset=utf-8");
        });

        // ==================== МАРШРУТИ - КНИЖКИ ====================

        // GET / - коренева сторінка
        app.get("/", ctx -> {
            ctx.redirect("/index.html");
        });

        // GET /books - отримати список всіх книжок з пошуком та пагінацією
        app.get("/books", ctx -> {
            String query = ctx.queryParam("q") != null ? ctx.queryParam("q") : "";
            int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(0);
            int size = ctx.queryParamAsClass("size", Integer.class).getOrDefault(3);

            PageRequest pageRequest = new PageRequest(page, size);
            Page<Book> result = catalogRepo.search(query, pageRequest);

            ctx.json(result);
            logger.info("Отримано список книжок: запит='{}', сторінка={}", query, page);
        });

        // GET /books/:id - отримати деталі книжки за ID
        app.get("/books/{id}", ctx -> {
            long bookId = ctx.pathParamAsClass("id", Long.class).get();
            Book book = catalogRepo.findById(bookId);

            if (book == null) {
                ctx.status(404);
                ctx.json(new ErrorResponse("Книжка не знайдена", 404));
                logger.warn("Книжка з ID {} не знайдена", bookId);
            } else {
                ctx.json(book);
                logger.info("Отримано деталі книжки ID {}", bookId);
            }
        });

        // POST /books - створити нову книжку
        app.post("/books", ctx -> {
            CreateBookRequest request = ctx.bodyAsClass(CreateBookRequest.class);

            Book newBook = catalogRepo.add(request.title, request.author, request.pubYear);
            ctx.status(201);
            ctx.json(newBook);
            logger.info("Створена нова книжка: ID={}, title='{}'", newBook.getId(), newBook.getTitle());
        });

        // ==================== МАРШРУТИ - КОМЕНТАРІ ====================

        // GET /books/:bookId/comments - отримати коментарі до книжки
        app.get("/books/{bookId}/comments", ctx -> {
            long bookId = ctx.pathParamAsClass("bookId", Long.class).get();
            String author = ctx.queryParam("author");
            String sinceStr = ctx.queryParam("since");
            int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(0);
            int size = ctx.queryParamAsClass("size", Integer.class).getOrDefault(3);

            Instant since = sinceStr != null ? Instant.parse(sinceStr) : null;
            PageRequest pageRequest = new PageRequest(page, size);
            Page<Comment> result = commentRepo.list(bookId, author, since, pageRequest);

            ctx.json(result);
            logger.info("Отримано коментарі до книжки ID {}: сторінка={}", bookId, page);
        });

        // GET /books/:bookId/comments/:commentId - отримати конкретний коментар
        app.get("/books/{bookId}/comments/{commentId}", ctx -> {
            long commentId = ctx.pathParamAsClass("commentId", Long.class).get();
            Comment comment = commentRepo.getById(commentId);

            if (comment == null) {
                ctx.status(404);
                ctx.json(new ErrorResponse("Коментар не знайдений", 404));
                logger.warn("Коментар ID {} не знайдений", commentId);
            } else {
                ctx.json(comment);
                logger.info("Отримано коментар ID {}", commentId);
            }
        });

        // POST /books/:bookId/comments - додати новий коментар
        app.post("/books/{bookId}/comments", ctx -> {
            long bookId = ctx.pathParamAsClass("bookId", Long.class).get();
            CreateCommentRequest request = ctx.bodyAsClass(CreateCommentRequest.class);

            commentRepo.add(bookId, request.author, request.text);
            ctx.status(201);
            ctx.json(new SuccessResponse("Коментар успішно додано"));
            logger.info("Додан коментар до книжки ID {}", bookId);
        });

        // DELETE /books/:bookId/comments/:commentId - видалити коментар
        app.delete("/books/{bookId}/comments/{commentId}", ctx -> {
            long bookId = ctx.pathParamAsClass("bookId", Long.class).get();
            long commentId = ctx.pathParamAsClass("commentId", Long.class).get();

            Comment comment = commentRepo.getById(commentId);
            if (comment == null) {
                ctx.status(404);
                ctx.json(new ErrorResponse("Коментар не знайдений", 404));
                logger.warn("Спроба видалити неіснуючий коментар ID {}", commentId);
                return;
            }

            try {
                commentService.delete(bookId, commentId, comment.getCreatedAt());
                ctx.json(new SuccessResponse("Коментар успішно видалено"));
                logger.info("Видалено коментар ID {}", commentId);
            } catch (IllegalStateException e) {
                ctx.status(400);
                ctx.json(new ErrorResponse(e.getMessage(), 400));
                logger.warn("Помилка при видаленні коментара ID {}: {}", commentId, e.getMessage());
            }
        });

        // ==================== ОБРОБКА ВИНЯТКІВ ====================

        // Обробка валідаційних помилок
        app.exception(IllegalArgumentException.class, (e, ctx) -> {
            logger.error("Помилка валідації: {}", e.getMessage());
            ctx.status(400);
            ctx.json(new ErrorResponse("Помилка валідації: " + e.getMessage(), 400));
        });

        // Обробка помилок сховища
        app.exception(Exception.class, (e, ctx) -> {
            logger.error("Непередбачена помилка", e);
            ctx.status(500);
            ctx.json(new ErrorResponse("Внутрішня помилка сервера", 500));
        });

        // Логування успішних відповідей
        app.after(ctx -> {
            logger.info("<- {} {} [{}]", ctx.method(), ctx.path(), ctx.status());
        });

        logger.info("REST API повністю налаштовано");
    }

    // ==================== ДОПОМІЖНІ КЛАСИ ДЛЯ ЗАПИТІВ ====================

    public static class CreateBookRequest {
        public String title;
        public String author;
        public int pubYear;
    }

    public static class CreateCommentRequest {
        public String author;
        public String text;
    }

    public static class SuccessResponse {
        public String message;
        public long timestamp = System.currentTimeMillis();

        public SuccessResponse(String message) {
            this.message = message;
        }
    }

    public static class ErrorResponse {
        public String error;
        public int status;
        public long timestamp = System.currentTimeMillis();

        public ErrorResponse(String error, int status) {
            this.error = error;
            this.status = status;
        }
    }
}
