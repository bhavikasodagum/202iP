package com.logparser;

import com.logparser.logs.APMLogEntry;
import com.logparser.parsers.APMLogParser;
import com.logparser.logs.LogEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class APMLogParserTest {

    private APMLogParser parser;

    @BeforeEach
    public void setUp() {
        parser = new APMLogParser();
    }

    @Test
    public void testParseValidLogLine() {
        // Arrange
        String validLogLine = "timestamp=2023-12-01T12:00:00 metric=cpu_usage value=75.5 host=webserver1";

        // Act
        LogEntry logEntry = parser.parse(validLogLine);

        // Assert
        assertNotNull(logEntry);
        assertTrue(logEntry instanceof APMLogEntry);
        APMLogEntry apmLogEntry = (APMLogEntry) logEntry;
        assertEquals("cpu_usage", apmLogEntry.getMetric());
        assertEquals(75.5, apmLogEntry.getValue());
        assertEquals("webserver1", apmLogEntry.getHost());
    }

    @Test
    public void testParseLogLineWithInvalidTimestamp() {
        // Arrange
        String invalidTimestampLogLine = "timestamp=2023-12-01T12:00:00-invalid metric=cpu_usage value=75.5 host=webserver1";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(invalidTimestampLogLine);
        });
        assertEquals("Invalid timestamp format.", exception.getMessage());
    }

    /*@Test
    public void testParseLogLineWithMissingKey() {
        // Arrange
        String missingKeyLogLine = "timestamp=2023-12-01T12:00:00 value=75.5 host=webserver1";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(missingKeyLogLine);  // Missing "metric="
        });
        assertEquals("Missing key: metric", exception.getMessage());
    }*/
    @Test
public void testParseLogLineWithMissingKey() {
    String logLine = "timestamp=2024-11-30T12:00:00 metric=cpu_usage value=50.0"; // No host key

    try {
        APMLogParser parser = new APMLogParser();
        parser.parse(logLine);  // This should throw an exception
        fail("Expected IllegalArgumentException due to missing 'host' key.");
    } catch (IllegalArgumentException e) {
        assertEquals("Missing key: host", e.getMessage()); // Assert the exception message
    }
}


    @Test
    public void testParseLogLineWithNegativeMetricValue() {
        // Arrange
        String negativeMetricValueLogLine = "timestamp=2023-12-01T12:00:00 metric=cpu_usage value=-75.5 host=webserver1";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(negativeMetricValueLogLine);  // Negative metric value
        });
        assertEquals("Metric value cannot be negative.", exception.getMessage());
    }

    @Test
    public void testParseLogLineWithNonNumericMetricValue() {
        // Arrange
        String nonNumericValueLogLine = "timestamp=2023-12-01T12:00:00 metric=cpu_usage value=nonNumericValue host=webserver1";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(nonNumericValueLogLine);  // Non-numeric metric value
        });
        assertEquals("For input string: \"nonNumericValue\"", exception.getMessage());
    }

    @Test
    public void testParseLogLineWithMissingHost() {
        // Arrange
        String missingHostLogLine = "timestamp=2023-12-01T12:00:00 metric=cpu_usage value=75.5";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(missingHostLogLine);  // Missing "host="
        });
        assertEquals("Missing key: host", exception.getMessage());
    }

    @Test
    public void testParseInvalidLogLine() {
        // Arrange
        String invalidLogLine = "timestamp=2023-12-01T12:00:00 invalid_key=some_value host=webserver1";

        // Act
        LogEntry logEntry = parser.parse(invalidLogLine);

        // Assert
        assertNull(logEntry);  // The parser should return null for an invalid log line
    }

    @Test
    public void testParseEmptyLogLine() {
        // Arrange
        String emptyLogLine = "";

        // Act
        LogEntry logEntry = parser.parse(emptyLogLine);

        // Assert
        assertNull(logEntry);  // The parser should return null for an empty log line
    }
}
