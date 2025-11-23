package com.linux.cheat.detect;

import com.linux.cheat.logs.CheatLog;
import com.linux.cheat.service.LogService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;

public class NoFallCheckListener implements Listener {
    private final LogService logService;
    private final Map<Player, Double> maxDrop = new HashMap<>();
    private final Map<Player, Long> lastFallDamage = new HashMap<>();

    public NoFallCheckListener(LogService logService) {
        this.logService = logService;
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (e.getTo() == null) return;
        double dy = e.getTo().getY() - e.getFrom().getY();
        // acumulamos queda
        if (dy < 0) {
            double drop = maxDrop.getOrDefault(p, 0.0);
            drop += -dy;
            maxDrop.put(p, drop);
        }
        // quando tocar o chão, verificar se houve queda relevante sem dano
        if (p.isOnGround()) {
            double drop = maxDrop.getOrDefault(p, 0.0);
            if (drop > 3.5) { // queda considerável
                long last = lastFallDamage.getOrDefault(p, 0L);
                if (System.currentTimeMillis() - last > 1200) {
                    logService.log(new CheatLog(p.getName(), "NoFall", "HIGH", System.currentTimeMillis(),
                            String.format("drop=%.2f no damage", drop)));
                }
            }
            maxDrop.put(p, 0.0);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            Player p = (Player) e.getEntity();
            lastFallDamage.put(p, System.currentTimeMillis());
            maxDrop.put(p, 0.0);
        }
    }
}
