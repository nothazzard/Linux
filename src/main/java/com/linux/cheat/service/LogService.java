package com.linux.cheat.service;

import com.linux.cheat.db.LogsRepository;
import com.linux.cheat.logs.CheatLog;
import com.linux.cheat.logs.LogStore;
import com.linux.cheat.web.WebhookService;
import com.linux.cheat.web.sse.StreamHub;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class LogService {
    private final Plugin plugin;
    private final LogsRepository repo;
    private final WebhookService webhook;

    public LogService(Plugin plugin, LogsRepository repo, WebhookService webhook) {
        this.plugin = plugin;
        this.repo = repo;
        this.webhook = webhook;
    }

    public void log(CheatLog log) {
        // memória
        LogStore.get().add(log);

        // realtime panel (SSE)
        try { StreamHub.get().broadcast("log", toJson(log)); } catch (Exception ignored) {}

        // db
        if (repo != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try { repo.insert(log); } catch (Exception ignored) {}
            });
        }
        // webhook
        if (webhook != null && webhook.isConfigured()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try { webhook.sendAlert(log); } catch (Exception ignored) {}
            });
        }

        // notify in-game staff (op or permission)
        String msg = ChatColor.GRAY + "[" + ChatColor.RED + "LinuxAC" + ChatColor.GRAY + "] " +
                ChatColor.YELLOW + log.getPlayer() + ChatColor.GRAY + " suspeito em " +
                ChatColor.AQUA + log.getCheck() + ChatColor.GRAY + " (" + ChatColor.GOLD + log.getSeverity() + ChatColor.GRAY + ") " +
                ChatColor.DARK_GRAY + log.getInfo();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp() || p.hasPermission("linuxac.notify")) {
                p.sendMessage(msg);
            }
        }

        // auto-kick removido a pedido do usuário
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
