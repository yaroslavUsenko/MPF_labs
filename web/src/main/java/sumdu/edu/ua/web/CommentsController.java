package sumdu.edu.ua.web;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

@RestController
@RequestMapping("/api/comments")
public class CommentsController {
    private static final Logger log = LoggerFactory.getLogger(CommentsController.class);

    private final CatalogRepositoryPort bookRepo;
    private final CommentRepositoryPort commentRepo;

    public CommentsController(CatalogRepositoryPort bookRepo, CommentRepositoryPort commentRepo) {
        this.bookRepo = bookRepo;
        this.commentRepo = commentRepo;
    }

    @GetMapping
    public ResponseEntity<?> listComments(
            @RequestParam(required = true) long bookId,
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            var result = commentRepo.list(bookId, author, null, new PageRequest(page, size));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("DB error while GET /api/comments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "DB error"));
        }
    }

    @PostMapping
    public ResponseEntity<?> addComment(
            @RequestParam long bookId,
            @RequestBody Map<String, String> body) {
        try {
            String author = body.get("author");
            String text = body.get("text");

            if (author == null || author.isBlank() || text == null || text.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "author & text required"));
            }

            // Verify book exists
            var book = bookRepo.findById(bookId);
            if (book == null) {
                return ResponseEntity.notFound().build();
            }

            commentRepo.add(bookId, author.trim(), text.trim());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("success", "Comment added successfully"));

        } catch (Exception e) {
            log.error("DB error while POST /api/comments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "DB error"));
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @RequestParam long bookId,
            @PathVariable long commentId) {
        try {
            commentRepo.delete(bookId, commentId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("DB error while DELETE /api/comments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "DB error"));
        }
    }
}
