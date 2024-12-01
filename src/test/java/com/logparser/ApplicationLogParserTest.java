package com.logparser;

import com.logparser.logs.ApplicationLogEntry;
import com.logparser.parsers.ApplicationLogParser;
import com.logparser.logs.LogEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationLogParserTest {

    private ApplicationLogParser parser;

    @BeforeEach
    public void setUp() {
        parser = new ApplicationLogParser();
    }

    @Test
    public void testParseValidLogLine() {
        // Arrange
        String validLogLine = "timestamp=2023-12-01T12:00:00 level=INFO message=\"Application started\" host=webserver1";

        // Act
        LogEntry logEntry = parser.parse(validLogLine);

        // Assert
        assertNotNull(logEntry);
        assertTrue(logEntry instanceof ApplicationLogEntry);
        ApplicationLogEntry appLogEntry = (ApplicationLogEntry) logEntry;
        assertEquals(LocalDateTime.parse("2023-12-01T12:00:00"), appLogEntry.getTimestamp());
        assertEquals("INFO", appLogEntry.getLevel());
        assertEquals("Application started", appLogEntry.getMessage());
        assertEquals("webserver1", appLogEntry.getHost());
    }

    @Test
    public void testParseLogLineWithMissingKey() {
        // Arrange
        String logLineMissingHost = "timestamp=2023-12-01T12:00:00 level=INFO message=\"Application started\"";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(logLineMissingHost);
        });
        assertEquals("Missing key: host", exception.getMessage());
    }

    @Test
    public void testParseLogLineWithInvalidTimestamp() {
        // Arrange
        String invalidTimestampLogLine = "timestamp=invalid-timestamp level=INFO message=\"Application started\" host=webserver1";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(invalidTimestampLogLine);
        });
        assertEquals("Text 'invalid-timestamp' could not be parsed at index 0", exception.getMessage());
    }

    @Test
    public void testParseLogLineWithInvalidMessage() {
        // Arrange
        String invalidMessageLogLine = "timestamp=2023-12-01T12:00:00 level=INFO host=webserver1";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(invalidMessageLogLine);
        });
        assertEquals("Missing key: message", exception.getMessage());
    }

    @Test
    public void testParseEmptyLogLine() {
        // Arrange
        String emptyLogLine = "";

        // Act
        LogEntry logEntry = parser.parse(emptyLogLine);

        // Assert
        assertNull(logEntry); // Null should be returned for invalid log lines
    }

    @Test
    public void testParseLogLineWithInvalidLevel() {
        // Arrange
        String invalidLevelLogLine = "timestamp=2023-12-01T12:00:00 level=INVALID message=\"Invalid level test\" host=webserver1";

        // Act
        LogEntry logEntry = parser.parse(invalidLevelLogLine);

        // Assert
        assertNotNull(logEntry); // Still parsed, as there's no validation for levels
        assertTrue(logEntry instanceof ApplicationLogEntry);
        ApplicationLogEntry appLogEntry = (ApplicationLogEntry) logEntry;
        assertEquals("INVALID", appLogEntry.getLevel()); // Ensure the invalid level is preserved
    }

    @Test
    public void testParseLogLineNotApplicationLog() {
        // Arrange
        String nonApplicationLogLine = "timestamp=2023-12-01T12:00:00 metric=cpu_usage value=50.0 host=webserver1";

        // Act
        LogEntry logEntry = parser.parse(nonApplicationLogLine);

        // Assert
        assertNull(logEntry); // Parser should return null for non-Application logs
    }

    @Test
    public void testParseLogLineWithExtraFields() {
        // Arrange
        String logLineWithExtraFields = "timestamp=2023-12-01T12:00:00 level=INFO message=\"Application started\" host=webserver1 extraField=extraValue";

        // Act
        LogEntry logEntry = parser.parse(logLineWithExtraFields);

        // Assert
        assertNotNull(logEntry);
        assertTrue(logEntry instanceof ApplicationLogEntry);
        ApplicationLogEntry appLogEntry = (ApplicationLogEntry) logEntry;
        assertEquals("INFO", appLogEntry.getLevel());
        assertEquals("Application started", appLogEntry.getMessage());
        assertEquals("webserver1", appLogEntry.getHost());
    }
}
