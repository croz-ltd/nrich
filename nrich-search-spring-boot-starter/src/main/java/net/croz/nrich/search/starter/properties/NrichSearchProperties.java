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

package net.croz.nrich.search.starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

/**
 * @param stringSearch            String search configuration used by {@link net.croz.nrich.search.converter.DefaultStringToTypeConverter}
 *                                that is used for string search by {@link net.croz.nrich.search.api.repository.StringSearchExecutor}.
 * @param defaultConverterEnabled Whether default string to type converter ({@link net.croz.nrich.search.converter.DefaultStringToTypeConverter}) used for converting strings to property values when querying is enabled.
 */
@ConfigurationProperties("nrich.search")
public record NrichSearchProperties(@DefaultValue @NestedConfigurationProperty StringSearchProperties stringSearch,
                                    @DefaultValue("true") boolean defaultConverterEnabled) {


    /**
     * @param dateFormatList           List of date formats used by {@link net.croz.nrich.search.converter.DefaultStringToTypeConverter} to convert string to date values.
     * @param decimalNumberFormatList  List of decimal formats used by {@link net.croz.nrich.search.converter.DefaultStringToTypeConverter} to convert string to decimal value.
     * @param booleanTrueRegexPattern  Regexp pattern that is used by {@link net.croz.nrich.search.converter.DefaultStringToTypeConverter} to match boolean true values.
     * @param booleanFalseRegexPattern Regexp pattern that is used by {@link net.croz.nrich.search.converter.DefaultStringToTypeConverter} to match boolean false values.
     */
    public record StringSearchProperties(@DefaultValue({ "dd.MM.yyyy.", "dd.MM.yyyy.'T'HH:mm", "dd.MM.yyyy.'T'HH:mm'Z'" }) List<String> dateFormatList,
                                         @DefaultValue({ "#0.00", "#0,00" }) List<String> decimalNumberFormatList,
                                         @DefaultValue("^(?i)\\s*(true|yes|da)\\s*$") String booleanTrueRegexPattern, @DefaultValue("^(?i)\\s*(false|no|ne)\\s*$") String booleanFalseRegexPattern) {

    }
}
