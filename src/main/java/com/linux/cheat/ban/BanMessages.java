package com.linux.cheat.ban;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BanMessages {
    private final String tmplBanPerm;
    private final String tmplBanTemp;
    private final String tmplKick;
    private final SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public BanMessages(FileConfiguration cfg) {
        this.tmplBanPerm = cfg.getString("messages.ban_permanent",
                "&cSeu acesso ao nosso servidor está bloqueado!\\n&7Jogador: &e{player}\\n&7Motivo: &e{reason}\\n&7Ban por: &e{staff}\\n&7Expira: &eNunca\\n&8Linux AntiCheat");
        this.tmplBanTemp = cfg.getString("messages.ban_temporary",
                "&6Você foi TEMPBANIDO do servidor\\n&7Jogador: &e{player}\\n&7Motivo: &e{reason}\\n&7Ban por: &e{staff}\\n&7Expira: &e{expires}\\n&8Linux AntiCheat");
        this.tmplKick = cfg.getString("messages.kick",
                "&cVocê foi desconectado\\n&7Motivo: &e{reason}\\n&8Linux AntiCheat");
    }

    public String formatPermanent(String player, String reason, String staff) {
        String msg = replaceCommon(tmplBanPerm, player, reason, staff)
                .replace("{expires}", "Nunca");
        return colorize(msg);
    }

    public String formatTemporary(String player, String reason, String staff, Date expires) {
        String when = expires == null ? "Nunca" : dateFmt.format(expires);
        String msg = replaceCommon(tmplBanTemp, player, reason, staff)
                .replace("{expires}", when);
        return colorize(msg);
    }

    public String formatKick(String reason) {
        String msg = tmplKick.replace("{reason}", safe(reason));
        return colorize(msg);
    }

    private String replaceCommon(String template, String player, String reason, String staff) {
        return template
                .replace("{player}", safe(player))
                .replace("{reason}", safe(reason))
                .replace("{staff}", safe(staff == null ? "Console" : staff));
    }

    private String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s.replace("\\n", "\n"));
    }

    private String safe(String s) {
        return (s == null || s.isEmpty()) ? "Indefinido" : s;
    }
}
