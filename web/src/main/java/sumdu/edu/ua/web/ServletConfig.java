package sumdu.edu.ua.web;

import org.springframework.context.annotation.Configuration;

/**
 * Конфігурація для Spring MVC контролерів.
 * Сервлети замінені на контролери (@Controller, @RestController),
 * тому реєстрація сервлетів більше не потрібна.
 * 
 * @deprecated Use WebConfig instead for Spring MVC configuration
 */
@Configuration
@Deprecated(since = "2.0", forRemoval = true)
public class ServletConfig {
    public ServletConfig() { 
        System.out.println("ServletConfig loaded (deprecated)"); 
    }
}
