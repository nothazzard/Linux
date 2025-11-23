package com.linux.cheat.detect;

import com.linux.cheat.config.ChecksConfig;
import com.linux.cheat.logs.CheatLog;
import com.linux.cheat.service.LogService;
import com.linux.cheat.util.ViolationBuffer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class NoSlowCheckListener implements Listener {
    private final LogService logService;
    private final ChecksConfig cfg;
    private final ViolationBuffer buffer;
    private final Map<Player, Double> lastH = new HashMap<>();

    public NoSlowCheckListener(LogService logService, ChecksConfig cfg) {
        this.logService = logService;
        this.cfg = cfg;
        this.buffer = new ViolationBuffer(0.2);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        if (e.getTo() == null) return;
        Player p = e.getPlayer();
        Vector from = e.getFrom().toVector();
        Vector to = e.getTo().toVector();
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();
        double h = Math.sqrt(dx*dx + dz*dz);
        Double last = lastH.put(p, h);

        boolean using = p.isBlocking() || isUsingItem(p.getItemInHand());
        if (using && last != null && last > 0) {
            double factor = h / last;
            if (factor > cfg.noslow_min_slow_factor + 0.5) { // deveria estar mais lento
                double v = buffer.add(p, (factor - (cfg.noslow_min_slow_factor + 0.5)) * 2, 10);
                if (v >= 5) {
                    logService.log(new CheatLog(p.getName(), "NoSlow", "MEDIUM", System.currentTimeMillis(),
                            String.format("factor=%.2f", factor)));
                    buffer.reset(p);
                }
            } else buffer.reduce(p);
        } else buffer.reduce(p);
    }

    private boolean isUsingItem(ItemStack is) {
        if (is == null) return false;
        Material m = is.getType();
        return m == Material.BOW || m == Material.POTION || m == Material.GOLDEN_APPLE || m == Material.MILK_BUCKET;
    }
}
