package sumdu.edu.ua.web.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * Конфігурація Spring MVC для підтримки локалізації (i18n).
 * Дозволяє перемикати мову через параметр ?lang=uk або ?lang=en.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * LocaleResolver визначає поточну локаль.
     * Використовує SessionLocaleResolver для збереження мови в сесії.
     * За замовчуванням використовується українська мова (uk).
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(new Locale("uk"));
        return resolver;
    }

    /**
     * LocaleChangeInterceptor перехоплює запити з параметром lang.
     * Наприклад: http://localhost:8080/books?lang=en
     * Змінює поточну локаль на основі параметра.
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    /**
     * Реєструємо перехоплювач для всіх URL-адрес.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
