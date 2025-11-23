package com.linux.cheat.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.linux.cheat.web.WebServer;
import com.linux.cheat.web.WebhookService;
import com.linux.cheat.logs.LogStore;
import com.linux.cheat.logs.CheatLog;
import com.linux.cheat.auth.AuthService;
import com.linux.cheat.db.Database;
import com.linux.cheat.db.LogsRepository;
import com.linux.cheat.service.LogService;
import com.linux.cheat.config.ChecksConfig;
import com.linux.cheat.checks.CheckRegistrar;
import com.linux.cheat.command.SbanCommand;
import com.linux.cheat.command.TbanCommand;
import com.linux.cheat.command.UnbanCommand;
import com.linux.cheat.command.LogsCommand;
import com.linux.cheat.ban.BanListener;
import com.linux.cheat.ban.BanMessages;
import com.linux.cheat.command.KickCommand;

public class Main extends JavaPlugin {

    private WebServer webServer;
    private WebhookService webhookService;
    private Database database;
    private LogsRepository logsRepository;
    private LogService logService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        String webhookUrl = getConfig().getString("webhook.url", "");
        int webPort = getConfig().getInt("web.port", 8080);

        // Auth config
        String panelPass = getConfig().getString("auth.password", "changeme");
        java.util.List<String> staffList = getConfig().getStringList("auth.staff");
        AuthService authService = new AuthService(panelPass, new java.util.HashSet<>(staffList));

        // DB config
        String host = getConfig().getString("mysql.host", "127.0.0.1");
        int port = getConfig().getInt("mysql.port", 3306);
        String databaseName = getConfig().getString("mysql.database", "linuxac");
        String user = getConfig().getString("mysql.user", "root");
        String password = getConfig().getString("mysql.password", "");
        boolean ssl = getConfig().getBoolean("mysql.ssl", false);

        try {
            this.database = new Database(host, port, databaseName, user, password, ssl);
            this.logsRepository = new LogsRepository(database.getDataSource());
            this.logsRepository.init();
        } catch (Exception ex) {
            getLogger().severe("DB init error: " + ex.getMessage());
        }

        this.webhookService = new WebhookService(webhookUrl);
        this.logService = new LogService(this, logsRepository, webhookService);

        // Checks config
        ChecksConfig checksCfg = new ChecksConfig(getConfig());

        this.webServer = new WebServer(webPort, authService);
        try {
            this.webServer.start();
            getLogger().info("Web panel started on port " + webPort);
        } catch (Exception e) {
            getLogger().severe("Failed to start web panel: " + e.getMessage());
        }

        // Boot log
        logService.log(new CheatLog("Server", "BOOT", "INFO", System.currentTimeMillis(), "Linux Anticheat iniciado"));

        // Register all detections centrally
        new CheckRegistrar(this, logService, checksCfg).registerAll();

        // Messages
        BanMessages banMessages = new BanMessages(getConfig());

        // Commands
        if (getCommand("sban") != null) getCommand("sban").setExecutor(new SbanCommand(this, banMessages));
        if (getCommand("tban") != null) getCommand("tban").setExecutor(new TbanCommand(banMessages));
        if (getCommand("unban") != null) getCommand("unban").setExecutor(new UnbanCommand());
        if (getCommand("logs") != null) getCommand("logs").setExecutor(new LogsCommand(this, logsRepository));
        if (getCommand("kick") != null) getCommand("kick").setExecutor(new KickCommand(this, banMessages));

        // Ban listener to keep kick messages consistent on login attempts
        Bukkit.getPluginManager().registerEvents(new BanListener(banMessages), this);
    }

    @Override
    public void onDisable() {
        if (this.webServer != null) {
            try {
                this.webServer.stop();
            } catch (Exception e) {
                getLogger().warning("Error stopping web panel: " + e.getMessage());
            }
        }
        if (this.database != null) {
            try { this.database.close(); } catch (Exception ignored) {}
        }
    }
}
