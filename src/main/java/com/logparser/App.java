package com.logparser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.logparser.logs.*;
import com.logparser.parsers.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class App {
    // Variables to track skipped lines and file write errors
    private static int skippedLines = 0;
    private static int writeErrors = 0;

    public static void main(String[] args) {
        if (args.length != 2 || !args[0].equals("--file")) {
            System.out.println("Usage: java -jar LogParserApp.jar --file <logfile>");
            return;
        }
    
        String logFileName = args[1];
    
        // Log Parsers
        List<LogParser> parsers = Arrays.asList(
                new APMLogParser(),
                new ApplicationLogParser(),
                new RequestLogParser()
        );
    
        // Aggregation data structures
        List<APMLogEntry> apmLogs = new ArrayList<>();
        List<ApplicationLogEntry> applicationLogs = new ArrayList<>();
        List<RequestLogEntry> requestLogs = new ArrayList<>();
    
        // Process log file
        try (BufferedReader reader = new BufferedReader(new FileReader(logFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                boolean parsed = false;
                for (LogParser parser : parsers) {
                    LogEntry entry = parser.parse(line);
                    if (entry != null) {
                        if (entry instanceof APMLogEntry) {
                            apmLogs.add((APMLogEntry) entry);
                        } else if (entry instanceof ApplicationLogEntry) {
                            applicationLogs.add((ApplicationLogEntry) entry);
                        } else if (entry instanceof RequestLogEntry) {
                            requestLogs.add((RequestLogEntry) entry);
                        }
                        parsed = true;
                        break; // Move to next log line
                    }
                }
                if (!parsed) {
                    System.err.println("Skipped invalid log line: " + line);
                    skippedLines++; // Increment skipped line count
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
            return;
        }
    
        if (!apmLogs.isEmpty()) {
            writeAggregatedAPMLogs(apmLogs, "apm.json");
        } else {
            createEmptyFile("apm.json");
        }
        
        if (!applicationLogs.isEmpty()) {
            writeAggregatedApplicationLogs(applicationLogs, "application.json");
        } else {
            createEmptyFile("application.json");
        }
        
        if (!requestLogs.isEmpty()) {
            writeAggregatedRequestLogs(requestLogs, "request.json");
        } else {
            createEmptyFile("request.json");
        }
        
    
        // Print summary report
        System.out.println("Summary Report:");
        System.out.println("Total skipped log lines: " + skippedLines);
        System.out.println("Total file write errors: " + writeErrors);
    }
    
    public static void createEmptyFile(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write nothing, just create an empty file
        } catch (IOException e) {
            System.err.println("Error creating empty file: " + fileName + " - " + e.getMessage());
            writeErrors++;
        }
    }
    
    /**
     * Aggregates and writes APM logs to JSON.
     */
    public static void writeAggregatedAPMLogs(List<APMLogEntry> apmLogs, String fileName) {
        //if (apmLogs.isEmpty()) return;
        if (apmLogs.isEmpty()) {
            System.out.println("No APM logs to write.");
            return;
        }
        Map<String, Map<String, Object>> apmAggregations = apmLogs.stream()
                .collect(Collectors.groupingBy(
                        APMLogEntry::getMetric,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                logs -> {
                                    List<Double> values = logs.stream()
                                            .map(APMLogEntry::getValue)
                                            .sorted()
                                            .collect(Collectors.toList());

                                    Map<String, Object> stats = new LinkedHashMap<>();
                                    stats.put("minimum", formatNumber(values.get(0)));
                                    stats.put("median", formatToOneDecimal(calculatePercentile(values, 50)));
                                    stats.put("average", formatToOneDecimal(values.stream().mapToDouble(v -> v).average().orElse(0.0)));
                                    stats.put("max", formatNumber(values.get(values.size() - 1)));

                                    return stats;
                                }
                        )
                ));

        writeToJson(apmAggregations, fileName);
    }

    /**
     * Aggregates and writes Application logs to JSON.
     */
    public static void writeAggregatedApplicationLogs(List<ApplicationLogEntry> applicationLogs, String fileName) {
        //if (applicationLogs.isEmpty()) return;
        if (applicationLogs.isEmpty()) {
            System.out.println("No Application logs to write.");
            return;
        }

        Map<String, Long> severityCounts = applicationLogs.stream()
                .collect(Collectors.groupingBy(ApplicationLogEntry::getLevel, Collectors.counting()));

        writeToJson(severityCounts, fileName);
    }

    /**
     * Aggregates and writes Request logs to JSON.
     */
    public static void writeAggregatedRequestLogs(List<RequestLogEntry> requestLogs, String fileName) {
       // if (requestLogs.isEmpty()) return;
        if (requestLogs.isEmpty()) {
            System.out.println("No Request logs to write.");
            return;
        }
        Map<String, Map<String, Object>> requestAggregations = requestLogs.stream()
                .collect(Collectors.groupingBy(
                        RequestLogEntry::getRequestUrl,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                logs -> {
                                    List<Integer> responseTimes = logs.stream()
                                            .map(RequestLogEntry::getResponseTimeMs)
                                            .sorted()
                                            .collect(Collectors.toList());

                                    Map<String, Object> responseTimeStats = new LinkedHashMap<>();
                                    responseTimeStats.put("min", formatNumber(responseTimes.get(0)));
                                    responseTimeStats.put("50_percentile", formatToOneDecimal(calculatePercentile(responseTimes, 50)));
                                    responseTimeStats.put("90_percentile", formatToOneDecimal(calculatePercentile(responseTimes, 90)));
                                    responseTimeStats.put("95_percentile", formatToOneDecimal(calculatePercentile(responseTimes, 95)));
                                    responseTimeStats.put("99_percentile", calculatePercentile(responseTimes, 99));
                                    responseTimeStats.put("max", formatNumber(responseTimes.get(responseTimes.size() - 1)));

                                    Map<String, Long> statusCodeCounts = logs.stream()
                                            .collect(Collectors.groupingBy(
                                                    log -> log.getResponseStatus() / 100 + "XX",
                                                    Collectors.counting()
                                            ));

                                    // Ensure all status codes (2XX, 4XX, 5XX) are present
                                    statusCodeCounts.putIfAbsent("2XX", 0L);
                                    statusCodeCounts.putIfAbsent("4XX", 0L);
                                    statusCodeCounts.putIfAbsent("5XX", 0L);

                                    // Add response times first, then status codes
                                    Map<String, Object> result = new LinkedHashMap<>();
                                    result.put("response_times", responseTimeStats);
                                    result.put("status_codes", statusCodeCounts);

                                    return result;
                                }
                        )
                ));

        writeToJson(requestAggregations, fileName);
    }

    /**
     * Calculates a specific percentile from a sorted list using a formula-based approach.
     */
    public static double calculatePercentile(List<? extends Number> sortedValues, double percentile) {
        int n = sortedValues.size();
        if (n == 0) return 0.0;

        double rank = (percentile / 100) * (n - 1) + 1;
        int lowerIndex = (int) Math.floor(rank) - 1;
        int upperIndex = Math.min(lowerIndex + 1, n - 1);

        if (lowerIndex < 0) return sortedValues.get(0).doubleValue();
        if (lowerIndex == upperIndex) return sortedValues.get(lowerIndex).doubleValue();

        double lowerValue = sortedValues.get(lowerIndex).doubleValue();
        double upperValue = sortedValues.get(upperIndex).doubleValue();
        return lowerValue + (rank - Math.floor(rank)) * (upperValue - lowerValue);
    }

    /**
     * Formats a number to one decimal place.
     */
    public static double formatToOneDecimal(double value) {
        BigDecimal roundedValue = BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP);
        return roundedValue.doubleValue();
    }

    /**
     * Rounds a number and formats it as an integer or double based on its value.
     */
    public static Object formatNumber(double value) {
        if (value == (long) value) {
            return (long) value; // Return as integer if no decimal part
        } else {
            BigDecimal roundedValue = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
            return roundedValue.doubleValue(); // Return rounded double with two decimal places
        }
    }

    /**
     * Writes any object to JSON using Jackson.
     */
    public static void writeToJson(Object data, String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            mapper.writeValue(new File(fileName), data);
            System.out.println("Logs written to " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing to file " + fileName + ": " + e.getMessage());
            writeErrors++; // Increment write error count
        }
    }
}
