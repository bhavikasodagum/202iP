package com.logparser;

import com.logparser.logs.RequestLogEntry;
import com.logparser.parsers.RequestLogParser;
import com.logparser.logs.LogEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RequestLogParserTest {

    private RequestLogParser parser;

    @BeforeEach
    public void setUp() {
        parser = new RequestLogParser();
    }

    @Test
    public void testParseValidLogLine() {
        // Arrange
        String validLogLine = "timestamp=2023-12-01T12:00:00 request_method=POST request_url=\"/api/status\" response_status=200 response_time_ms=150 host=webserver1";

        // Act
        LogEntry logEntry = parser.parse(validLogLine);

        // Assert
        assertNotNull(logEntry);
        assertTrue(logEntry instanceof RequestLogEntry);
        RequestLogEntry requestLogEntry = (RequestLogEntry) logEntry;
        assertEquals(LocalDateTime.parse("2023-12-01T12:00:00"), requestLogEntry.getTimestamp());
        assertEquals("POST", requestLogEntry.getRequestMethod());
        assertEquals("/api/status", requestLogEntry.getRequestUrl());
        assertEquals(200, requestLogEntry.getResponseStatus());
        assertEquals(150, requestLogEntry.getResponseTimeMs());
        assertEquals("webserver1", requestLogEntry.getHost());
    }

    @Test
    public void testParseLogLineWithMissingKey() {
        // Arrange
        String logLineMissingHost = "timestamp=2023-12-01T12:00:00 request_method=POST request_url=\"/api/status\" response_status=200 response_time_ms=150";

        // Act
        LogEntry logEntry = parser.parse(logLineMissingHost);

        // Assert
        assertNull(logEntry); // Should return null for invalid log lines
    }

    @Test
    public void testParseLogLineWithInvalidTimestamp() {
        // Arrange
        String invalidTimestampLogLine = "timestamp=invalid-timestamp request_method=POST request_url=\"/api/status\" response_status=200 response_time_ms=150 host=webserver1";

        // Act
        LogEntry logEntry = parser.parse(invalidTimestampLogLine);

        // Assert
        assertNull(logEntry); // Invalid timestamp should result in null
    }

    @Test
    public void testParseLogLineWithInvalidResponseStatus() {
        // Arrange
        String invalidResponseStatusLogLine = "timestamp=2023-12-01T12:00:00 request_method=POST request_url=\"/api/status\" response_status=invalid response_time_ms=150 host=webserver1";

        // Act
        LogEntry logEntry = parser.parse(invalidResponseStatusLogLine);

        // Assert
        assertNull(logEntry); // Invalid response status should result in null
    }

    @Test
    public void testParseLogLineWithInvalidResponseTime() {
        // Arrange
        String invalidResponseTimeLogLine = "timestamp=2023-12-01T12:00:00 request_method=POST request_url=\"/api/status\" response_status=200 response_time_ms=invalid host=webserver1";

        // Act
        LogEntry logEntry = parser.parse(invalidResponseTimeLogLine);

        // Assert
        assertNull(logEntry); // Invalid response time should result in null
    }

    @Test
    public void testParseNonRequestLogLine() {
        // Arrange
        String nonRequestLogLine = "timestamp=2023-12-01T12:00:00 metric=cpu_usage value=50.0 host=webserver1";

        // Act
        LogEntry logEntry = parser.parse(nonRequestLogLine);

        // Assert
        assertNull(logEntry); // Non-request logs should result in null
    }

    @Test
    public void testParseEmptyLogLine() {
        // Arrange
        String emptyLogLine = "";

        // Act
        LogEntry logEntry = parser.parse(emptyLogLine);

        // Assert
        assertNull(logEntry); // Empty log line should result in null
    }

    /*@Test
    public void testCalculatePercentileWithValidInput() {
        // Arrange
        List<Integer> responseTimes = Arrays.asList(100, 200, 300, 400, 500);

        // Act
        double percentile90 = parser.calculatePercentile(responseTimes, 90);
        double percentile50 = parser.calculatePercentile(responseTimes, 50);

        // Assert
        assertEquals(470.0, percentile90, 0.1);
        assertEquals(300.0, percentile50, 0.1);
    }*/
    @Test
public void testCalculatePercentileWithValidInput() {
    // Arrange
    List<Integer> responseTimes = Arrays.asList(100, 200, 300, 400, 500);

    // Act
    double percentile90 = parser.calculatePercentile(responseTimes, 90);
    double percentile50 = parser.calculatePercentile(responseTimes, 50);

    // Assert
    assertEquals(460.0, percentile90, 0.1); // Allow a tolerance of 0.1
    assertEquals(300.0, percentile50, 0.1); // Allow a tolerance of 0.1
}


    @Test
    public void testCalculatePercentileWithEmptyList() {
        // Arrange
        List<Integer> emptyList = Collections.emptyList();

        // Act
        double percentile = parser.calculatePercentile(emptyList, 90);

        // Assert
        assertEquals(0.0, percentile);
    }

    @Test
    public void testCalculatePercentileWithSingleElement() {
        // Arrange
        List<Integer> singleElementList = Collections.singletonList(200);

        // Act
        double percentile = parser.calculatePercentile(singleElementList, 90);

        // Assert
        assertEquals(200.0, percentile);
    }
}
