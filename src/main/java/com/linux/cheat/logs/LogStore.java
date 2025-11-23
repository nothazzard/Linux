package com.linux.cheat.logs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogStore {
    private static final LogStore INSTANCE = new LogStore();
    public static LogStore get() { return INSTANCE; }

    private final List<CheatLog> logs = Collections.synchronizedList(new ArrayList<>());

    public void add(CheatLog log) {
        logs.add(log);
        if (logs.size() > 2000) {
            // manter tamanho razoável na memória
            logs.remove(0);
        }
    }

    public List<CheatLog> all() {
        return new ArrayList<>(logs);
    }

    public void purgeByPlayer(String player) {
        if (player == null) return;
        synchronized (logs) {
            logs.removeIf(l -> player.equalsIgnoreCase(l.getPlayer()));
        }
    }
}
