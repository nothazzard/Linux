package com.linux.cheat.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class Database {
    private final HikariDataSource ds;

    public Database(String host, int port, String database, String user, String password, boolean ssl) {
        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=UTF-8&useSSL=" + ssl + "&serverTimezone=UTC";
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(jdbcUrl);
        cfg.setUsername(user);
        cfg.setPassword(password);
        cfg.setMaximumPoolSize(5);
        cfg.setMinimumIdle(1);
        cfg.setPoolName("LinuxAC-Pool");
        this.ds = new HikariDataSource(cfg);
    }

    public DataSource getDataSource() { return ds; }

    public void close() { ds.close(); }
}
