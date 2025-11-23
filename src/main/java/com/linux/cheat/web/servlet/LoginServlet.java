package com.linux.cheat.web.servlet;

import com.linux.cheat.auth.AuthService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        String html = "<html><head><title>Login</title></head><body>" +
                "<h1>Login</h1>" +
                "<form method='post'>" +
                "Nickname: <input name='nick'/> <br/>" +
                "Senha: <input type='password' name='pass'/> <br/>" +
                "<button type='submit'>Entrar</button>" +
                "</form>" +
                "</body></html>";
        resp.getWriter().write(html);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AuthService auth = (AuthService) req.getServletContext().getAttribute("auth");
        String nick = req.getParameter("nick");
        String pass = req.getParameter("pass");
        if (auth != null && auth.isStaff(nick) && auth.verifyPassword(pass)) {
            HttpSession session = req.getSession(true);
            session.setAttribute("user", nick);
            resp.sendRedirect("/logs");
        } else {
            resp.sendRedirect("/login");
        }
    }
}
