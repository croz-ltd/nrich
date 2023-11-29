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

package net.croz.nrich.excel.util;

import net.croz.nrich.excel.api.model.TypeDataFormat;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TypeDataFormatUtilTest {

    @Test
    void shouldReturnTypeDataFormatList() {
        // given
        String dateFormat = "dd.MM.yyyy.";
        String dateTimeFormat = "dd.MM.yyyy. HH:mm";
        String integerFormat = "#,##0";
        String decimalFormat = "#,##0.00";

        // when
        List<TypeDataFormat> formatList = TypeDataFormatUtil.resolveTypeDataFormatList(dateFormat, dateTimeFormat, integerFormat, decimalFormat, true, null);

        // then
        assertThat(formatList).extracting("type").containsExactly(
            Date.class, Instant.class, LocalDate.class, java.sql.Date.class, LocalDateTime.class, ZonedDateTime.class, OffsetDateTime.class, Timestamp.class,
            Short.class, Integer.class, Long.class, BigInteger.class, Float.class, Double.class, BigDecimal.class
        );
        assertThat(formatList).extracting("dataFormat").containsExactly(
            dateFormat, dateFormat, dateFormat, dateFormat, dateTimeFormat, dateTimeFormat, dateTimeFormat, dateTimeFormat,
            integerFormat, integerFormat, integerFormat, integerFormat, decimalFormat, decimalFormat, decimalFormat
        );
    }

    @Test
    void shouldReturnDateFormatWithTimeWhenEnabled() {
        // given
        String dateTimeFormat = "dd.MM.yyyy. HH:mm";

        // when
        List<TypeDataFormat> formatList = TypeDataFormatUtil.resolveTypeDataFormatList("dd.MM.yyyy.", dateTimeFormat, "#,##0", "#,##0.00", true, null);

        // then
        assertThat(formatList).extracting("dataFormat").contains(dateTimeFormat);

        // and when
        List<TypeDataFormat> formatListWithoutTime = TypeDataFormatUtil.resolveTypeDataFormatList("dd.MM.yyyy.", dateTimeFormat, "#,##0", "#,##0.00", false, null);

        // then
        assertThat(formatListWithoutTime).extracting("dataFormat").isNotEmpty().doesNotContain(dateTimeFormat);
    }

    @Test
    void shouldAllowForAdditionalFormatsToBeSpecified() {
        // given
        Class<?> additionalClass = Object.class;
        String dataFormat = "dd/MM/yyyy";
        List<TypeDataFormat> overriddenFormatList = Collections.singletonList(new TypeDataFormat(additionalClass, dataFormat));

        // when
        List<TypeDataFormat> formatList = TypeDataFormatUtil.resolveTypeDataFormatList("dd.MM.yyyy.", "dd.MM.yyyy. HH:mm", "#,##0", "#,##0.00", true, overriddenFormatList);
        TypeDataFormat dateTypeDataFormat = formatList.stream().filter(typeDataFormat -> additionalClass.equals(typeDataFormat.getType())).findFirst().orElse(null);

        // then
        assertThat(dateTypeDataFormat).isNotNull();
        assertThat(dateTypeDataFormat.getDataFormat()).isEqualTo(dataFormat);
    }
}
