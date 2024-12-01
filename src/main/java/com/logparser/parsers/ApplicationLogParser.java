package com.logparser.parsers;

import com.logparser.logs.ApplicationLogEntry;
import com.logparser.logs.LogEntry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ApplicationLogParser implements LogParser {
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public LogEntry parse(String logLine) {
        if (logLine.contains("level=")) {
            try {
                String[] parts = logLine.split(" ");

                // Parse required fields
                LocalDateTime timestamp = extractTimestamp(parts[0]);
                String level = extractValue(parts, "level");
                String message = extractMessage(logLine);
                String host = extractValue(parts, "host");

                return new ApplicationLogEntry(timestamp, level, message, host);
            } catch (IllegalArgumentException e) {
                throw e; // Re-throw expected exceptions
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to parse Application log line: " + logLine, e);
            }
        }
        return null;
    }

   /*  private LocalDateTime extractTimestamp(String part) {
        try {
            return LocalDateTime.parse(part.split("=")[1], TIMESTAMP_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid timestamp format.");
        }
    } */
    private LocalDateTime extractTimestamp(String part) {
        try {
            return LocalDateTime.parse(part.split("=")[1], TIMESTAMP_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e); // Propagate the original exception message
        }
    }
    

    private String extractValue(String[] parts, String key) {
        for (String part : parts) {
            if (part.startsWith(key + "=")) {
                return part.split("=")[1];
            }
        }
        throw new IllegalArgumentException("Missing key: " + key);
    }

    private String extractMessage(String logLine) {
        int messageIndex = logLine.indexOf("message=");
        if (messageIndex != -1) {
            try {
                return logLine.substring(messageIndex + 8).split(" host=")[0].replace("\"", "").trim();
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid message format.");
            }
        }
        throw new IllegalArgumentException("Missing key: message");
    }
}
