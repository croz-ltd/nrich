package net.croz.nrich.search.converter.impl;

import net.croz.nrich.search.SearchConfigurationTestConfiguration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.stream.Stream;

import static net.croz.nrich.search.converter.testutil.ConverterGeneratingUtil.dateOf;
import static net.croz.nrich.search.converter.testutil.ConverterGeneratingUtil.localDateOf;
import static net.croz.nrich.search.converter.testutil.ConverterGeneratingUtil.localDateTimeOf;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = SearchConfigurationTestConfiguration.class)
public class DefaultStringToTypeConverterTest {

    @Autowired
    private DefaultStringToTypeConverter defaultStringToTypeConverter;

    @MethodSource("shouldConvertStringValueToRequiredValueMethodSource")
    @ParameterizedTest
    void shouldConvertStringValueToRequiredValue(final String stringValue, final Class<?> typeToConvertTo, final Object expectedValue) {
        // when
        final Object convertedValue = defaultStringToTypeConverter.convert(stringValue, typeToConvertTo);

        // then
        assertThat(convertedValue).isEqualTo(expectedValue);
    }

    private static Stream<Arguments> shouldConvertStringValueToRequiredValueMethodSource() {
        return Stream.of(
                Arguments.of(null, Boolean.class, null),
                Arguments.of(null, DefaultStringToTypeConverterTest.class, null),
                Arguments.of("true", Boolean.class, Boolean.TRUE),
                Arguments.of("1", Long.class, 1L),
                Arguments.of("D", Long.class, null),
                Arguments.of("5", Integer.class, 5),
                Arguments.of("5", Short.class, Short.valueOf("5")),
                Arguments.of("ONE", Value.class, Value.ONE),
                Arguments.of("01.01.1970", Date.class, dateOf("01.01.1970")),
                Arguments.of("not a date", Date.class, null),
                Arguments.of("01.01.1970", LocalDate.class, localDateOf("01.01.1970")),
                Arguments.of("2020-01-01T11:11", LocalDateTime.class, localDateTimeOf("2020-01-01T11:11")),
                Arguments.of("1.1", BigDecimal.class, new BigDecimal("1.1")),
                Arguments.of("1.1", Float.class, Double.valueOf("1.1")),
                Arguments.of("1.1", Double.class, Double.valueOf("1.1")),
                Arguments.of("nn", Double.class, null)
        );
    }

    enum Value {
        ONE
    }
}
