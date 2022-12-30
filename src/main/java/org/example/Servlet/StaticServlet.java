package org.example.Servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaticServlet extends HttpServlet {
    private final String osPrefix;

    public StaticServlet(String osPrefix) {
        this.osPrefix = osPrefix;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // localhost/web/*
        String pathInfo = req.getPathInfo();
        if (pathInfo.startsWith("/")) pathInfo = pathInfo.substring(1);

        Path file = Path.of(osPrefix, pathInfo);

        try (ServletOutputStream os = resp.getOutputStream()) {
            Files.copy(file, os);
        }
    }
}
