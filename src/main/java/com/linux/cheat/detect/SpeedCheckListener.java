package com.linux.cheat.detect;

import com.linux.cheat.logs.CheatLog;
import com.linux.cheat.service.LogService;
import com.linux.cheat.util.ViolationBuffer;
import com.linux.cheat.config.ChecksConfig;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class SpeedCheckListener implements Listener {
    private final LogService logService;
    private final Map<Player, Long> lastCombat = new HashMap<>();
    private final ViolationBuffer buffer;
    private final ChecksConfig cfg;

    public SpeedCheckListener(LogService logService, ChecksConfig cfg) {
        this.logService = logService;
        this.cfg = cfg;
        this.buffer = new ViolationBuffer(cfg.speed_buffer_decay);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)) {
            lastCombat.put((Player) e.getEntity(), System.currentTimeMillis());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        if (e.getTo() == null) return;
        if (e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()) return;
        Player p = e.getPlayer();
        if (p.isInsideVehicle() || p.getAllowFlight()) return; // ignorar voando/veículos

        // ignorar imediatamente após dano (knockback) para evitar falsos
        Long lc = lastCombat.get(p);
        if (lc != null && System.currentTimeMillis() - lc < cfg.speed_combat_grace_ms) {
            buffer.reduce(p);
            return;
        }

        // cálculo velocidade horizontal
        Vector from = e.getFrom().toVector();
        Vector to = e.getTo().toVector();
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();
        double horizontal = Math.sqrt(dx*dx + dz*dz);

        // base para 1.8.8
        double limit = p.isSprinting() ? cfg.speed_sprint_limit : cfg.speed_walk_limit;

        // poções de speed (nível 1 ~20%, nível 2 ~40%)
        PotionEffect eff = p.getActivePotionEffects().stream().filter(pe -> pe.getType().equals(PotionEffectType.SPEED)).findFirst().orElse(null);
        if (eff != null) {
            limit *= (1.0 + 0.20 * (eff.getAmplifier() + 1));
        }

        // em ar aumenta um pouco
        if (!p.isOnGround()) limit += cfg.speed_air_bonus;

        // blocos especiais: gelo, gelo compactado, soul sand, escadas/lajes alteram
        Block under = e.getTo().getBlock().getRelative(0, -1, 0);
        Material m = under.getType();
        if (m == Material.ICE || m == Material.PACKED_ICE) limit += cfg.speed_ice_bonus;
        if (m == Material.SOUL_SAND) limit += cfg.speed_soulsand_penalty;

        // escadas/lajes dão variações, aumentar margem
        Block feet = e.getTo().getBlock();
        if (feet.getType().name().contains("STAIRS") || feet.getType().name().contains("STEP")) limit += cfg.speed_stairs_bonus;

        // calcular violação
        double over = horizontal - limit;
        if (over > 0.0) {
            double v = buffer.add(p, over * 8.0, 12.0);
            if (v >= cfg.speed_buffer_flag) {
                long now = System.currentTimeMillis();
                logService.log(new CheatLog(p.getName(), "Speed", "MEDIUM", now,
                        String.format("vel=%.3f limit=%.3f buf=%.1f", horizontal, limit, v)));
                buffer.reset(p); // reseta após flag para evitar spam
            }
        } else {
            buffer.reduce(p);
        }
    }
}
