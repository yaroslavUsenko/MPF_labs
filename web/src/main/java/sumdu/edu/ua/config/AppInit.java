package sumdu.edu.ua.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class AppInit implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent e) {
        Beans.init();
        System.out.print("\nApp started at: http://localhost:8080/books\n");
    }
}
