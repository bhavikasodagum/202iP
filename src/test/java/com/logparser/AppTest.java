package com.logparser;

import com.logparser.logs.APMLogEntry;
import com.logparser.logs.ApplicationLogEntry;
import com.logparser.logs.RequestLogEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    private final String testLogFile = "test-log.txt";
    private final String apmOutputFile = "apm.json";
    private final String applicationOutputFile = "application.json";
    private final String requestOutputFile = "request.json";

    @BeforeEach
    public void setUp() throws IOException {
        List<String> logLines = Arrays.asList(
                "timestamp=2023-12-01T12:00:00 metric=cpu_usage value=50.0 host=webserver1",
                "timestamp=2023-12-01T12:01:00 level=INFO message=\"Application started\" host=webserver1",
                "timestamp=2023-12-01T12:02:00 request_method=POST request_url=\"/api/update\" response_status=200 response_time_ms=150 host=webserver1",
                "invalid log line"
        );
        Files.write(Paths.get(testLogFile), logLines);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(testLogFile));
        Files.deleteIfExists(Paths.get(apmOutputFile));
        Files.deleteIfExists(Paths.get(applicationOutputFile));
        Files.deleteIfExists(Paths.get(requestOutputFile));
    }

    @Test
    public void testMainWithValidInput() {
        String[] args = {"--file", testLogFile};
        App.main(args);
        assertTrue(Files.exists(Paths.get(apmOutputFile)));
        assertTrue(Files.exists(Paths.get(applicationOutputFile)));
        assertTrue(Files.exists(Paths.get(requestOutputFile)));
    }

    @Test
    public void testMainWithInvalidArguments() {
        String[] args = {"--invalid", "test-log.txt"};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        App.main(args);
        String output = outputStream.toString();
        assertTrue(output.contains("Usage: java -jar LogParserApp.jar --file <logfile>"));
    }

    @Test
    public void testMainWithMissingFile() {
        String[] args = {"--file", "non-existent-file.txt"};
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errorStream));
        App.main(args);
        String errorOutput = errorStream.toString();
        assertTrue(errorOutput.contains("Error reading log file"));
    }

    @Test
    public void testMainWithEmptyFile() throws IOException {
        String emptyFile = "empty-log.txt";
        Files.createFile(Paths.get(emptyFile));
        String[] args = {"--file", emptyFile};
        App.main(args);
        assertTrue(Files.exists(Paths.get(apmOutputFile)));
        assertTrue(Files.exists(Paths.get(applicationOutputFile)));
        assertTrue(Files.exists(Paths.get(requestOutputFile)));
        assertEquals(0, Files.size(Paths.get(apmOutputFile)));
        assertEquals(0, Files.size(Paths.get(applicationOutputFile)));
        assertEquals(0, Files.size(Paths.get(requestOutputFile)));
        Files.deleteIfExists(Paths.get(emptyFile));
    }

    @Test
    public void testMainWithOnlyInvalidLines() throws IOException {
        String invalidLogFile = "invalid-log.txt";
        List<String> invalidLines = Collections.singletonList("invalid line");
        Files.write(Paths.get(invalidLogFile), invalidLines);
        String[] args = {"--file", invalidLogFile};
        App.main(args);
        assertTrue(Files.exists(Paths.get(apmOutputFile)));
        assertTrue(Files.exists(Paths.get(applicationOutputFile)));
        assertTrue(Files.exists(Paths.get(requestOutputFile)));
        assertEquals(0, Files.size(Paths.get(apmOutputFile)));
        assertEquals(0, Files.size(Paths.get(applicationOutputFile)));
        assertEquals(0, Files.size(Paths.get(requestOutputFile)));
        Files.deleteIfExists(Paths.get(invalidLogFile));
    }

    @Test
    public void testWriteAggregatedAPMLogsWithEmptyList() {
        App.writeAggregatedAPMLogs(new ArrayList<>(), apmOutputFile);
        assertFalse(Files.exists(Paths.get(apmOutputFile)));
    }

    @Test
    public void testWriteAggregatedApplicationLogsWithEmptyList() {
        App.writeAggregatedApplicationLogs(new ArrayList<>(), applicationOutputFile);
        assertFalse(Files.exists(Paths.get(applicationOutputFile)));
    }

    @Test
    public void testWriteAggregatedRequestLogsWithEmptyList() {
        App.writeAggregatedRequestLogs(new ArrayList<>(), requestOutputFile);
        assertFalse(Files.exists(Paths.get(requestOutputFile)));
    }

    @Test
    public void testWriteToJsonWithIOException() {
        Object data = new HashMap<>();
        String invalidFileName = "/invalid-path/output.json";
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errorStream));
        App.writeToJson(data, invalidFileName);
        String errorOutput = errorStream.toString();
        assertTrue(errorOutput.contains("Error writing to file"));
    }

    @Test
    public void testCalculatePercentileWithEmptyList() {
        double percentile = App.calculatePercentile(new ArrayList<>(), 50);
        assertEquals(0.0, percentile);
    }

    @Test
    public void testCalculatePercentileWithValidInput() {
        List<Double> values = Arrays.asList(10.0, 20.0, 30.0, 40.0, 50.0);
        double percentile = App.calculatePercentile(values, 90);
        assertEquals(46.0, percentile, 0.1);
    }

    @Test
    public void testFormatToOneDecimal() {
        assertEquals(12.3, App.formatToOneDecimal(12.345));
    }

    @Test
    public void testFormatNumber() {
        assertEquals(12L, App.formatNumber(12.0));
        assertEquals(12.35, App.formatNumber(12.345));
    }

    @Test
    public void testCreateEmptyFile() throws IOException {
        String emptyFileName = "empty.json";
        App.createEmptyFile(emptyFileName);
        assertTrue(Files.exists(Paths.get(emptyFileName)));
        assertEquals(0, Files.size(Paths.get(emptyFileName)));
        Files.deleteIfExists(Paths.get(emptyFileName));
    }
}
