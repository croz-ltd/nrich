package net.croz.nrich.excel.util;

import net.croz.nrich.excel.api.model.TypeDataFormat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TypeDataFormatUtil {

    private TypeDataFormatUtil() {
    }

    public static List<TypeDataFormat> resolveTypeDataFormatList(final String dateFormat, final String dateTimeFormat, final String integerNumberFormat, final String decimalNumberFormat, final boolean writeDateWithTime, final List<TypeDataFormat> additionalTypeDataFormatList) {
        final String resolvedDateTimeFormat = writeDateWithTime ? dateTimeFormat : dateFormat;

        final List<TypeDataFormat> nonNullAdditionalDataFormatList = Optional.ofNullable(additionalTypeDataFormatList).orElse(Collections.emptyList());

        final List<TypeDataFormat> typeDataFormatList = Arrays.asList(
                new TypeDataFormat(Date.class, dateFormat),
                new TypeDataFormat(Instant.class, dateFormat),
                new TypeDataFormat(LocalDate.class, dateFormat),
                new TypeDataFormat(LocalDateTime.class, resolvedDateTimeFormat),
                new TypeDataFormat(ZonedDateTime.class, resolvedDateTimeFormat),
                new TypeDataFormat(OffsetDateTime.class, resolvedDateTimeFormat),
                new TypeDataFormat(Short.class, integerNumberFormat),
                new TypeDataFormat(Integer.class, integerNumberFormat),
                new TypeDataFormat(Long.class, integerNumberFormat),
                new TypeDataFormat(BigInteger.class, integerNumberFormat),
                new TypeDataFormat(Float.class, decimalNumberFormat),
                new TypeDataFormat(Double.class, decimalNumberFormat),
                new TypeDataFormat(BigDecimal.class, decimalNumberFormat)
        );

        final List<TypeDataFormat> allTypeDataFormatList = typeDataFormatList.stream()
                .map(typeDataFormat -> Optional.ofNullable(findTypeDataFormat(nonNullAdditionalDataFormatList, typeDataFormat.getType())).orElse(typeDataFormat))
                .collect(Collectors.toList());

        final List<TypeDataFormat> notAddedOverriddenFormatList = nonNullAdditionalDataFormatList.stream()
                .filter(typeDataFormat -> findTypeDataFormat(allTypeDataFormatList, typeDataFormat.getType()) == null)
                .collect(Collectors.toList());

        return Stream.concat(allTypeDataFormatList.stream(), notAddedOverriddenFormatList.stream()).collect(Collectors.toList());
    }

    private static TypeDataFormat findTypeDataFormat(final List<TypeDataFormat> typeDataFormatList, final Class<?> type) {
        return typeDataFormatList.stream()
                .filter(typeDataFormat -> type.equals(typeDataFormat.getType()))
                .findFirst()
                .orElse(null);
    }
}
