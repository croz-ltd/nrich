/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.validation.constraint.util;

import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class DateConverterUtil {

    private DateConverterUtil() {
    }

    private static final List<DateConverter> DATE_CONVERTER_LIST = initializeConverterList();

    public static Instant convertToInstant(Object date) {
        DateConverter dateConverter = Objects.requireNonNull(DATE_CONVERTER_LIST.stream()
            .filter(converterHolder -> converterHolder.type().isAssignableFrom(date.getClass()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported type defined for conversion: %s", ObjectUtils.nullSafeClassName(date)))));

        return dateConverter.converterFunction().apply(date);
    }

    private static List<DateConverter> initializeConverterList() {
        return List.of(
            new DateConverter(ZonedDateTime.class, value -> ((ZonedDateTime) value).toInstant()),
            new DateConverter(LocalDateTime.class, value -> ((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant()),
            new DateConverter(LocalDate.class, value -> ((LocalDate) value).atTime(LocalTime.now()).atZone(ZoneId.systemDefault()).toInstant()),
            new DateConverter(LocalTime.class, value -> ((LocalTime) value).atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant()),
            new DateConverter(OffsetTime.class, value -> ((OffsetTime) value).atDate(LocalDate.now()).toInstant()),
            new DateConverter(OffsetDateTime.class, value -> ((OffsetDateTime) value).toInstant()),
            new DateConverter(Year.class, value -> ((Year) value).atDay(1).atTime(LocalTime.now()).atZone(ZoneId.systemDefault()).toInstant()),
            new DateConverter(YearMonth.class, value -> ((YearMonth) value).atDay(1).atTime(LocalTime.now()).atZone(ZoneId.systemDefault()).toInstant()),
            new DateConverter(MonthDay.class, value -> Year.now().atMonthDay(((MonthDay) value)).atTime(LocalTime.now()).atZone(ZoneId.systemDefault()).toInstant()),
            new DateConverter(Date.class, value -> ((Date) value).toInstant()),
            new DateConverter(Calendar.class, value -> ((Calendar) value).toInstant()),
            new DateConverter(Instant.class, Instant.class::cast)
        );
    }

    public record DateConverter(Class<?> type, Function<Object, Instant> converterFunction) {

    }
}
