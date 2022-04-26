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

package net.croz.nrich.registry.configuration.util;

import net.croz.nrich.registry.api.configuration.model.property.JavascriptType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class JavaToJavascriptTypeConversionUtilTest {

    @MethodSource("shouldConvertJavaToJavascriptTypeMethodSource")
    @ParameterizedTest
    void shouldConvertJavaToJavascriptType(Class<?> javaType, JavascriptType javascriptType) {
        // when
        JavascriptType result = JavaToJavascriptTypeConversionUtil.fromJavaType(javaType);

        // then
        assertThat(result).isEqualTo(javascriptType);
    }

    private static Stream<Arguments> shouldConvertJavaToJavascriptTypeMethodSource() {
        return Stream.of(
            arguments(Integer.class, JavascriptType.NUMBER),
            arguments(Short.class, JavascriptType.NUMBER),
            arguments(Long.class, JavascriptType.NUMBER),
            arguments(Float.class, JavascriptType.NUMBER),
            arguments(Double.class, JavascriptType.NUMBER),
            arguments(BigDecimal.class, JavascriptType.NUMBER),
            arguments(String.class, JavascriptType.STRING),
            arguments(Character.class, JavascriptType.STRING),
            arguments(Instant.class, JavascriptType.DATE),
            arguments(LocalDate.class, JavascriptType.DATE),
            arguments(LocalDateTime.class, JavascriptType.DATE),
            arguments(OffsetDateTime.class, JavascriptType.DATE),
            arguments(ZonedDateTime.class, JavascriptType.DATE),
            arguments(GregorianCalendar.class, JavascriptType.DATE),
            arguments(Date.class, JavascriptType.DATE),
            arguments(java.sql.Date.class, JavascriptType.DATE),
            arguments(Boolean.class, JavascriptType.BOOLEAN),
            arguments(Object.class, JavascriptType.OBJECT)
        );
    }

    @MethodSource("shouldReturnTrueIfNumberIsDecimalMethodSource")
    @ParameterizedTest
    void shouldReturnTrueIfNumberIsDecimal(Class<?> type, boolean isDecimal) {
        // when
        boolean result = JavaToJavascriptTypeConversionUtil.isDecimal(type);

        // then
        assertThat(result).isEqualTo(isDecimal);
    }

    private static Stream<Arguments> shouldReturnTrueIfNumberIsDecimalMethodSource() {
        return Stream.of(
            arguments(Integer.class, false),
            arguments(Short.class, false),
            arguments(Long.class, false),
            arguments(Float.class, true),
            arguments(Double.class, true),
            arguments(BigDecimal.class, true)
        );
    }
}
