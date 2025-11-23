package com.linux.cheat.web.servlet;

import com.linux.cheat.logs.CheatLog;
import com.linux.cheat.logs.LogStore;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class LogsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("/login");
            return;
        }
        resp.setContentType("text/html; charset=UTF-8");
        List<CheatLog> logs = LogStore.get().all();
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Linux AntiCheat - Logs</title><link rel='stylesheet' href='/style.css'/></head><body>");
        html.append("<div class='top'><div class='brand'>Linux AntiCheat</div><div><a class='btn' href='/'>Dashboard</a> <a class='btn' href='/logout'>Sair</a></div></div>");
        html.append("<div class='wrap'><div class='card'>");
        html.append("<h3 style='margin:6px 0'>Logs</h3>");
        html.append("<p><a href='/logout'>Sair</a></p>");
        html.append("<table id='logs'>");
        html.append("<tr><th>Timestamp</th><th>Player</th><th>Check</th><th>Severidade</th><th>Info</th></tr>");
        for (CheatLog l : logs) {
            html.append("<tr>")
                .append("<td>").append(l.getTimestamp()).append("</td>")
                .append("<td>").append(escape(l.getPlayer())).append("</td>")
                .append("<td>").append(escape(l.getCheck())).append("</td>")
                .append("<td>").append(escape(l.getSeverity())).append("</td>")
                .append("<td>").append(escape(l.getInfo())).append("</td>")
                .append("</tr>");
        }
        html.append("</table></div></div></body></html>");
        resp.getWriter().write(html.toString());
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}
