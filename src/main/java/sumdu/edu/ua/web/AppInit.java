package sumdu.edu.ua.web;


import sumdu.edu.ua.db.CommentDao;
import jakarta.servlet.*;

public class AppInit implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            new CommentDao().init();
        } catch (Exception e) {
            throw new RuntimeException("DB init failed", e);
        }
    }
}
