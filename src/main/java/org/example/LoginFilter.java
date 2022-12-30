package org.example;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

import static org.example.App.cookieName;

public class LoginFilter implements Filter {
    DAO dao;

    public LoginFilter(DAO dao) {
        this.dao = dao;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse) {

            HttpServletRequest req = (HttpServletRequest) servletRequest;
            HttpServletResponse resp = (HttpServletResponse) servletResponse;

            Cookie[] cs = req.getCookies();

            Optional.ofNullable(cs).flatMap(cc -> Arrays.stream(cc).filter(c -> c.getName().equals(cookieName)).findFirst())
                    .ifPresentOrElse(c -> {
                                final String sessionId = c.getValue();

                                try {
                                    if (!dao.doesUserWithSuchSessionIdExist(sessionId)) {
                                        Cookie co = new Cookie(cookieName, "");
                                        co.setMaxAge(0);
                                        resp.addCookie(co);
                                        if (!req.getServletPath().equals("/login")) resp.sendRedirect("/login");
                                    }
                                    else if (req.getServletPath().equals("/login") || req.getServletPath().equals("/sign-up")) resp.sendRedirect("/users");
                                } catch (SQLException | IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } , () -> {
                                try {
                                    if (!req.getServletPath().equals("/login")) resp.sendRedirect("/login");
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );
            filterChain.doFilter(req, resp);
        } else filterChain.doFilter(servletRequest, servletResponse);
    }
}
