package sumdu.edu.ua.web;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import sumdu.edu.ua.core.port.CatalogRepositoryPort;

@Configuration
public class ServletConfig {
    public ServletConfig() { System.out.println("ServletConfig loaded"); }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * Залишено для сумісності з API.
     * Spring MVC контролери обробляють /books та /comments адреси.
     */
    @Bean
    public ServletRegistrationBean<BooksApiServlet> booksApiServlet(
            CatalogRepositoryPort bookRepo, ObjectMapper objectMapper) {
        return new ServletRegistrationBean<>(new BooksApiServlet(bookRepo, objectMapper), "/api/books/*");
    }
}
