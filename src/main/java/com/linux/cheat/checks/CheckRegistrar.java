package com.linux.cheat.checks;

import com.linux.cheat.config.ChecksConfig;
import com.linux.cheat.detect.*;
import com.linux.cheat.service.LogService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class CheckRegistrar {
    private final Plugin plugin;
    private final LogService logService;
    private final ChecksConfig cfg;

    public CheckRegistrar(Plugin plugin, LogService logService, ChecksConfig cfg) {
        this.plugin = plugin;
        this.logService = logService;
        this.cfg = cfg;
    }

    public void registerAll() {
        Bukkit.getPluginManager().registerEvents(new SpeedCheckListener(logService, cfg), plugin);
        Bukkit.getPluginManager().registerEvents(new CpsCheckListener(logService), plugin);
        Bukkit.getPluginManager().registerEvents(new ReachCheckListener(logService), plugin);
        Bukkit.getPluginManager().registerEvents(new NoFallCheckListener(logService), plugin);
        Bukkit.getPluginManager().registerEvents(new TimerCheckListener(logService, cfg.timer_max_aps), plugin);
        Bukkit.getPluginManager().registerEvents(new NoSlowCheckListener(logService, cfg), plugin);
        Bukkit.getPluginManager().registerEvents(new CombinedChecksListener(logService, cfg), plugin);
    }
}
