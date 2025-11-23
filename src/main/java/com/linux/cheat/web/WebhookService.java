package com.linux.cheat.web;

import com.linux.cheat.logs.CheatLog;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebhookService {
    private final String webhookUrl;

    public WebhookService(String webhookUrl) {
        this.webhookUrl = webhookUrl == null ? "" : webhookUrl.trim();
    }

    public boolean isConfigured() {
        return !this.webhookUrl.isEmpty();
    }

    public void sendAlert(CheatLog log) throws Exception {
        if (!isConfigured()) return;
        URL url = new URL(webhookUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String payload = toJson(log);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes(StandardCharsets.UTF_8));
        }
        int code = conn.getResponseCode();
        if (code >= 400) {
            throw new IllegalStateException("Webhook HTTP " + code);
        }
    }

    private String toJson(CheatLog log) {
        // simples JSON manual para evitar dependências extras
        return "{" +
                "\"player\":\"" + escape(log.getPlayer()) + "\"," +
                "\"check\":\"" + escape(log.getCheck()) + "\"," +
                "\"severity\":\"" + escape(log.getSeverity()) + "\"," +
                "\"timestamp\":" + log.getTimestamp() + "," +
                "\"info\":\"" + escape(log.getInfo()) + "\"" +
                "}";
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
