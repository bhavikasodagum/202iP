package com.logparser.logs;

import java.time.LocalDateTime;

public class ApplicationLogEntry implements LogEntry {
    private LocalDateTime timestamp; // Log timestamp
    private String level;            // Log level (e.g., INFO, ERROR, WARNING, DEBUG)
    private String message;          // Log message content
    private String host;             // Hostname where the log originated

    // Constructor
    public ApplicationLogEntry(LocalDateTime timestamp, String level, String message, String host) {
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
        this.host = host;
    }

    // Getters
    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String getHost() {
        return host;
    }

    // Override toString for better debugging and logging
    @Override
    public String toString() {
        return "ApplicationLogEntry{" +
                "timestamp=" + timestamp +
                ", level='" + level + '\'' +
                ", message='" + message + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}
