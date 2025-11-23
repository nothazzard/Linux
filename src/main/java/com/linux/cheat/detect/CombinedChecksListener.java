package com.linux.cheat.detect;

import com.linux.cheat.config.ChecksConfig;
import com.linux.cheat.logs.CheatLog;
import com.linux.cheat.service.LogService;
import com.linux.cheat.util.ViolationBuffer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class CombinedChecksListener implements Listener {
    private final LogService logService;
    private final ChecksConfig cfg;

    // Buffers / state
    private final ViolationBuffer kaRateBuf = new ViolationBuffer(1.0);
    private final Map<Player, Integer> hitsPerSec = new HashMap<>();
    private long kaLastSecond = System.currentTimeMillis();

    private final Map<Player, Integer> placePerSec = new HashMap<>();
    private long placeLastSecond = System.currentTimeMillis();

    private final Map<Player, Integer> heal10s = new HashMap<>();
    private long healWindowStart = System.currentTimeMillis();

    private final Map<Player, Integer> breakPerSec = new HashMap<>();
    private long breakLastSecond = System.currentTimeMillis();

    private final Map<Player, Long> lastUse = new HashMap<>();
    private final Map<Player, Long> lastSneakToggle = new HashMap<>();
    private final Map<Player, Integer> airTicks = new HashMap<>();

    // InventoryMove buffer
    private ViolationBuffer invBuf;

    public CombinedChecksListener(LogService logService, ChecksConfig cfg) {
        this.logService = logService;
        this.cfg = cfg;
        this.invBuf = new ViolationBuffer(cfg.inventorymove_buffer_decay);
    }

    // InventoryMove
    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent e) {
        // apenas marca evento; verificado em movimento
        // nada aqui
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        // idem
    }

    @EventHandler(ignoreCancelled = true)
    public void onMoveInv(PlayerMoveEvent e) {
        if (e.getTo() == null) return;
        if (!cfg.inventorymove_enabled) return; // disabled via config
        if (e.getPlayer().getOpenInventory() != null && e.getPlayer().getOpenInventory().getTopInventory() != null
                && e.getPlayer().getOpenInventory().getTopInventory().getSize() > 0) {
            // se não permitir andar com inventário
            if (!cfg.inventorymove_allow_sneak || !e.getPlayer().isSneaking()) {
                Vector from = e.getFrom().toVector();
                Vector to = e.getTo().toVector();
                double dx = to.getX() - from.getX();
                double dz = to.getZ() - from.getZ();
                double h = Math.sqrt(dx*dx + dz*dz);
                if (h > cfg.inventorymove_move_threshold) {
                    double v = invBuf.add(e.getPlayer(), (h - cfg.inventorymove_move_threshold) * 6.0, 10.0);
                    if (v >= cfg.inventorymove_buffer_flag) {
                        logService.log(new CheatLog(e.getPlayer().getName(), "InventoryMove", "MEDIUM",
                                System.currentTimeMillis(), String.format("moved=%.3f buf=%.1f", h, v)));
                        invBuf.reset(e.getPlayer());
                    }
                } else {
                    invBuf.reduce(e.getPlayer());
                }
            }
        }
    }

    // FastUse (uso de itens muito rápido)
    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack is = p.getItemInHand();
        if (is == null) return;
        Material m = is.getType();
        if (m == Material.GOLDEN_APPLE || m == Material.POTION || m == Material.BOW) {
            long now = System.currentTimeMillis();
            Long last = lastUse.put(p, now);
            if (last != null) {
                long dt = now - last;
                if (m == Material.BOW) {
                    if (dt < cfg.fastbow_min_draw_ms) {
                        logService.log(new CheatLog(p.getName(), "FastBow", "MEDIUM", now, "draw_ms=" + dt));
                    }
                } else {
                    if (dt < cfg.fastuse_min_ms) {
                        logService.log(new CheatLog(p.getName(), "FastUse", "MEDIUM", now, "use_ms=" + dt));
                    }
                }
            }
        }
    }

    // FastPlace
    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        int c = placePerSec.getOrDefault(p, 0) + 1;
        placePerSec.put(p, c);
        long now = System.currentTimeMillis();
        if (now - placeLastSecond >= 1000) {
            for (Map.Entry<Player, Integer> en : placePerSec.entrySet()) {
                int count = en.getValue();
                if (count > cfg.fastplace_max_bps) {
                    logService.log(new CheatLog(en.getKey().getName(), "FastPlace", "MEDIUM", now, "bps=" + count));
                }
            }
            placePerSec.clear();
            placeLastSecond = now;
        }
        // Scaffold básico: colocar bloco abaixo/na borda continuamente em ângulo vertical alto
        Vector dir = p.getLocation().getDirection();
        double pitch = Math.toDegrees(Math.asin(dir.getY()));
        if (Math.abs(pitch) > cfg.scaffold_max_place_angle_deg) {
            logService.log(new CheatLog(p.getName(), "Scaffold", "MEDIUM", now, String.format("pitch=%.1f", pitch)));
        }
    }

    // KillAuraRate (hits por segundo)
    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player p = (Player) e.getDamager();
        int c = hitsPerSec.getOrDefault(p, 0) + 1;
        hitsPerSec.put(p, c);
        long now = System.currentTimeMillis();
        if (now - kaLastSecond >= 1000) {
            for (Map.Entry<Player, Integer> en : hitsPerSec.entrySet()) {
                int count = en.getValue();
                if (count > cfg.karate_max_hps) {
                    double v = kaRateBuf.add(en.getKey(), (count - cfg.karate_max_hps) * 0.2, 10);
                    if (v >= 5) {
                        logService.log(new CheatLog(en.getKey().getName(), "KillauraRate", "HIGH", now, "hps=" + count));
                        kaRateBuf.reset(en.getKey());
                    }
                } else kaRateBuf.reduce(en.getKey());
            }
            hitsPerSec.clear();
            kaLastSecond = now;
        }
    }

    // Jesus (andar sobre água)
    @EventHandler(ignoreCancelled = true)
    public void onMoveJesus(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Block feet = e.getTo().getBlock();
        Block under = feet.getRelative(0, -1, 0);
        if (isWater(feet) || isWater(under)) {
            // se vertical está praticamente plana por muitas amostras
            double dy = e.getTo().getY() - e.getFrom().getY();
            if (Math.abs(dy) < 0.01) {
                // contamos via airTicks como flat in liquid
                int t = airTicks.getOrDefault(p, 0) + 1;
                airTicks.put(p, t);
                if (t > cfg.jesus_max_flat_ticks) {
                    logService.log(new CheatLog(p.getName(), "Jesus", "HIGH", System.currentTimeMillis(), "flat on water"));
                    airTicks.put(p, 0);
                }
            } else {
                airTicks.put(p, 0);
            }
        } else {
            airTicks.put(p, 0);
        }
    }

    private boolean isWater(Block b) {
        Material m = b.getType();
        return m == Material.WATER || m == Material.STATIONARY_WATER;
    }

    // Fly simples (tempo no ar)
    @EventHandler(ignoreCancelled = true)
    public void onMoveFly(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.getAllowFlight()) return;
        if (!p.isOnGround()) {
            int t = airTicks.getOrDefault(p, 0) + 1;
            airTicks.put(p, t);
            if (t > cfg.fly_max_air_ticks) {
                logService.log(new CheatLog(p.getName(), "Fly", "HIGH", System.currentTimeMillis(), "air_ticks=" + t));
                airTicks.put(p, 0);
            }
        } else {
            airTicks.put(p, 0);
        }
    }

    // AntiKB-lite (após dano, verificar deslocamento)
    @EventHandler(ignoreCancelled = true)
    public void onKB(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK && e.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) return;
        Player p = (Player) e.getEntity();
        // aguardaremos próximo movimento para observar deslocamento
        lastUse.put(p, System.currentTimeMillis()); // reutilizando mapa para marcação
    }

    @EventHandler(ignoreCancelled = true)
    public void onMoveKB(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Long t = lastUse.get(p);
        if (t != null && System.currentTimeMillis() - t < 500) {
            double dx = e.getTo().getX() - e.getFrom().getX();
            double dz = e.getTo().getZ() - e.getFrom().getZ();
            double h = Math.sqrt(dx*dx + dz*dz);
            if (h < cfg.antikb_min_ratio * 0.4) { // muito baixo
                logService.log(new CheatLog(p.getName(), "AntiKB", "HIGH", System.currentTimeMillis(), String.format("kb=%.3f", h)));
            }
            lastUse.remove(p);
        }
    }

    // AutoSneak (toggle muito rápido/constante)
    @EventHandler(ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        long now = System.currentTimeMillis();
        Long last = lastSneakToggle.put(p, now);
        if (last != null && now - last < cfg.autosneak_min_toggle_ms) {
            logService.log(new CheatLog(p.getName(), "AutoSneak", "MEDIUM", now, "toggle_ms=" + (now - last)));
        }
    }

    // Step (subida alta sem escadas/lajes)
    @EventHandler(ignoreCancelled = true)
    public void onStep(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (e.getTo() == null) return;
        double dy = e.getTo().getY() - e.getFrom().getY();
        if (dy > cfg.step_max_height) {
            Material m = e.getTo().getBlock().getType();
            String name = m.name();
            if (!name.contains("STAIRS") && !name.contains("STEP") && !name.contains("SLAB") && m != Material.LADDER && m != Material.VINE) {
                logService.log(new CheatLog(p.getName(), "Step", "MEDIUM", System.currentTimeMillis(), String.format("dy=%.2f", dy)));
            }
        }
    }

    // FastLadder (subida em escada muito rápida)
    @EventHandler(ignoreCancelled = true)
    public void onLadder(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (e.getTo() == null) return;
        if (e.getTo().getBlock().getType() == Material.LADDER) {
            double dy = e.getTo().getY() - e.getFrom().getY();
            if (dy > cfg.fastladder_max_speed) {
                logService.log(new CheatLog(p.getName(), "FastLadder", "MEDIUM", System.currentTimeMillis(), String.format("dy=%.2f", dy)));
            }
        }
    }

    // Criticals (hits "críticos" sem queda real)
    @EventHandler(ignoreCancelled = true)
    public void onCrit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        if (!cfg.criticals_enabled) return;
        Player p = (Player) e.getDamager();
        if (p.getFallDistance() < cfg.criticals_min_fall_distance && !p.isOnGround() && !p.hasPotionEffect(PotionEffectType.JUMP)) {
            logService.log(new CheatLog(p.getName(), "Criticals", "MEDIUM", System.currentTimeMillis(), "no real fall"));
        }
    }

    // BadPackets-Position (delta horizontal por tick enorme)
    @EventHandler(ignoreCancelled = true)
    public void onBadPos(PlayerMoveEvent e) {
        if (e.getTo() == null) return;
        double dx = Math.abs(e.getTo().getX() - e.getFrom().getX());
        double dz = Math.abs(e.getTo().getZ() - e.getFrom().getZ());
        double h = Math.max(dx, dz);
        if (h > cfg.badpackets_max_horiz_per_tick) {
            logService.log(new CheatLog(e.getPlayer().getName(), "BadPackets-Pos", "HIGH", System.currentTimeMillis(), String.format("h=%.2f", h)));
        }
    }

    // BadPackets-Look (yaw/pitch saltos gigantes)
    @EventHandler(ignoreCancelled = true)
    public void onLook(PlayerMoveEvent e) {
        if (e.getTo() == null) return;
        if (!cfg.badpackets_look_enabled) return;
        float dyaw = Math.abs(e.getTo().getYaw() - e.getFrom().getYaw());
        float dpitch = Math.abs(e.getTo().getPitch() - e.getFrom().getPitch());
        if (dyaw > cfg.badpackets_max_yaw || dpitch > cfg.badpackets_max_pitch) {
            logService.log(new CheatLog(e.getPlayer().getName(), "BadPackets-Look", "MEDIUM", System.currentTimeMillis(), String.format("yaw=%.1f pitch=%.1f", dyaw, dpitch)));
        }
    }

    // AutoArmor (troca muito rápida)
    @EventHandler(ignoreCancelled = true)
    public void onArmor(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        long now = System.currentTimeMillis();
        Long last = lastUse.put(p, now);
        if (last != null && now - last < cfg.autoarmor_min_swap_ms) {
            logService.log(new CheatLog(p.getName(), "AutoArmor", "MEDIUM", now, "swap_ms=" + (now - last)));
        }
    }

    // FastHeal (eventos de cura em janela)
    @EventHandler(ignoreCancelled = true)
    public void onRegen(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        int c = heal10s.getOrDefault(p, 0) + 1;
        heal10s.put(p, c);
        long now = System.currentTimeMillis();
        if (now - healWindowStart >= 10_000) {
            for (Map.Entry<Player, Integer> en : heal10s.entrySet()) {
                if (en.getValue() > cfg.fastheal_max_10s) {
                    logService.log(new CheatLog(en.getKey().getName(), "FastHeal", "MEDIUM", now, "evts=" + en.getValue()));
                }
            }
            heal10s.clear();
            healWindowStart = now;
        }
    }

    // NoWeb (mobilidade em teias)
    @EventHandler(ignoreCancelled = true)
    public void onWeb(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Material feet = e.getTo() == null ? Material.AIR : e.getTo().getBlock().getType();
        if (feet == Material.WEB) {
            double dx = e.getTo().getX() - e.getFrom().getX();
            double dz = e.getTo().getZ() - e.getFrom().getZ();
            double h = Math.sqrt(dx*dx + dz*dz);
            if (h > 0.1) {
                logService.log(new CheatLog(p.getName(), "NoWeb", "MEDIUM", System.currentTimeMillis(), String.format("h=%.2f", h)));
            }
        }
    }

    // FastBreak (heurstico simples por taxa de quebra)
    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        int c = breakPerSec.getOrDefault(p, 0) + 1;
        breakPerSec.put(p, c);
        long now = System.currentTimeMillis();
        if (now - breakLastSecond >= 1000) {
            for (Map.Entry<Player, Integer> en : breakPerSec.entrySet()) {
                if (en.getValue() > 18) {
                    logService.log(new CheatLog(en.getKey().getName(), "FastBreak", "MEDIUM", now, "bps=" + en.getValue()));
                }
            }
            breakPerSec.clear();
            breakLastSecond = now;
        }
    }
}
