package com.linux.cheat.db;

import com.linux.cheat.logs.CheatLog;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LogsRepository {
    private final DataSource ds;

    public LogsRepository(DataSource ds) {
        this.ds = ds;
    }

    public void init() throws Exception {
        try (Connection c = ds.getConnection();
             Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS linuxac_logs (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                    "player VARCHAR(32) NOT NULL, " +
                    "check_name VARCHAR(64) NOT NULL, " +
                    "severity VARCHAR(16) NOT NULL, " +
                    "timestamp BIGINT NOT NULL, " +
                    "info TEXT NOT NULL" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
        }
    }

    public void deleteByPlayer(String player) throws Exception {
        String sql = "DELETE FROM linuxac_logs WHERE player = ?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, player);
            ps.executeUpdate();
        }
    }

    public void insert(CheatLog log) throws Exception {
        String sql = "INSERT INTO linuxac_logs (player, check_name, severity, timestamp, info) VALUES (?,?,?,?,?)";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, log.getPlayer());
            ps.setString(2, log.getCheck());
            ps.setString(3, log.getSeverity());
            ps.setLong(4, log.getTimestamp());
            ps.setString(5, log.getInfo());
            ps.executeUpdate();
        }
    }

    public List<CheatLog> latest(int limit) throws Exception {
        String sql = "SELECT player, check_name, severity, timestamp, info FROM linuxac_logs ORDER BY id DESC LIMIT ?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                List<CheatLog> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new CheatLog(
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getLong(4),
                            rs.getString(5)
                    ));
                }
                return out;
            }
        }
    }

    public List<CheatLog> latestByPlayer(String player, int limit) throws Exception {
        String sql = "SELECT player, check_name, severity, timestamp, info FROM linuxac_logs WHERE player = ? ORDER BY id DESC LIMIT ?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, player);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                List<CheatLog> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new CheatLog(
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getLong(4),
                            rs.getString(5)
                    ));
                }
                return out;
            }
        }
    }
}
