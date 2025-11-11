package sumdu.edu.ua.core.port;

import java.time.Instant;

import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;

public interface  CommentRepositoryPort {
    void add(long bookId, String author, String text);
    Page<Comment> list(long bookId, String author, Instant since, PageRequest request);
    Comment getById(long commentId);
    void delete(long bookId, long commentId);
}
