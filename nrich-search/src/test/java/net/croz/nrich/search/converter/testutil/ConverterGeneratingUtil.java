package net.croz.nrich.search.converter.testutil;

import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class ConverterGeneratingUtil {

    private static final String DATE_PATTERN = "dd-MM-yyyy";

    private static final String DATE_TIME_PATTERN = "dd-MM-yyyy'T'HH:mm";

    private ConverterGeneratingUtil() {
    }

    @SneakyThrows
    public static Date dateOf(String value) {
        return new SimpleDateFormat(DATE_PATTERN).parse(value);
    }

    public static LocalDate localDateOf(String value) {
        return DateTimeFormatter.ofPattern(DATE_PATTERN).parse(value, LocalDate::from);
    }

    public static LocalDateTime localDateTimeOf(String value) {
        return DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).parse(value, LocalDateTime::from);
    }

    public static Instant instantOf(String value) {
        return DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).withZone(ZoneId.systemDefault()).parse(value, Instant::from);
    }
}
