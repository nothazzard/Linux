package com.linux.cheat.util;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

public class ViolationBuffer {
    private final Map<Player, Double> buf = new HashMap<>();
    private final double decay;

    public ViolationBuffer(double decay) {
        this.decay = decay;
    }

    public double add(Player p, double amt, double max) {
        double v = buf.getOrDefault(p, 0.0);
        v = Math.max(0.0, v - decay);
        v += amt;
        if (v > max) v = max;
        buf.put(p, v);
        return v;
    }

    public double get(Player p) { return buf.getOrDefault(p, 0.0); }

    public void reduce(Player p) {
        double v = Math.max(0.0, buf.getOrDefault(p, 0.0) - decay);
        buf.put(p, v);
    }

    public void reset(Player p) { buf.remove(p); }
}
