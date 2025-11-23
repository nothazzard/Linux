package com.linux.cheat.detect;

import com.linux.cheat.logs.CheatLog;
import com.linux.cheat.service.LogService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;

import java.util.HashMap;
import java.util.Map;

public class CpsCheckListener implements Listener {
    private final LogService logService;
    private final Map<Player, Integer> counter = new HashMap<>();
    private long lastSecond = System.currentTimeMillis();

    public CpsCheckListener(LogService logService) {
        this.logService = logService;
    }

    @EventHandler(ignoreCancelled = true)
    public void onSwing(PlayerAnimationEvent e) {
        Player p = e.getPlayer();
        // incrementar contador simples por jogador
        counter.put(p, counter.getOrDefault(p, 0) + 1);
        long now = System.currentTimeMillis();
        if (now - lastSecond >= 1000) {
            // avaliar CPS a cada segundo
            for (Map.Entry<Player, Integer> en : counter.entrySet()) {
                int cps = en.getValue();
                if (cps > 18) { // limite simples; valores altos indicam autoclick
                    logService.log(new CheatLog(en.getKey().getName(), "CPS", "MEDIUM", now,
                            "cps=" + cps));
                }
            }
            counter.clear();
            lastSecond = now;
        }
    }
}
