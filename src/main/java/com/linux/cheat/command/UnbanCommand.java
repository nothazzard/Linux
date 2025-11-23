package com.linux.cheat.command;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UnbanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("linuxac.ban")) {
            sender.sendMessage(ChatColor.RED + "Sem permissão.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.YELLOW + "Uso: /" + label + " <jogador>");
            return true;
        }
        String target = args[0];
        Bukkit.getBanList(BanList.Type.NAME).pardon(target);
        sender.sendMessage(ChatColor.GREEN + "Unban aplicado: " + target);
        return true;
    }
}
