package com.linux.cheat.detect;

import com.linux.cheat.logs.CheatLog;
import com.linux.cheat.service.LogService;
import com.linux.cheat.util.ViolationBuffer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;

public class TimerCheckListener implements Listener {
    private final LogService logService;
    private final ViolationBuffer buffer = new ViolationBuffer(0.5);
    private final Map<Player, Integer> ticks = new HashMap<>();
    private long lastSecond = System.currentTimeMillis();
    private final int maxActionsPerSecond;

    public TimerCheckListener(LogService logService, int maxActionsPerSecond) {
        this.logService = logService;
        this.maxActionsPerSecond = maxActionsPerSecond;
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        ticks.put(p, ticks.getOrDefault(p, 0) + 1);
        long now = System.currentTimeMillis();
        if (now - lastSecond >= 1000) {
            for (Map.Entry<Player, Integer> en : ticks.entrySet()) {
                int count = en.getValue();
                if (count > maxActionsPerSecond) {
                    double v = buffer.add(en.getKey(), (count - maxActionsPerSecond) * 0.2, 10);
                    if (v >= 5) {
                        logService.log(new CheatLog(en.getKey().getName(), "Timer", "MEDIUM", now,
                                "aps=" + count + " limit=" + maxActionsPerSecond));
                        buffer.reset(en.getKey());
                    }
                } else buffer.reduce(en.getKey());
            }
            ticks.clear();
            lastSecond = now;
        }
    }
}
