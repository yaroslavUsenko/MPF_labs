package sumdu.edu.ua.core.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;

import sumdu.edu.ua.core.port.CommentRepositoryPort;

@Service
public class CommentService {
    private final CommentRepositoryPort repo;

    public CommentService(CommentRepositoryPort repo) {
        this.repo = repo;
    }

    public void delete(long bookId, long commentId, Instant createdAt) {
        if (Duration.between(createdAt, Instant.now()).toHours() > 24) {
            throw new IllegalStateException("Comment too old to delete");
        }
        repo.delete(bookId, commentId);
    }
}
