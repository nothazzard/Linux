package com.linux.cheat.ban;

import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BanUtil {
    private static final SimpleDateFormat DATE = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public static String formatPermanent(String player, String reason, String staff) {
        return  "Seu acesso ao nosso servidor está bloqueado!" + "\n" +
                ChatColor.GRAY + "Jogador: " + ChatColor.YELLOW + player + "\n" +
                ChatColor.GRAY + "Motivo: " + ChatColor.YELLOW + (reason == null || reason.isEmpty() ? "Indefinido" : reason) + "\n" +
                ChatColor.GRAY + "Ban por: " + ChatColor.YELLOW + (staff == null ? "Console" : staff) + "\n" +
                ChatColor.GRAY + "Expira: " + ChatColor.YELLOW + "Nunca" + "\n" +
                ChatColor.DARK_GRAY + "Linux AntiCheat";
    }

    public static String formatTemporary(String player, String reason, String staff, Date expires) {
        String when = expires == null ? "Nunca" : DATE.format(expires);
        return ChatColor.GOLD + "Você foi TEMPBANIDO do servidor" + "\n" +
                ChatColor.GRAY + "Jogador: " + ChatColor.YELLOW + player + "\n" +
                ChatColor.GRAY + "Motivo: " + ChatColor.YELLOW + (reason == null || reason.isEmpty() ? "Indefinido" : reason) + "\n" +
                ChatColor.GRAY + "Ban por: " + ChatColor.YELLOW + (staff == null ? "Console" : staff) + "\n" +
                ChatColor.GRAY + "Expira: " + ChatColor.YELLOW + when + "\n" +
                ChatColor.DARK_GRAY + "Linux AntiCheat";
    }
}
