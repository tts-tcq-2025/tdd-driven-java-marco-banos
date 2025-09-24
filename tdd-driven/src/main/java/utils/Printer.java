package src.main.java.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class to print messages in a consistent way.
 * This avoids direct usage of System.out.println in code/tests.
 */
public class Printer {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void info(String message) {
        log("INFO", message);
    }

    public static void success(String message) {
        log("SUCCESS", message);
    }

    public static void error(String message) {
        log("ERROR", message);
    }

    private static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("[%s] %-7s %s%n", timestamp, level, message);
    }
}
