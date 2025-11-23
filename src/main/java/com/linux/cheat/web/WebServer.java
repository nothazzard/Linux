package com.linux.cheat.web;

import com.linux.cheat.auth.AuthService;
import com.linux.cheat.web.servlet.IndexServlet;
import com.linux.cheat.web.servlet.LogsServlet;
import com.linux.cheat.web.servlet.ApiLogsServlet;
import com.linux.cheat.web.servlet.StreamServlet;
import com.linux.cheat.web.servlet.StyleServlet;
import com.linux.cheat.web.servlet.ApiStatsServlet;
import com.linux.cheat.web.servlet.LoginServlet;
import com.linux.cheat.web.servlet.LogoutServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class WebServer {
    private final int port;
    private final AuthService authService;
    private Server server;

    public WebServer(int port, AuthService authService) {
        this.port = port;
        this.authService = authService;
    }

    public void start() throws Exception {
        server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        context.setAttribute("auth", authService);

        // Pages
        context.addServlet(new ServletHolder(new LoginServlet()), "/login");
        context.addServlet(new ServletHolder(new LogoutServlet()), "/logout");
        context.addServlet(new ServletHolder(new IndexServlet()), "/");
        context.addServlet(new ServletHolder(new LogsServlet()), "/logs");

        // API
        context.addServlet(new ServletHolder(new ApiLogsServlet()), "/api/logs");
        context.addServlet(new ServletHolder(new StreamServlet()), "/api/stream");
        context.addServlet(new ServletHolder(new ApiStatsServlet()), "/api/stats");

        // Assets
        context.addServlet(new ServletHolder(new StyleServlet()), "/style.css");

        server.setHandler(context);
        server.start();
    }

    public void stop() throws Exception {
        if (server != null) {
            server.stop();
        }
    }
}
