package net.croz.nrich.search.converter.testutil;

import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class ConverterGeneratingUtil {

    private ConverterGeneratingUtil() {
    }

    @SneakyThrows
    public static Date dateOf(String value) {
        return new SimpleDateFormat("dd.MM.yyyy").parse(value);
    }

    public static LocalDate localDateOf(String value) {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy").parse(value, LocalDate::from);
    }

    public static LocalDateTime localDateTimeOf(String value) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm").parse(value, LocalDateTime::from);
    }
}
