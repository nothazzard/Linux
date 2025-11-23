package com.linux.cheat.web.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class IndexServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("/login");
            return;
        }
        resp.setContentType("text/html; charset=UTF-8");
        String html = "" +
                "<html><head><title>Linux AC</title>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1'/>" +
                "<link rel='stylesheet' href='/style.css'/>" +
                "</head><body>" +
                "<div class='top'><div class='brand'>Linux AntiCheat</div><div><a class='btn' href='/logout'>Sair</a></div></div>" +
                "<div class='wrap'>" +
                "  <div class='card' style='margin-bottom:12px'>" +
                "    <div style='display:flex;gap:12px;flex-wrap:wrap'>" +
                "      <div class='card' style='flex:1'><div>Total Logs</div><div id='s-total' style='font-size:22px;font-weight:700'>0</div></div>" +
                "      <div class='card' style='flex:1'><div>Última hora</div><div id='s-hour' style='font-size:22px;font-weight:700'>0</div></div>" +
                "      <div class='card' style='flex:1'><div>Últimos 5 min</div><div id='s-5m' style='font-size:22px;font-weight:700'>0</div></div>" +
                "      <div class='card' style='flex:1'><div>Severidade</div><div id='s-sev' style='font-size:14px'>INFO:0 / MED:0 / HIGH:0</div></div>" +
                "      <div class='card' style='flex:1'><div>Staff online</div><div id='s-staff' style='font-size:22px;font-weight:700'>0</div></div>" +
                "    </div>" +
                "  </div>" +
                "  <div class='card'>" +
                "    <h3 style='margin:6px 0'>Logs em tempo real</h3>" +
                "    <table id='logs'>" +
                "      <thead><tr><th>Timestamp</th><th>Jogador</th><th>Check</th><th>Sev</th><th>Info</th></tr></thead>" +
                "      <tbody></tbody>" +
                "    </table>" +
                "  </div>" +
                "</div>" +
                "<script>" +
                "function esc(s){return (s||'').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;')}" +
                "function row(l){var tr=document.createElement('tr');tr.innerHTML='<td>'+l.timestamp+'</td><td>'+esc(l.player)+'</td><td>'+esc(l.check)+'</td><td class=sev-'+esc(l.severity)+'>'+esc(l.severity)+'</td><td>'+esc(l.info)+'</td>';return tr;}" +
                "fetch('/api/logs').then(r=>r.json()).then(arr=>{var tb=document.querySelector('#logs tbody');arr.forEach(l=>tb.appendChild(row(l)));});" +
                "function loadStats(){fetch('/api/stats').then(r=>r.json()).then(s=>{document.getElementById('s-total').innerText=s.total;document.getElementById('s-hour').innerText=s.lastHour;document.getElementById('s-5m').innerText=s.last5m;document.getElementById('s-sev').innerText='INFO:'+s.sev.INFO+' / MED:'+s.sev.MEDIUM+' / HIGH:'+s.sev.HIGH;document.getElementById('s-staff').innerText=s.staffOnline;});} loadStats(); setInterval(loadStats, 5000);" +
                "var ev=new EventSource('/api/stream');ev.onmessage=function(ev){try{var l=JSON.parse(ev.data);var tb=document.querySelector('#logs tbody');tb.insertBefore(row(l),tb.firstChild);loadStats();}catch(e){}};" +
                "</script>" +
                "</body></html>";
        resp.getWriter().write(html);
    }
}
