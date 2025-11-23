package com.linux.cheat.command;

import com.linux.cheat.db.LogsRepository;
import com.linux.cheat.logs.CheatLog;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class LogsCommand implements CommandExecutor {
    private final Plugin plugin;
    private final LogsRepository repo;

    public LogsCommand(Plugin plugin, LogsRepository repo) {
        this.plugin = plugin;
        this.repo = repo;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("linuxac.logs")) {
            sender.sendMessage(ChatColor.RED + "Sem permissão.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.YELLOW + "Uso: /" + label + " <jogador> [limite]");
            return true;
        }
        String player = args[0];
        int limit = 20;
        if (args.length >= 2) {
            try { limit = Integer.parseInt(args[1]); } catch (Exception ignored) {}
        }
        int finalLimit = Math.max(1, Math.min(200, limit));
        sender.sendMessage(ChatColor.GRAY + "Consultando logs de " + ChatColor.YELLOW + player + ChatColor.GRAY + " (limite=" + finalLimit + ")...");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                List<CheatLog> list = repo.latestByPlayer(player, finalLimit);
                if (list.isEmpty()) {
                    sender.sendMessage(ChatColor.GRAY + "Sem logs para " + ChatColor.YELLOW + player);
                    return;
                }
                sender.sendMessage(ChatColor.DARK_GRAY + "---- " + ChatColor.RED + "Logs de " + player + ChatColor.DARK_GRAY + " ----");
                for (CheatLog l : list) {
                    sender.sendMessage(ChatColor.GRAY + "[" + l.getTimestamp() + "] " + ChatColor.YELLOW + l.getPlayer() + ChatColor.GRAY + " " + l.getCheck() + " (" + l.getSeverity() + ") " + l.getInfo());
                }
            } catch (Exception ex) {
                sender.sendMessage(ChatColor.RED + "Erro ao consultar logs: " + ex.getMessage());
            }
        });
        return true;
    }
}
