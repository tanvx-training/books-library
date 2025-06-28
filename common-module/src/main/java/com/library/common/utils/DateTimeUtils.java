package com.library.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Utility class for date and time operations.
 * Provides methods for common date and time operations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtils {

    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Converts a LocalDateTime to a Date.
     *
     * @param localDateTime the LocalDateTime to convert
     * @return the converted Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * Converts a Date to a LocalDateTime.
     *
     * @param date the Date to convert
     * @return the converted LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE_ID);
    }

    /**
     * Converts a LocalDate to a Date.
     *
     * @param localDate the LocalDate to convert
     * @return the converted Date
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * Converts a Date to a LocalDate.
     *
     * @param date the Date to convert
     * @return the converted LocalDate
     */
    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDate();
    }

    /**
     * Formats a LocalDateTime to a string using the default format.
     *
     * @param localDateTime the LocalDateTime to format
     * @return the formatted string
     */
    public static String formatDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT));
    }

    /**
     * Formats a LocalDateTime to a string using the specified format.
     *
     * @param localDateTime the LocalDateTime to format
     * @param pattern the format pattern
     * @return the formatted string
     */
    public static String formatDateTime(LocalDateTime localDateTime, String pattern) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Formats a LocalDate to a string using the default format.
     *
     * @param localDate the LocalDate to format
     * @return the formatted string
     */
    public static String formatDate(LocalDate localDate) {
        return formatDate(localDate, DEFAULT_DATE_FORMAT);
    }

    /**
     * Formats a LocalDate to a string using the specified format.
     *
     * @param localDate the LocalDate to format
     * @param pattern the format pattern
     * @return the formatted string
     */
    public static String formatDate(LocalDate localDate, String pattern) {
        if (localDate == null) {
            return null;
        }
        return localDate.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Parses a string to a LocalDateTime using the default format.
     *
     * @param dateTimeString the string to parse
     * @return the parsed LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        return parseDateTime(dateTimeString, DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * Parses a string to a LocalDateTime using the specified format.
     *
     * @param dateTimeString the string to parse
     * @param pattern the format pattern
     * @return the parsed LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeString, String pattern) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Parses a string to a LocalDate using the default format.
     *
     * @param dateString the string to parse
     * @return the parsed LocalDate
     */
    public static LocalDate parseDate(String dateString) {
        return parseDate(dateString, DEFAULT_DATE_FORMAT);
    }

    /**
     * Parses a string to a LocalDate using the specified format.
     *
     * @param dateString the string to parse
     * @param pattern the format pattern
     * @return the parsed LocalDate
     */
    public static LocalDate parseDate(String dateString, String pattern) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Gets the current date and time.
     *
     * @return the current LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Gets the current date.
     *
     * @return the current LocalDate
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Calculates the difference between two LocalDateTime objects in the specified unit.
     *
     * @param start the start date and time
     * @param end the end date and time
     * @param unit the unit to measure the difference in
     * @return the difference in the specified unit
     */
    public static long between(LocalDateTime start, LocalDateTime end, ChronoUnit unit) {
        return unit.between(start, end);
    }

    /**
     * Calculates the difference between two LocalDate objects in the specified unit.
     *
     * @param start the start date
     * @param end the end date
     * @param unit the unit to measure the difference in
     * @return the difference in the specified unit
     */
    public static long between(LocalDate start, LocalDate end, ChronoUnit unit) {
        return unit.between(start, end);
    }

    /**
     * Converts a timestamp (milliseconds since epoch) to a LocalDateTime.
     *
     * @param timestamp the timestamp in milliseconds
     * @return the converted LocalDateTime
     */
    public static LocalDateTime fromTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), DEFAULT_ZONE_ID);
    }

    /**
     * Converts a LocalDateTime to a timestamp (milliseconds since epoch).
     *
     * @param localDateTime the LocalDateTime to convert
     * @return the timestamp in milliseconds
     */
    public static long toTimestamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    /**
     * Converts a LocalDateTime to a ZonedDateTime in the default time zone.
     *
     * @param localDateTime the LocalDateTime to convert
     * @return the converted ZonedDateTime
     */
    public static ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
        return toZonedDateTime(localDateTime, DEFAULT_ZONE_ID);
    }

    /**
     * Converts a LocalDateTime to a ZonedDateTime in the specified time zone.
     *
     * @param localDateTime the LocalDateTime to convert
     * @param zoneId the time zone
     * @return the converted ZonedDateTime
     */
    public static ZonedDateTime toZonedDateTime(LocalDateTime localDateTime, ZoneId zoneId) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(zoneId);
    }
} 