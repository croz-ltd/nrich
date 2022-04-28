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

    public static List<TypeDataFormat> resolveTypeDataFormatList(String dateFormat, String dateTimeFormat, String integerNumberFormat, String decimalNumberFormat, boolean writeDateWithTime,
                                                                 List<TypeDataFormat> additionalTypeDataFormatList) {
        String resolvedDateTimeFormat = writeDateWithTime ? dateTimeFormat : dateFormat;

        List<TypeDataFormat> nonNullAdditionalDataFormatList = Optional.ofNullable(additionalTypeDataFormatList).orElse(Collections.emptyList());

        List<TypeDataFormat> typeDataFormatList = Arrays.asList(
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

        List<TypeDataFormat> allTypeDataFormatList = typeDataFormatList.stream()
            .map(typeDataFormat -> Optional.ofNullable(findTypeDataFormat(nonNullAdditionalDataFormatList, typeDataFormat.getType())).orElse(typeDataFormat))
            .collect(Collectors.toList());

        List<TypeDataFormat> notAddedAdditionalTypeDataFormatList = nonNullAdditionalDataFormatList.stream()
            .filter(typeDataFormat -> findTypeDataFormat(allTypeDataFormatList, typeDataFormat.getType()) == null)
            .collect(Collectors.toList());

        return Stream.concat(allTypeDataFormatList.stream(), notAddedAdditionalTypeDataFormatList.stream()).collect(Collectors.toList());
    }

    private static TypeDataFormat findTypeDataFormat(List<TypeDataFormat> typeDataFormatList, Class<?> type) {
        return typeDataFormatList.stream()
            .filter(typeDataFormat -> type.equals(typeDataFormat.getType()))
            .findFirst()
            .orElse(null);
    }
}
