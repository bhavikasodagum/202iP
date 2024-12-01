package com.logparser.parsers;

import com.logparser.logs.RequestLogEntry;
import com.logparser.logs.LogEntry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class RequestLogParser implements LogParser {
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public LogEntry parse(String logLine) {
        if (logLine.contains("request_method=")) {
            try {
                String[] parts = logLine.split(" ");
                LocalDateTime timestamp = LocalDateTime.parse(parts[0].split("=")[1], TIMESTAMP_FORMATTER);
                String requestMethod = parts[1].split("=")[1];
                String requestUrl = parts[2].split("=")[1].replace("\"", "");
                int responseStatus = Integer.parseInt(parts[3].split("=")[1]);
                int responseTimeMs = Integer.parseInt(parts[4].split("=")[1]);
                String host = parts[5].split("=")[1];

                return new RequestLogEntry(timestamp, requestMethod, requestUrl, responseStatus, responseTimeMs, host);
            } catch (Exception e) {
                System.err.println("Failed to parse Request log line: " + logLine);
            }
        }
        return null; // Invalid or non-Request log
    }

    /**
     * Calculates percentiles using a formula-based approach.
     *
     * @param sortedValues List of sorted response times in milliseconds.
     * @param percentile   Percentile to calculate (e.g., 50, 90, 95, 99).
     * @return Calculated percentile value.
     */
    public double calculatePercentile(List<Integer> sortedValues, double percentile) {
        int n = sortedValues.size();
        if (n == 0) return 0.0;

        double rank = (percentile / 100) * (n - 1) + 1;
        int lowerIndex = (int) Math.floor(rank) - 1;
        int upperIndex = Math.min(lowerIndex + 1, n - 1);

        if (lowerIndex < 0) return sortedValues.get(0);
        if (lowerIndex == upperIndex) return sortedValues.get(lowerIndex);

        double lowerValue = sortedValues.get(lowerIndex);
        double upperValue = sortedValues.get(upperIndex);
        return lowerValue + (rank - Math.floor(rank)) * (upperValue - lowerValue);
    }
}
