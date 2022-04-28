/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
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

    private static final String DATE_PATTERN = "dd.MM.yyyy.";

    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy.'T'HH:mm";

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
