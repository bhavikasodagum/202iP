package com.logparser;

import com.logparser.logs.ApplicationLogEntry;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationLogEntryTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.now();
        String level = "INFO";
        String message = "Application started successfully.";
        String host = "webserver1";

        // Act
        ApplicationLogEntry logEntry = new ApplicationLogEntry(timestamp, level, message, host);

        // Assert
        assertEquals(timestamp, logEntry.getTimestamp());
        assertEquals(level, logEntry.getLevel());
        assertEquals(message, logEntry.getMessage());
        assertEquals(host, logEntry.getHost());
    }

    @Test
    public void testToString() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.now();
        String level = "INFO";
        String message = "Application started successfully.";
        String host = "webserver1";

        // Act
        ApplicationLogEntry logEntry = new ApplicationLogEntry(timestamp, level, message, host);

        // Assert
        String expectedString = "ApplicationLogEntry{" +
                "timestamp=" + timestamp +
                ", level='" + level + '\'' +
                ", message='" + message + '\'' +
                ", host='" + host + '\'' +
                "}";
        assertEquals(expectedString, logEntry.toString());
    }
}
