package com.linux.cheat.logs;

public class CheatLog {
    private final String player;
    private final String check;
    private final String severity;
    private final long timestamp;
    private final String info;

    public CheatLog(String player, String check, String severity, long timestamp, String info) {
        this.player = player;
        this.check = check;
        this.severity = severity;
        this.timestamp = timestamp;
        this.info = info;
    }

    public String getPlayer() { return player; }
    public String getCheck() { return check; }
    public String getSeverity() { return severity; }
    public long getTimestamp() { return timestamp; }
    public String getInfo() { return info; }
}
