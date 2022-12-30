package org.example.Servlet;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.DAO;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.example.App.cookieName;

public class SignUpServlet extends HttpServlet {
    DAO dao;
    boolean alreadyExist;

    public SignUpServlet(DAO dao) {
        this.dao = dao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Configuration conf = new Configuration(freemarker.template.Configuration.VERSION_2_3_31);
        conf.setDefaultEncoding(String.valueOf(StandardCharsets.UTF_8));
        conf.setDirectoryForTemplateLoading(new File("static-content"));
        resp.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));

        Map<String, Object> data = new HashMap<>();
        data.put("alreadyExist", alreadyExist);
        alreadyExist = false;

        try {
            conf.getTemplate("sign-up.ftl").process(data, resp.getWriter());
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sessionId = "";
        try {
            alreadyExist = dao.doesUserWithSuchEmailAlreadyExist(req.getParameter("email"));
            if (alreadyExist) {
                resp.sendRedirect("sign-up");
            }
            dao.signUp(req.getParameter("email"), req.getParameter("password"));
            sessionId = dao.login(req.getParameter("email"), req.getParameter("password"));
            resp.addCookie(new Cookie(cookieName, sessionId));
            resp.sendRedirect("users");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
