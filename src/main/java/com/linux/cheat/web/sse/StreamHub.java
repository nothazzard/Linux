package com.linux.cheat.web.sse;

import javax.servlet.AsyncContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StreamHub {
    private static final StreamHub INSTANCE = new StreamHub();
    public static StreamHub get() { return INSTANCE; }

    private final List<AsyncContext> clients = new CopyOnWriteArrayList<>();

    public void add(AsyncContext ctx) {
        ctx.setTimeout(0);
        clients.add(ctx);
    }

    public void remove(AsyncContext ctx) {
        clients.remove(ctx);
        try { ctx.complete(); } catch (Exception ignored) {}
    }

    public void broadcast(String eventName, String data) {
        String payload = (eventName != null && !eventName.isEmpty() ? "event: " + eventName + "\n" : "") +
                "data: " + data.replace("\n", " ") + "\n\n";
        for (AsyncContext ctx : clients) {
            try {
                PrintWriter w = ctx.getResponse().getWriter();
                w.write(payload);
                w.flush();
            } catch (IOException e) {
                remove(ctx);
            }
        }
    }
}
