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

package net.croz.nrich.excel.starter.properties;

import net.croz.nrich.excel.api.model.TypeDataFormat;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

/**
 * @param dateFormat              Date format used to set excel cell style for date values (i.e. {@link java.time.LocalDate}).
 * @param dateTimeFormat          Date time format used to set excel cell style for date time values (i.e. {@link java.time.LocalDateTime}).
 * @param writeDateWithTime       Whether dateFormat or dateTimeFormat should be used for date time values.
 * @param integerNumberFormat     Integer number format used to set excel cell style for integer numbers (short, integer, long, BigInteger).
 * @param decimalNumberFormat     Decimal number format used to set excel cell style for decimal numbers (float, double, BigDecimal).
 * @param typeDataFormatList      A list of formats that overrides default formats for classes.
 * @param defaultConverterEnabled Whether default converter {@link net.croz.nrich.excel.converter.DefaultCellValueConverter} should be enabled. It handles conversion of objects to value
 *                                accepted by excel generator implementation.
 */
@ConfigurationProperties("nrich.excel")
public record NrichExcelProperties(@DefaultValue("dd.MM.yyyy.") String dateFormat, @DefaultValue("dd.MM.yyyy. HH:mm") String dateTimeFormat, @DefaultValue("false") boolean writeDateWithTime,
                                   @DefaultValue("#,##0") String integerNumberFormat, @DefaultValue("#,##0.00") String decimalNumberFormat, List<TypeDataFormat> typeDataFormatList,
                                   @DefaultValue("true") boolean defaultConverterEnabled) {

}
