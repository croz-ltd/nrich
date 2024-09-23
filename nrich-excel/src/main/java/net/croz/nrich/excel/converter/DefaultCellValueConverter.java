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

import lombok.RequiredArgsConstructor;
import net.croz.nrich.excel.api.converter.CellValueConverter;
import net.croz.nrich.excel.api.model.CellHolder;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
@Order
public class DefaultCellValueConverter implements CellValueConverter {

    private static final String ENUM_MESSAGE_FORMAT = "%s.%s.description";

    private final List<ConverterHolder> converterHolderList = initializeConverterList();

    private final MessageSource messageSource;

    @Override
    public void setCellValue(CellHolder cell, Object value) {
        Optional.ofNullable(findConverter(value)).ifPresent(converterHolder -> converterHolder.setCellValueFunction.accept(cell, value));
    }

    @Override
    public boolean supports(CellHolder cell, Object value) {
        return findConverter(value) != null;
    }

    private List<ConverterHolder> initializeConverterList() {
        return List.of(
            new ConverterHolder(Date.class, CellHolder::setCellValue),
            new ConverterHolder(Calendar.class, CellHolder::setCellValue),
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
            new ConverterHolder(Double.class, (cell, value) -> cell.setCellValue(((Number) value).doubleValue())),
            new ConverterHolder(Enum.class, (cell, value) -> cell.setCellValue(resolveEnumValue(value)))
        );
    }

    private ConverterHolder findConverter(Object value) {
        if (value == null) {
            return null;
        }

        return converterHolderList.stream()
            .filter(converterHolder -> converterHolder.type().isAssignableFrom(value.getClass()))
            .findFirst()
            .orElse(null);
    }

    private String resolveEnumValue(Object value) {
        String messageCode = String.format(ENUM_MESSAGE_FORMAT, value.getClass().getName(), value);

        return messageSource.getMessage(messageCode, null, value.toString(), LocaleContextHolder.getLocale());
    }

    public record ConverterHolder(Class<?> type, BiConsumer<CellHolder, Object> setCellValueFunction) {

    }
}
