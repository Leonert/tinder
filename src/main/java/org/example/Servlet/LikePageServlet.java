package org.example.Servlet;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.DAO;
import org.example.Entities.Profile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.App.cookieName;

public class LikePageServlet extends HttpServlet {
    DAO dao;
    Profile currentProfile;
    String sessionId;

    public LikePageServlet(DAO dao) {
        this.dao = dao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Configuration conf = new Configuration(freemarker.template.Configuration.VERSION_2_3_31);
        conf.setDirectoryForTemplateLoading(new File("static-content"));
        conf.setDefaultEncoding(String.valueOf(StandardCharsets.UTF_8));
        resp.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));
        sessionId = Arrays.stream(req.getCookies()).filter(x -> x.getName().equals(cookieName)).findFirst().get().getValue();

        try {
            currentProfile = dao.getUnseenProfile(sessionId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (currentProfile == null) {
            List<String> strings = Files.readAllLines(Paths.get("static-content/NoNewProfiles.html"));

            try (PrintWriter w = resp.getWriter()) {
                strings.forEach(w::println);
            }
        } else {
            Map<String, Object> data = new HashMap<>();
            data.put("profile_id", currentProfile.getProfile_id());
            data.put("profile_name", currentProfile.getProfile_name());
            data.put("age", currentProfile.getAge());
            data.put("image", currentProfile.getImage());
            data.put("description", currentProfile.getDescription());
            try {
                data.put("userEmail", dao.getUsersEmail(sessionId));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                conf.getTemplate("like-page.ftl").process(data, resp.getWriter());
            } catch (TemplateException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            try {
                if (req.getParameter("choice").equals("yes")) dao.likeProfile(sessionId, currentProfile.getProfile_id() + "", "true");
                else if (req.getParameter("choice").equals("no")) dao.likeProfile(sessionId, currentProfile.getProfile_id() + "", "false");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        currentProfile = null;
        sessionId = null;
        resp.sendRedirect("users");
    }
}
