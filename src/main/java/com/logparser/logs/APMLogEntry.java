package com.logparser.logs;

import java.time.LocalDateTime;

public class APMLogEntry implements LogEntry {
    private LocalDateTime timestamp;
    private String metric; // Metric type (e.g., "cpu_usage_percent")
    private double value;  // Metric value
    private String host;   // Hostname of the machine that generated the log

    // Constructor
    public APMLogEntry(LocalDateTime timestamp, String metric, double value, String host) {
        if (value < 0) {
            throw new IllegalArgumentException("Metric value cannot be negative.");
        }
        this.timestamp = timestamp;
        this.metric = metric;
        this.value = value;
        this.host = host;
    }

    // Getters
    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMetric() {
        return metric;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String getHost() {
        return host;
    }

    // Override toString for better debugging and logging
    @Override
    public String toString() {
        return "APMLogEntry{" +
                "timestamp=" + timestamp +
                ", metric='" + metric + '\'' +
                ", value=" + value +
                ", host='" + host + '\'' +
                '}';
    }
}