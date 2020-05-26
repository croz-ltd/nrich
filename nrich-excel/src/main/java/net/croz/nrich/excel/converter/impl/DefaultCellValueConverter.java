package net.croz.nrich.excel.converter.impl;

import lombok.Value;
import net.croz.nrich.excel.converter.CellValueConverter;
import net.croz.nrich.excel.model.TypeDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Order
public class DefaultCellValueConverter implements CellValueConverter {

    private final String dateFormat;

    private final String dateTimeFormat;

    private final String integerNumberFormat;

    private final String decimalNumberFormat;

    private final boolean writeDateWithTime;

    private final List<ConverterHolder> converterHolderList;

    public DefaultCellValueConverter(final String dateFormat, final String dateTimeFormat, final String integerNumberFormat, final String decimalNumberFormat, final boolean writeDateWithTime) {
        this.dateFormat = dateFormat;
        this.dateTimeFormat = dateTimeFormat;
        this.integerNumberFormat = integerNumberFormat;
        this.decimalNumberFormat = decimalNumberFormat;
        this.writeDateWithTime = writeDateWithTime;
        this.converterHolderList = initializeConverterList();
    }

    @Override
    public void setCellValue(final Cell cell, final Object value) {
        Optional.ofNullable(findConverter(value)).ifPresent(converterHolder -> converterHolder.setCellValueFunction.accept(cell, value));
    }

    @Override
    public boolean supports(final Cell cell, final Object value) {
        return findConverter(value) != null;
    }

    @Override
    public List<TypeDataFormat> typeDataFormatList() {
        return converterHolderList.stream()
                .map(converterHolder -> new TypeDataFormat(converterHolder.type, converterHolder.dataFormat))
                .collect(Collectors.toList());
    }

    private List<ConverterHolder> initializeConverterList() {
        final String resolvedDateTimeFormat = writeDateWithTime ? dateTimeFormat : dateFormat;

        return Arrays.asList(
                new ConverterHolder(Date.class, dateFormat, (cell, value) -> cell.setCellValue((Date) value)),
                new ConverterHolder(Instant.class, dateFormat, (cell, value) -> cell.setCellValue(new Date(((Instant) value).toEpochMilli()))),
                new ConverterHolder(LocalDate.class, dateFormat, (cell, value) -> cell.setCellValue((LocalDate) value)),
                new ConverterHolder(LocalDateTime.class, resolvedDateTimeFormat, (cell, value) -> cell.setCellValue((LocalDateTime) value)),
                new ConverterHolder(ZonedDateTime.class, resolvedDateTimeFormat, (cell, value) -> cell.setCellValue(((ZonedDateTime) value).toLocalDateTime())),
                new ConverterHolder(OffsetDateTime.class, resolvedDateTimeFormat, (cell, value) -> cell.setCellValue(((OffsetDateTime) value).toLocalDateTime())),
                new ConverterHolder(Short.class, integerNumberFormat, (cell, value) -> cell.setCellValue(((Number) value).longValue())),
                new ConverterHolder(Integer.class, integerNumberFormat, (cell, value) -> cell.setCellValue(((Number) value).longValue())),
                new ConverterHolder(Long.class, integerNumberFormat, (cell, value) -> cell.setCellValue(((Number) value).longValue())),
                new ConverterHolder(BigDecimal.class, decimalNumberFormat, (cell, value) -> cell.setCellValue(((Number) value).doubleValue())),
                new ConverterHolder(Float.class, decimalNumberFormat, (cell, value) -> cell.setCellValue(((Number) value).doubleValue())),
                new ConverterHolder(Double.class, decimalNumberFormat, (cell, value) -> cell.setCellValue(((Number) value).doubleValue()))
        );
    }

    private ConverterHolder findConverter(final Object value) {
        if (value == null) {
            return null;
        }

        return converterHolderList.stream()
                .filter(converterHolder -> converterHolder.getType().isAssignableFrom(value.getClass()))
                .findFirst()
                .orElse(null);
    }

    @Value
    public static class ConverterHolder {

        Class<?> type;

        String dataFormat;

        BiConsumer<Cell, Object> setCellValueFunction;

    }
}
