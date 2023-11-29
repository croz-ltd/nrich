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

package net.croz.nrich.excel.converter;

import net.croz.nrich.excel.api.model.CellHolder;
import net.croz.nrich.excel.stub.TestEnum;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DefaultCellValueConverterTest {

    private final DefaultCellValueConverter defaultCellValueConverter = new DefaultCellValueConverter(new ResourceBundleMessageSource());

    @MethodSource("shouldReturnTrueIfConversionIsSupportedMethodSource")
    @ParameterizedTest
    void shouldReturnTrueIfConversionIsSupported(Object value, boolean isSupported) {
        // given
        CellHolder cellHolder = mock(CellHolder.class);

        // when
        boolean result = defaultCellValueConverter.supports(cellHolder, value);

        // then
        assertThat(result).isEqualTo(isSupported);
    }

    private static Stream<Arguments> shouldReturnTrueIfConversionIsSupportedMethodSource() {
        return Stream.of(
            arguments(null, false),
            arguments(new Object(), false),
            arguments(new Date(), true),
            arguments(new java.sql.Date(System.currentTimeMillis()), true),
            arguments(new GregorianCalendar(), true),
            arguments(Instant.now(), true),
            arguments(LocalDate.now(), true),
            arguments(LocalDateTime.now(), true),
            arguments(OffsetDateTime.now(), true),
            arguments(ZonedDateTime.now(), true),
            arguments((short) 1, true),
            arguments(1, true),
            arguments(1L, true),
            arguments(2.2F, true),
            arguments(2.2D, true),
            arguments(BigDecimal.ONE, true),
            arguments(TestEnum.FIRST, true)
        );
    }

    @MethodSource("shouldSetValueWithConversionIfNecessaryMethodSource")
    @ParameterizedTest
    void shouldSetValueWithConversionIfNecessary(Object value, Class<?> type) {
        // given
        CellHolder cellHolder = mock(CellHolder.class);

        // when
        defaultCellValueConverter.setCellValue(cellHolder, value);

        // then
        verify(cellHolder).setCellValue(argThat(argument -> type.isAssignableFrom(argument.getClass())));
    }

    private static Stream<Arguments> shouldSetValueWithConversionIfNecessaryMethodSource() {
        return Stream.of(
            arguments(new Date(), Date.class),
            arguments(new java.sql.Date(System.currentTimeMillis()), Date.class),
            arguments(new GregorianCalendar(), Calendar.class),
            arguments(Instant.now(), Date.class),
            arguments(LocalDate.now(), LocalDate.class),
            arguments(LocalDateTime.now(), LocalDateTime.class),
            arguments(OffsetDateTime.now(), LocalDateTime.class),
            arguments(ZonedDateTime.now(), LocalDateTime.class),
            arguments((short) 1, Number.class),
            arguments(1, Number.class),
            arguments(1L, Number.class),
            arguments(2.2F, Number.class),
            arguments(2.2D, Number.class),
            arguments(BigDecimal.ONE, Number.class),
            arguments(TestEnum.FIRST, String.class)
        );
    }
}
