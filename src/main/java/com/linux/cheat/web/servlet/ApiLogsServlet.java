package com.linux.cheat.web.servlet;

import com.linux.cheat.logs.CheatLog;
import com.linux.cheat.logs.LogStore;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class ApiLogsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"unauthorized\"}");
            return;
        }
        int limit = 200;
        try { limit = Integer.parseInt(req.getParameter("limit")); } catch (Exception ignored) {}
        if (limit <= 0 || limit > 1000) limit = 200;
        List<CheatLog> all = LogStore.get().all();
        int from = Math.max(0, all.size() - limit);
        List<CheatLog> sub = all.subList(from, all.size());
        StringBuilder json = new StringBuilder();
        json.append("[");
        boolean first = true;
        for (CheatLog l : sub) {
            if (!first) json.append(',');
            first = false;
            json.append(toJson(l));
        }
        json.append("]");
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write(json.toString());
    }

    private String toJson(CheatLog l) {
        return "{" +
                "\"player\":\"" + esc(l.getPlayer()) + "\"," +
                "\"check\":\"" + esc(l.getCheck()) + "\"," +
                "\"severity\":\"" + esc(l.getSeverity()) + "\"," +
                "\"timestamp\":" + l.getTimestamp() + "," +
                "\"info\":\"" + esc(l.getInfo()) + "\"" +
                "}";
    }

    private String esc(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
