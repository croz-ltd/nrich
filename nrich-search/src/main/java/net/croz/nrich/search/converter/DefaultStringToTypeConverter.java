package net.croz.nrich.search.converter;

import lombok.SneakyThrows;
import lombok.Value;
import net.croz.nrich.search.api.converter.StringToTypeConverter;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalQuery;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Order
public class DefaultStringToTypeConverter implements StringToTypeConverter<Object> {

    private final List<String> dateFormatList;

    private final List<String> decimalNumberFormatList;

    private final String booleanTrueRegexPattern;

    private final String booleanFalseRegexPattern;

    private final List<ConverterHolder> converterHolderList;

    public DefaultStringToTypeConverter(List<String> dateFormatList, List<String> decimalNumberFormatList, String booleanTrueRegexPattern, String booleanFalseRegexPattern) {
        this.dateFormatList = dateFormatList;
        this.decimalNumberFormatList = decimalNumberFormatList;
        this.booleanTrueRegexPattern = booleanTrueRegexPattern;
        this.booleanFalseRegexPattern = booleanFalseRegexPattern;
        this.converterHolderList = initializeConverterList();
    }

    @Override
    public Object convert(String value, Class<?> requiredType) {
        if (value == null) {
            return null;
        }

        ConverterHolder converterHolder = converterHolderList.stream().filter(holder -> holder.getType().isAssignableFrom(requiredType)).findFirst().orElse(null);

        Object convertedValue = null;
        if (converterHolder != null) {
            convertedValue = convertWithExceptionIgnored(() -> converterHolder.getConversionFunction().apply(value, requiredType));
        }

        return convertedValue;
    }

    @Override
    public boolean supports(Class<?> requiredType) {
        return true;
    }

    private Object convertWithExceptionIgnored(Supplier<Object> conversionFunction) {
        try {
            return conversionFunction.get();
        }
        catch (Exception ignored) {
            return null;
        }
    }

    private List<ConverterHolder> initializeConverterList() {
        return Arrays.asList(
            new ConverterHolder(Boolean.class, (value, type) -> booleanConverter(value)),
            new ConverterHolder(Long.class, (value, type) -> Long.valueOf(value)),
            new ConverterHolder(Integer.class, (value, type) -> Integer.valueOf(value)),
            new ConverterHolder(Short.class, (value, type) -> Short.valueOf(value)),
            new ConverterHolder(Enum.class, this::enumConverter),
            new ConverterHolder(Date.class, (value, type) -> dateConverter(value)),
            new ConverterHolder(Instant.class, (value, type) -> temporalConverter(value, Instant::from)),
            new ConverterHolder(LocalDate.class, (value, type) -> temporalConverter(value, LocalDate::from)),
            new ConverterHolder(LocalDateTime.class, (value, type) -> temporalConverter(value, LocalDateTime::from)),
            new ConverterHolder(OffsetTime.class, (value, type) -> temporalConverter(value, OffsetTime::from)),
            new ConverterHolder(OffsetDateTime.class, (value, type) -> temporalConverter(value, OffsetDateTime::from)),
            new ConverterHolder(ZonedDateTime.class, (value, type) -> temporalConverter(value, ZonedDateTime::from)),
            new ConverterHolder(Float.class, (value, type) -> numberConverter(value, false)),
            new ConverterHolder(Double.class, (value, type) -> numberConverter(value, false)),
            new ConverterHolder(BigDecimal.class, (value, type) -> numberConverter(value, true))
        );
    }

    private Boolean booleanConverter(String value) {
        Boolean convertedValue = null;

        if (value.matches(booleanTrueRegexPattern)) {
            convertedValue = Boolean.TRUE;
        }
        else if (value.matches(booleanFalseRegexPattern)) {
            convertedValue = Boolean.FALSE;
        }

        return convertedValue;
    }

    private <E extends Enum<E>> E enumConverter(String value, Class<?> requiredType) {
        @SuppressWarnings("unchecked")
        Class<E> enumType = (Class<E>) requiredType;

        return Enum.valueOf(enumType, value);
    }

    private Object dateConverter(String value) {
        return dateFormatList.stream()
            .map(SimpleDateFormat::new)
            .map(format -> convertWithExceptionIgnored(() -> parseDate(format, value)))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    private Object temporalConverter(String value, TemporalQuery<?> query) {
        return dateFormatList.stream()
            .map(DateTimeFormatter::ofPattern)
            .map(format -> convertWithExceptionIgnored(() -> format.parse(value, query)))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    private Object numberConverter(String value, boolean parseBigDecimal) {
        return decimalNumberFormatList.stream()
            .map(format -> {
                DecimalFormat decimalFormat = new DecimalFormat(format);

                decimalFormat.setParseBigDecimal(parseBigDecimal);

                return decimalFormat;
            })
            .map(format -> convertWithExceptionIgnored(() -> parseNumber(format, value)))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    @SneakyThrows
    private Object parseDate(SimpleDateFormat format, String value) {
        return format.parse(value);
    }

    @SneakyThrows
    private Object parseNumber(DecimalFormat format, String value) {
        return format.parse(value);
    }

    @Value
    public static class ConverterHolder {

        Class<?> type;

        BiFunction<String, Class<?>, Object> conversionFunction;

    }
}
