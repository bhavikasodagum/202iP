package com.logparser.logs;

import java.time.LocalDateTime;

public interface LogEntry {
    /**
     * Gets the timestamp of the log entry.
     *
     * @return The timestamp.
     */
    LocalDateTime getTimestamp();

    /**
     * Gets the host associated with the log entry.
     *
     * @return The host name or identifier.
     */
    String getHost();
}
