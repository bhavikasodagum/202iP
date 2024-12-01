/*package com.logparser;

import com.logparser.logs.APMLogEntry;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class APMLogEntryTest {

    @Test
    void testValidInstantiation() {
        LocalDateTime timestamp = LocalDateTime.now();
        APMLogEntry log = new APMLogEntry(timestamp, "cpu_usage_percent", 75.0, "webserver1");

        assertNotNull(log);
        assertEquals(timestamp, log.getTimestamp());
        assertEquals("cpu_usage_percent", log.getMetric());
        assertEquals(75.0, log.getValue());
        assertEquals("webserver1", log.getHost());
    }

    @Test
    public void testInvalidValue() {
        LocalDateTime timestamp = LocalDateTime.now();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new APMLogEntry(timestamp, "cpu_usage_percent", -10, "webserver1");
        });
        assertEquals("Metric value cannot be negative.", exception.getMessage());
    }
    

    @Test
    void testToString() {
        LocalDateTime timestamp = LocalDateTime.now();
        APMLogEntry log = new APMLogEntry(timestamp, "memory_usage_percent", 50.5, "webserver2");

        String expected = "APMLogEntry{timestamp=" + timestamp +
                ", metric='memory_usage_percent', value=50.5, host='webserver2'}";
        assertEquals(expected, log.toString());
    }
}
*/

package com.logparser;

import com.logparser.logs.APMLogEntry;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class APMLogEntryTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.now();
        String metric = "cpu_usage_percent";
        double value = 75.5;
        String host = "webserver1";

        // Act
        APMLogEntry logEntry = new APMLogEntry(timestamp, metric, value, host);

        // Assert
        assertEquals(timestamp, logEntry.getTimestamp());
        assertEquals(metric, logEntry.getMetric());
        assertEquals(value, logEntry.getValue());
        assertEquals(host, logEntry.getHost());
    }

    @Test
    public void testConstructorThrowsExceptionForNegativeValue() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.now();
        String metric = "cpu_usage_percent";
        double negativeValue = -10.0;  // Invalid value
        String host = "webserver1";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new APMLogEntry(timestamp, metric, negativeValue, host);
        });
        assertEquals("Metric value cannot be negative.", exception.getMessage());
    }

    @Test
    public void testToString() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.now();
        String metric = "cpu_usage_percent";
        double value = 75.5;
        String host = "webserver1";

        // Act
        APMLogEntry logEntry = new APMLogEntry(timestamp, metric, value, host);

        // Assert
        String expectedString = "APMLogEntry{" +
                "timestamp=" + timestamp +
                ", metric='" + metric + '\'' +
                ", value=" + value +
                ", host='" + host + '\'' +
                "}";
        assertEquals(expectedString, logEntry.toString());
    }
}
