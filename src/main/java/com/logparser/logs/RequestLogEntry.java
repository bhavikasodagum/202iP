package com.logparser.logs;

import java.time.LocalDateTime;

public class RequestLogEntry implements LogEntry {
    private LocalDateTime timestamp;
    private String requestMethod; // HTTP request method (e.g., POST, GET)
    private String requestUrl;    // URL of the request
    private int responseStatus;   // HTTP status code of the response
    private int responseTimeMs;   // Response time in milliseconds
    private String host;          // Hostname of the machine that generated the log

    // Constructor
    public RequestLogEntry(LocalDateTime timestamp, String requestMethod, String requestUrl,
                           int responseStatus, int responseTimeMs, String host) {
        if (responseTimeMs < 0) {
            throw new IllegalArgumentException("Response time cannot be negative.");
        }
        this.timestamp = timestamp;
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
        this.responseStatus = responseStatus;
        this.responseTimeMs = responseTimeMs;
        this.host = host;
    }

    // Getters
    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public int getResponseTimeMs() {
        return responseTimeMs;
    }

    @Override
    public String getHost() {
        return host;
    }

    // Override toString for better debugging and logging
    @Override
    public String toString() {
        return "RequestLogEntry{" +
                "timestamp=" + timestamp +
                ", requestMethod='" + requestMethod + '\'' +
                ", requestUrl='" + requestUrl + '\'' +
                ", responseStatus=" + responseStatus +
                ", responseTimeMs=" + responseTimeMs +
                ", host='" + host + '\'' +
                '}';
    }
}
