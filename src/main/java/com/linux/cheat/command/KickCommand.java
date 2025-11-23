package com.linux.cheat.command;

import com.linux.cheat.ban.BanMessages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class KickCommand implements CommandExecutor {
    private final Plugin plugin;
    private final BanMessages messages;

    public KickCommand(Plugin plugin, BanMessages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("linuxac.kick")) {
            sender.sendMessage(ChatColor.RED + "Sem permissão.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Uso: /" + label + " <jogador> <motivo>");
            return true;
        }
        String target = args[0];
        String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        Player p = Bukkit.getPlayerExact(target);
        if (p == null || !p.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Jogador offline ou não encontrado.");
            return true;
        }
        p.kickPlayer(messages.formatKick(reason));
        sender.sendMessage(ChatColor.GREEN + "Kick aplicado: " + target + " por: " + reason);
        return true;
    }
}
