package com.linux.cheat.web.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StyleServlet extends HttpServlet {
    private static final String CSS = "" +
            "*{box-sizing:border-box} body{margin:0;font-family:Inter,Arial,sans-serif;background:#1f1f1f;color:#e5e5e5}" +
            ".top{background:#2a2a2a;border-bottom:1px solid #3a3a3a;padding:12px 16px;display:flex;justify-content:space-between;align-items:center;position:sticky;top:0}" +
            ".brand{font-weight:600;color:#f0f0f0}" +
            ".btn{color:#ddd;text-decoration:none;background:#3a3a3a;padding:6px 10px;border-radius:6px;border:1px solid #4a4a4a} .btn:hover{background:#444}" +
            ".wrap{padding:16px;max-width:1100px;margin:0 auto}" +
            ".card{background:#2a2a2a;border:1px solid #3a3a3a;border-radius:10px;padding:12px}" +
            "table{width:100%;border-collapse:collapse;margin-top:8px}" +
            "th,td{padding:8px;border-bottom:1px solid #3a3a3a}" +
            "th{color:#cfcfcf;text-align:left;font-weight:600}" +
            ".sev-INFO{color:#8ab4f8}.sev-MEDIUM{color:#f6d365}.sev-HIGH{color:#ff6b6b}";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/css; charset=UTF-8");
        resp.getWriter().write(CSS);
    }
}
