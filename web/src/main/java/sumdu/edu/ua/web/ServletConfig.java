package sumdu.edu.ua.web;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

@Configuration
public class ServletConfig {
    public ServletConfig() { System.out.println("ServletConfig loaded"); }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ServletRegistrationBean<BooksServlet> booksServlet(CatalogRepositoryPort bookRepo) {
        return new ServletRegistrationBean<>(new BooksServlet(bookRepo), "/books/*");
    }

    @Bean
    public ServletRegistrationBean<CommentsServlet> commentsServlet(
            CatalogRepositoryPort bookRepo, CommentRepositoryPort commentRepo) {
        return new ServletRegistrationBean<>(new CommentsServlet(bookRepo, commentRepo), "/comments/*");
    }

    @Bean
    public ServletRegistrationBean<BooksApiServlet> booksApiServlet(
            CatalogRepositoryPort bookRepo, ObjectMapper objectMapper) {
        return new ServletRegistrationBean<>(new BooksApiServlet(bookRepo, objectMapper), "/api/books/*");
    }
}
