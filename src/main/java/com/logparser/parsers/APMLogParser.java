package com.logparser.parsers;

import com.logparser.logs.APMLogEntry;
import com.logparser.logs.LogEntry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class APMLogParser implements LogParser {
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public LogEntry parse(String logLine) {
        // Check if the log line contains "metric=" to identify an APM log
        if (logLine.contains("metric=")) {
            try {
                // Split the log line into key-value pairs
                String[] parts = logLine.split(" ");

                // Parse required fields
                LocalDateTime timestamp = extractTimestamp(parts[0]);
                String metric = extractValue(parts, "metric");
                double value = parseValue(extractValue(parts, "value"));
                String host = extractValue(parts, "host");

                // Return a new APMLogEntry object
                return new APMLogEntry(timestamp, metric, value, host);
            } catch (IllegalArgumentException e) {
                // Log the exception for debugging purposes
                System.err.println("Error parsing log line: " + e.getMessage());
                throw e;
            } catch (Exception e) {
                // If any other exception occurs, we throw it with a generic message
                throw new IllegalArgumentException("Failed to parse log line: " + logLine, e);
            }
        }
        return null; // Return null if the log line is not an APM log
    }

    /**
     * Extracts the timestamp from a key-value pair.
     *
     * @param part The key-value pair containing the timestamp.
     * @return A LocalDateTime object representing the timestamp.
     */
    private LocalDateTime extractTimestamp(String part) {
        try {
            return LocalDateTime.parse(part.split("=")[1], TIMESTAMP_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid timestamp format.");
        }
    }

    /**
     * Extracts the value for a given key from the log parts array.
     *
     * @param parts The array of key-value pairs.
     * @param key   The key to search for.
     * @return The value associated with the key.
     * @throws IllegalArgumentException If the key is missing.
     */
    private String extractValue(String[] parts, String key) {
        for (String part : parts) {
            if (part.startsWith(key + "=")) {
                return part.split("=")[1];
            }
        }
        // Add logging here to confirm that we are hitting this code path
        System.err.println("Key missing: " + key);
        throw new IllegalArgumentException("Missing key: " + key);  // This should match the test case
    }

    /**
     * Parses the value and checks if it's a valid number.
     *
     * @param value The value to parse.
     * @return The parsed value as a double.
     */
    private double parseValue(String value) {
        try {
            double parsedValue = Double.parseDouble(value);
            if (parsedValue < 0) {
                throw new IllegalArgumentException("Metric value cannot be negative.");
            }
            return parsedValue;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("For input string: \"" + value + "\"");
        }
    }
}
