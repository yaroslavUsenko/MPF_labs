package sumdu.edu.ua.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import sumdu.edu.ua.db.CommentDao;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.annotation.MultipartConfig;
@MultipartConfig
public class CommentsServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(CommentsServlet.class);

    private final CommentDao dao = new CommentDao();
    private final ObjectMapper om = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            om.writeValue(resp.getWriter(), dao.list());
        } catch (Exception e) {
            log.error("DB error while GET /comments", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        String author = req.getParameter("author");
        String text   = req.getParameter("text");

        if (author == null || author.isBlank() || text == null || text.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "author & text required");
            return;
        }
        if (author.length() > 64 || text.length() > 1000) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "too long");
            return;
        }

        try {
            dao.add(author.trim(), text.trim());
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            log.error("DB error while POST /comments", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error");
        }
    }
}
