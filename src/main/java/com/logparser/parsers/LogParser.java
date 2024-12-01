package com.logparser.parsers;

import com.logparser.logs.LogEntry;

/**
 * Represents a parser for a specific type of log entry.
 */
public interface LogParser {
    /**
     * Parses a single log line and returns a LogEntry object.
     * Returns null if the log line does not match the parser's type or is invalid.
     *
     * @param logLine The log line to parse.
     * @return A LogEntry object if successfully parsed, or null if invalid or not of the expected type.
     */
    LogEntry parse(String logLine);
}
