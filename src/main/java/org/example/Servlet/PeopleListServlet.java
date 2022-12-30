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

public class PeopleListServlet extends HttpServlet {

    DAO dao;
    String sessionId;

    public PeopleListServlet(DAO dao) {
        this.dao = dao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        sessionId = Arrays.stream(req.getCookies()).filter(x -> x.getName().equals(cookieName)).findFirst().get().getValue();
        Configuration conf = new Configuration(freemarker.template.Configuration.VERSION_2_3_31);
        conf.setDefaultEncoding(String.valueOf(StandardCharsets.UTF_8));
        conf.setDirectoryForTemplateLoading(new File("static-content"));
        resp.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));

        try {
            List<Profile> profiles = dao.getLikedProfiles(sessionId);
            if (profiles.isEmpty()) {
                List<String> strings = Files.readAllLines(Paths.get("static-content/NoLikedProfiles.html"));
                try (PrintWriter w = resp.getWriter()) {
                    strings.forEach(w::println);
                }
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("profiles", profiles);

                try {
                    data.put("userEmail", dao.getUsersEmail(sessionId));
                    conf.getTemplate("people-list.ftl").process(data, resp.getWriter());
                } catch (SQLException | TemplateException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
