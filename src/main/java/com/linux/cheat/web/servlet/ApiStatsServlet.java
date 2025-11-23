package com.linux.cheat.web.servlet;

import com.linux.cheat.logs.CheatLog;
import com.linux.cheat.logs.LogStore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class ApiStatsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"unauthorized\"}");
            return;
        }
        List<CheatLog> all = LogStore.get().all();
        long now = System.currentTimeMillis();
        int total = all.size();
        int lastHour = 0;
        int last5m = 0;
        int info = 0, medium = 0, high = 0;
        for (CheatLog l : all) {
            if (now - l.getTimestamp() <= 3600_000) lastHour++;
            if (now - l.getTimestamp() <= 300_000) last5m++;
            String sev = l.getSeverity();
            if ("HIGH".equalsIgnoreCase(sev)) high++; else if ("MEDIUM".equalsIgnoreCase(sev)) medium++; else info++;
        }
        int staffOnline = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp() || p.hasPermission("linuxac.notify")) staffOnline++;
        }
        String json = "{"+
                "\"total\":"+total+","+
                "\"lastHour\":"+lastHour+","+
                "\"last5m\":"+last5m+","+
                "\"staffOnline\":"+staffOnline+","+
                "\"sev\":{\"INFO\":"+info+",\"MEDIUM\":"+medium+",\"HIGH\":"+high+"}"+
                "}";
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write(json);
    }
}
