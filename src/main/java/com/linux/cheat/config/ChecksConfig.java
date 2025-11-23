package com.linux.cheat.config;

import org.bukkit.configuration.file.FileConfiguration;

public class ChecksConfig {
    public final double speed_buffer_flag;
    public final double speed_buffer_decay;
    public final double speed_sprint_limit;
    public final double speed_walk_limit;
    public final double speed_air_bonus;
    public final double speed_ice_bonus;
    public final double speed_soulsand_penalty;
    public final double speed_stairs_bonus;
    public final int speed_combat_grace_ms;

    public final int cps_max;

    public final double reach_limit;

    public final double nofall_min_drop;
    public final int nofall_damage_grace_ms;

    public final int timer_max_aps;

    public final double noslow_min_slow_factor;
    public final boolean inventorymove_enabled;
    public final boolean inventorymove_allow_sneak;
    public final double inventorymove_move_threshold;
    public final double inventorymove_buffer_flag;
    public final double inventorymove_buffer_decay;

    public final int fastuse_min_ms;
    public final int fastplace_max_bps;

    public final int karate_max_hps;

    public final int jesus_samples;
    public final int jesus_max_flat_ticks;

    public final int fly_max_air_ticks;

    public final double antikb_min_ratio;

    public final int autosneak_min_toggle_ms;

    public final double step_max_height;

    public final double fastladder_max_speed;

    public final int fastbow_min_draw_ms;

    public final boolean criticals_enabled;
    public final double criticals_min_fall_distance;

    public final double badpackets_max_horiz_per_tick;

    public final boolean badpackets_look_enabled;
    public final double badpackets_max_yaw;
    public final double badpackets_max_pitch;

    public final double scaffold_max_place_angle_deg;

    public final int autoarmor_min_swap_ms;

    public final int fastheal_max_10s;

    public ChecksConfig(FileConfiguration c) {
        speed_buffer_flag = c.getDouble("checks.speed.buffer_flag", 8.0);
        speed_buffer_decay = c.getDouble("checks.speed.buffer_decay", 0.25);
        speed_sprint_limit = c.getDouble("checks.speed.sprint_limit", 0.36);
        speed_walk_limit = c.getDouble("checks.speed.walk_limit", 0.28);
        speed_air_bonus = c.getDouble("checks.speed.air_bonus", 0.06);
        speed_ice_bonus = c.getDouble("checks.speed.ice_bonus", 0.18);
        speed_soulsand_penalty = c.getDouble("checks.speed.soulsand_penalty", -0.10);
        speed_stairs_bonus = c.getDouble("checks.speed.stairs_bonus", 0.06);
        speed_combat_grace_ms = c.getInt("checks.speed.combat_grace_ms", 600);

        cps_max = c.getInt("checks.cps.max_cps", 18);

        reach_limit = c.getDouble("checks.reach.limit", 3.6);

        nofall_min_drop = c.getDouble("checks.nofall.min_drop", 3.5);
        nofall_damage_grace_ms = c.getInt("checks.nofall.damage_grace_ms", 1200);

        timer_max_aps = c.getInt("checks.timer.max_actions_per_second", 24);

        noslow_min_slow_factor = c.getDouble("checks.noslow.min_slow_factor", 0.6);
        inventorymove_enabled = c.getBoolean("checks.inventorymove.enabled", false);
        inventorymove_allow_sneak = c.getBoolean("checks.inventorymove.allow_sneak", true);
        inventorymove_move_threshold = c.getDouble("checks.inventorymove.move_threshold", 0.12);
        inventorymove_buffer_flag = c.getDouble("checks.inventorymove.buffer_flag", 3.0);
        inventorymove_buffer_decay = c.getDouble("checks.inventorymove.buffer_decay", 0.5);

        fastuse_min_ms = c.getInt("checks.fastuse.min_use_ms", 1400);
        fastplace_max_bps = c.getInt("checks.fastplace.max_blocks_per_second", 10);

        karate_max_hps = c.getInt("checks.ka_rate.max_hits_per_second", 16);

        jesus_samples = c.getInt("checks.jesus.samples", 8);
        jesus_max_flat_ticks = c.getInt("checks.jesus.max_flat_ticks", 6);

        fly_max_air_ticks = c.getInt("checks.fly.max_air_ticks", 18);

        antikb_min_ratio = c.getDouble("checks.antikb.min_knockback_ratio", 0.2);

        autosneak_min_toggle_ms = c.getInt("checks.autosneak.min_toggle_ms", 400);

        step_max_height = c.getDouble("checks.step.max_step_height", 1.1);

        fastladder_max_speed = c.getDouble("checks.fastladder.max_ladder_speed", 0.45);

        fastbow_min_draw_ms = c.getInt("checks.fastbow.min_draw_ms", 300);

        criticals_enabled = c.getBoolean("checks.criticals.enabled", false);
        criticals_min_fall_distance = c.getDouble("checks.criticals.min_fall_distance", 0.1);

        badpackets_max_horiz_per_tick = c.getDouble("checks.badpackets_position.max_horizontal_per_tick", 1.2);

        badpackets_look_enabled = c.getBoolean("checks.badpackets_look.enabled", false);
        badpackets_max_yaw = c.getDouble("checks.badpackets_look.max_yaw_delta", 180);
        badpackets_max_pitch = c.getDouble("checks.badpackets_look.max_pitch_delta", 50);

        scaffold_max_place_angle_deg = c.getDouble("checks.scaffold.max_place_angle_deg", 80);

        autoarmor_min_swap_ms = c.getInt("checks.autoarmor.min_swap_ms", 250);

        fastheal_max_10s = c.getInt("checks.fastheal.max_heal_events_per_10s", 8);
    }
}
