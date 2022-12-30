package org.example;

import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.example.Servlet.*;

import java.util.EnumSet;
public class App {
    public final static String cookieName = "sessionId";
    private static final EnumSet<DispatcherType> ft = EnumSet.of(DispatcherType.REQUEST);

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler();
        handler.addLocaleEncoding("ru", "UTF-8");
        DAO dao = new DAO();
        handler.addServlet(new ServletHolder(new LikePageServlet(dao)), "/users");
        handler.addServlet(new ServletHolder(new StaticServlet("static-content")), "/static/*");
        handler.addServlet(new ServletHolder(new PeopleListServlet(dao)), "/liked");
        handler.addServlet(new ServletHolder(new ChatServlet(dao)), "/messages/*");
        handler.addServlet(new ServletHolder(new LoginServlet(dao)), "/login");
        handler.addServlet(new ServletHolder(new SignUpServlet(dao)), "/sign-up");
        handler.addServlet(LogoutServlet.class, "/logout");
        handler.addServlet(StartServlet.class, "/");

        handler.addFilter(new FilterHolder(new LoginFilter(dao)), "/messages/*", ft);
        handler.addFilter(new FilterHolder(new LoginFilter(dao)), "/liked", ft);
        handler.addFilter(new FilterHolder(new LoginFilter(dao)), "/users", ft);
        handler.addFilter(new FilterHolder(new LoginFilter(dao)), "/login", ft);

        server.setHandler(handler);

        server.start();
        server.join();
    }
}
