package com.linux.cheat.command;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Date;
import com.linux.cheat.ban.BanMessages;

public class SbanCommand implements CommandExecutor {
    private final Plugin plugin;
    private final BanMessages messages;

    public SbanCommand(Plugin plugin, BanMessages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("linuxac.ban")) {
            sender.sendMessage(ChatColor.RED + "Sem permissão.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Uso: /" + label + " <jogador> <motivo>");
            return true;
        }
        String target = args[0];
        String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        String staff = sender.getName();
        Bukkit.getBanList(BanList.Type.NAME).addBan(target, reason, (Date) null, staff);
        Player p = Bukkit.getPlayerExact(target);
        if (p != null && p.isOnline()) {
            p.kickPlayer(messages.formatPermanent(target, reason, staff));
        }
        sender.sendMessage(ChatColor.GREEN + "Banido: " + target + " por: " + reason);
        return true;
    }
}
