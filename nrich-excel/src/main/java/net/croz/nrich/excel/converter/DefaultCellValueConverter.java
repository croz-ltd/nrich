package net.croz.nrich.excel.converter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.excel.api.converter.CellValueConverter;
import net.croz.nrich.excel.api.model.CellHolder;
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

@Order
public class DefaultCellValueConverter implements CellValueConverter {

    private final List<ConverterHolder> converterHolderList = initializeConverterList();

    @Override
    public void setCellValue(CellHolder cell, Object value) {
        Optional.ofNullable(findConverter(value)).ifPresent(converterHolder -> converterHolder.setCellValueFunction.accept(cell, value));
    }

    @Override
    public boolean supports(CellHolder cell, Object value) {
        return findConverter(value) != null;
    }

    private List<ConverterHolder> initializeConverterList() {
        return Arrays.asList(
            new ConverterHolder(Date.class, CellHolder::setCellValue),
            new ConverterHolder(Instant.class, (cell, value) -> cell.setCellValue(new Date(((Instant) value).toEpochMilli()))),
            new ConverterHolder(LocalDate.class, CellHolder::setCellValue),
            new ConverterHolder(LocalDateTime.class, CellHolder::setCellValue),
            new ConverterHolder(ZonedDateTime.class, (cell, value) -> cell.setCellValue(((ZonedDateTime) value).toLocalDateTime())),
            new ConverterHolder(OffsetDateTime.class, (cell, value) -> cell.setCellValue(((OffsetDateTime) value).toLocalDateTime())),
            new ConverterHolder(Short.class, (cell, value) -> cell.setCellValue(((Number) value).longValue())),
            new ConverterHolder(Integer.class, (cell, value) -> cell.setCellValue(((Number) value).longValue())),
            new ConverterHolder(Long.class, (cell, value) -> cell.setCellValue(((Number) value).longValue())),
            new ConverterHolder(BigDecimal.class, (cell, value) -> cell.setCellValue(((Number) value).doubleValue())),
            new ConverterHolder(Float.class, (cell, value) -> cell.setCellValue(((Number) value).doubleValue())),
            new ConverterHolder(Double.class, (cell, value) -> cell.setCellValue(((Number) value).doubleValue()))
        );
    }

    private ConverterHolder findConverter(Object value) {
        if (value == null) {
            return null;
        }

        return converterHolderList.stream()
            .filter(converterHolder -> converterHolder.getType().isAssignableFrom(value.getClass()))
            .findFirst()
            .orElse(null);
    }


    @RequiredArgsConstructor
    @Getter
    public static class ConverterHolder {

        private final Class<?> type;

        private final BiConsumer<CellHolder, Object> setCellValueFunction;

    }
}
