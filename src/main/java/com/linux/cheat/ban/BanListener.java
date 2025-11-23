package com.linux.cheat.ban;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Date;

public class BanListener implements Listener {
    private final BanMessages messages;

    public BanListener(BanMessages messages) {
        this.messages = messages;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if (e.getResult() == PlayerLoginEvent.Result.KICK_BANNED) {
            String name = e.getPlayer().getName();
            BanList list = Bukkit.getBanList(BanList.Type.NAME);
            BanEntry entry = list.getBanEntry(name);
            String reason = entry != null ? entry.getReason() : "Indefinido";
            String source = entry != null ? entry.getSource() : "Console";
            Date expires = entry != null ? entry.getExpiration() : null;
            String msg = expires == null
                    ? messages.formatPermanent(name, reason, source)
                    : messages.formatTemporary(name, reason, source, expires);
            e.setKickMessage(msg);
        }
    }
}
