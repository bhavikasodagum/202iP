package com.logparser;
import com.logparser.logs.RequestLogEntry;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RequestLogEntryTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.now();
        String requestMethod = "POST";
        String requestUrl = "/api/status";
        int responseStatus = 200;
        int responseTimeMs = 150;
        String host = "webserver1";

        // Act
        RequestLogEntry logEntry = new RequestLogEntry(timestamp, requestMethod, requestUrl, responseStatus, responseTimeMs, host);

        // Assert
        assertEquals(timestamp, logEntry.getTimestamp());
        assertEquals(requestMethod, logEntry.getRequestMethod());
        assertEquals(requestUrl, logEntry.getRequestUrl());
        assertEquals(responseStatus, logEntry.getResponseStatus());
        assertEquals(responseTimeMs, logEntry.getResponseTimeMs());
        assertEquals(host, logEntry.getHost());
    }

    @Test
    public void testConstructorThrowsExceptionForNegativeResponseTime() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.now();
        String requestMethod = "POST";
        String requestUrl = "/api/status";
        int responseStatus = 200;
        int negativeResponseTime = -100;  // Invalid response time
        String host = "webserver1";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new RequestLogEntry(timestamp, requestMethod, requestUrl, responseStatus, negativeResponseTime, host);
        });
        assertEquals("Response time cannot be negative.", exception.getMessage());
    }

    @Test
    public void testToString() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.now();
        String requestMethod = "POST";
        String requestUrl = "/api/status";
        int responseStatus = 200;
        int responseTimeMs = 150;
        String host = "webserver1";

        // Act
        RequestLogEntry logEntry = new RequestLogEntry(timestamp, requestMethod, requestUrl, responseStatus, responseTimeMs, host);

        // Assert
        String expectedString = "RequestLogEntry{" +
                "timestamp=" + timestamp +
                ", requestMethod='" + requestMethod + '\'' +
                ", requestUrl='" + requestUrl + '\'' +
                ", responseStatus=" + responseStatus +
                ", responseTimeMs=" + responseTimeMs +
                ", host='" + host + '\'' +
                "}";
        assertEquals(expectedString, logEntry.toString());
    }
}
