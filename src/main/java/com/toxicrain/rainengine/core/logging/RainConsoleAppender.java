package com.toxicrain.rainengine.core.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RainConsoleAppender extends AppenderBase<ILoggingEvent> {
    public static class LogEntry {
        public final Level level;
        public final String message;

        public LogEntry(Level level, String message) {
            this.level = level;
            this.message = message;
        }
    }
    @Getter
    private static final List<LogEntry> logLines = new CopyOnWriteArrayList<>();

    @Override
    protected void append(ILoggingEvent eventObject) {
        logLines.add(new LogEntry(eventObject.getLevel(), eventObject.getFormattedMessage()));
        if (logLines.size() > 1000) logLines.remove(0);
    }
}
