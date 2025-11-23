package com.linux.cheat.command;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.linux.cheat.ban.BanMessages;

import java.util.Date;

public class TbanCommand implements CommandExecutor {
    private final BanMessages messages;

    public TbanCommand(BanMessages messages) {
        this.messages = messages;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("linuxac.ban")) {
            sender.sendMessage(ChatColor.RED + "Sem permissão.");
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(ChatColor.YELLOW + "Uso: /" + label + " <jogador> <tempo><s|m|h|d> <motivo>");
            return true;
        }
        String target = args[0];
        String timeArg = args[1].toLowerCase();
        long millis = parseDuration(timeArg);
        if (millis <= 0) {
            sender.sendMessage(ChatColor.RED + "Tempo inválido. Ex: 30m, 2h, 7d");
            return true;
        }
        String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));
        Date expires = new Date(System.currentTimeMillis() + millis);
        String staff = sender.getName();
        Bukkit.getBanList(BanList.Type.NAME).addBan(target, reason, expires, staff);
        Player p = Bukkit.getPlayerExact(target);
        if (p != null && p.isOnline()) {
            p.kickPlayer(messages.formatTemporary(target, reason, staff, expires));
        }
        sender.sendMessage(ChatColor.GREEN + "Tempban aplicado: " + target + " por: " + reason);
        return true;
    }

    private long parseDuration(String s) {
        try {
            char u = s.charAt(s.length()-1);
            long n = Long.parseLong(s.substring(0, s.length()-1));
            switch (u) {
                case 's': return n * 1000L;
                case 'm': return n * 60_000L;
                case 'h': return n * 3_600_000L;
                case 'd': return n * 86_400_000L;
                default: return -1;
            }
        } catch (Exception e) { return -1; }
    }
}
