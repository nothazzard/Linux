package com.linux.cheat.detect;

import com.linux.cheat.logs.CheatLog;
import com.linux.cheat.service.LogService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class ReachCheckListener implements Listener {
    private final LogService logService;

    public ReachCheckListener(LogService logService) {
        this.logService = logService;
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Entity victim = e.getEntity();
        Player attacker = (Player) e.getDamager();
        // distância horizontal simples entre olhos do atacante e ponto aproximado do alvo
        Vector a = attacker.getEyeLocation().toVector();
        double yOffset = (victim instanceof LivingEntity) ? (((LivingEntity) victim).getEyeHeight() * 0.5) : 0.9;
        Vector v = victim.getLocation().toVector().add(new Vector(0, yOffset, 0));
        double dx = a.getX() - v.getX();
        double dz = a.getZ() - v.getZ();
        double horizontal = Math.sqrt(dx*dx + dz*dz);
        double limit = 3.6; // margem simples para 1.8.x
        if (horizontal > limit) {
            long now = System.currentTimeMillis();
            logService.log(new CheatLog(attacker.getName(), "Reach", "HIGH", now,
                    String.format("reach=%.2f limit=%.2f", horizontal, limit)));
        }
    }
}
