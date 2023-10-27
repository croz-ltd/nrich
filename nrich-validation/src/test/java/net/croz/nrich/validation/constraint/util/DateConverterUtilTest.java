package net.croz.nrich.validation.constraint.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.chrono.JapaneseDate;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class DateConverterUtilTest {

    @MethodSource("shouldReturnTrueIfDateIsConvertedToInstantMethodSource")
    @ParameterizedTest
    void shouldReturnTrueIfDateIsConvertedToInstant(Object date) {
        //expect
        assertThat(DateConverterUtil.convertToInstant(date).getClass()).isEqualTo(Instant.class);
    }

    @Test
    void shouldThrowExceptionWhenTheDateIsNotSupportedType() {
        //given
        JapaneseDate date = JapaneseDate.now();

        //when
        Throwable thrown = catchThrowable(() -> DateConverterUtil.convertToInstant(date));

        //then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> shouldReturnTrueIfDateIsConvertedToInstantMethodSource() {
        return Stream.of(
            Arguments.of(new Date()),
            Arguments.of(Calendar.getInstance()),
            Arguments.of(Instant.now()),
            Arguments.of(LocalDate.now()),
            Arguments.of(LocalDateTime.now()),
            Arguments.of(LocalTime.now()),
            Arguments.of(MonthDay.now()),
            Arguments.of(OffsetDateTime.now()),
            Arguments.of(OffsetTime.now()),
            Arguments.of(Year.now()),
            Arguments.of(YearMonth.now()),
            Arguments.of(ZonedDateTime.now())
        );
    }
}
