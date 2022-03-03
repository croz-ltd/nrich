package net.croz.nrich.search.converter;

import net.croz.nrich.search.SearchTestConfiguration;
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
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringJUnitConfig(SearchTestConfiguration.class)
class DefaultStringToTypeConverterTest {

    @Autowired
    private DefaultStringToTypeConverter defaultStringToTypeConverter;

    @MethodSource("shouldConvertStringValueToRequiredValueMethodSource")
    @ParameterizedTest
    void shouldConvertStringValueToRequiredValue(String stringValue, Class<?> typeToConvertTo, Object expectedValue) {
        // when
        Object convertedValue = defaultStringToTypeConverter.convert(stringValue, typeToConvertTo);

        // then
        assertThat(convertedValue).isEqualTo(expectedValue);
    }

    private static Stream<Arguments> shouldConvertStringValueToRequiredValueMethodSource() {
        return Stream.of(
            arguments(null, Boolean.class, null),
            arguments(null, DefaultStringToTypeConverterTest.class, null),
            arguments("true", Boolean.class, Boolean.TRUE),
            arguments("yes", Boolean.class, Boolean.TRUE),
            arguments("no", Boolean.class, Boolean.FALSE),
            arguments("1", Long.class, 1L),
            arguments("D", Long.class, null),
            arguments("5", Integer.class, 5),
            arguments("5", Short.class, Short.valueOf("5")),
            arguments("ONE", Value.class, Value.ONE),
            arguments("01.01.1970", Date.class, dateOf("01.01.1970")),
            arguments("not a date", Date.class, null),
            arguments("01.01.1970", LocalDate.class, localDateOf("01.01.1970")),
            arguments("2020-01-01T11:11", LocalDateTime.class, localDateTimeOf("2020-01-01T11:11")),
            arguments("1.1", BigDecimal.class, new BigDecimal("1.1")),
            arguments("1.1", Float.class, Double.valueOf("1.1")),
            arguments("1.1", Double.class, Double.valueOf("1.1")),
            arguments("nn", Double.class, null)
        );
    }

    enum Value {
        ONE
    }
}
