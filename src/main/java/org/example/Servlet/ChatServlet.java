package org.example.Servlet;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.DAO;
import org.example.Entities.Message;
import org.example.Entities.Profile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.App.cookieName;

public class ChatServlet extends HttpServlet {
    DAO dao;
    Profile profile;
    String sessionId;

    public ChatServlet(DAO dao) {
        this.dao = dao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        sessionId = Arrays.stream(req.getCookies()).filter(x -> x.getName().equals(cookieName)).findFirst().get().getValue();
        String pathInfo = req.getPathInfo();
        if (pathInfo.startsWith("/")) pathInfo = pathInfo.substring(1);
        List<Message> messages;
        try {
            profile = dao.getProfileById(Integer.parseInt(pathInfo));
            messages = dao.getMessages(sessionId, profile.getProfile_id() + "");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Configuration conf = new Configuration(freemarker.template.Configuration.VERSION_2_3_31);
        conf.setDefaultEncoding(String.valueOf(StandardCharsets.UTF_8));
        conf.setDirectoryForTemplateLoading(new File("static-content"));
        resp.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));

        Map<String, Object> data = new HashMap<>();
        data.put("messages", messages);
        data.put("profileId", profile.getProfile_id());
        data.put("profile", profile);
        try {
            data.put("userEmail", dao.getUsersEmail(sessionId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            conf.getTemplate("chat.ftl").process(data, resp.getWriter());
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            dao.addMessage(new Message(sessionId, profile.getProfile_id(), req.getParameter("message"), true));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        resp.sendRedirect("" + profile.getProfile_id());
    }
}
