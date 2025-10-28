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

package net.croz.nrich.registry.starter.properties;

import net.croz.nrich.registry.api.core.model.RegistryConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

/**
 * @param defaultReadOnlyPropertyList             List of property names that should always be marked as readonly.
 * @param registrySearch                          Registry search configuration used by {@link net.croz.nrich.search.converter.DefaultStringToTypeConverter}.
 * @param defaultConverterEnabled                 Whether default string to type converter ({@link net.croz.nrich.search.converter.DefaultStringToTypeConverter}) used for converting strings to property values when searching registry is enabled.
 * @param defaultJavaToJavascriptConverterEnabled Whether default Java to Javascript type converter ({@link net.croz.nrich.javascript.converter.DefaultJavaToJavascriptTypeConverter}) used for converting Java to Javascript types is enabled.
 * @param registryConfiguration                   Registry configuration used for defining entities and groups which will be managed.
 */
@ConfigurationProperties("nrich.registry")
public record NrichRegistryProperties(List<String> defaultReadOnlyPropertyList, @DefaultValue @NestedConfigurationProperty RegistrySearchProperties registrySearch,
                                      @DefaultValue("true") boolean defaultConverterEnabled,
                                      @DefaultValue("true") boolean defaultJavaToJavascriptConverterEnabled,
                                      @NestedConfigurationProperty RegistryConfiguration registryConfiguration) {

    /**
     * @param dateFormatList           List of date formats used by {@link net.croz.nrich.search.converter.DefaultStringToTypeConverter} to convert string to date values.
     * @param decimalNumberFormatList  List of decimal formats used by {@link net.croz.nrich.search.converter.DefaultStringToTypeConverter} to convert string to decimal value.
     * @param booleanTrueRegexPattern  Regexp pattern that is used by {@link net.croz.nrich.search.converter.DefaultStringToTypeConverter} to match boolean true values.
     * @param booleanFalseRegexPattern Regexp pattern that is used by {@link net.croz.nrich.search.converter.DefaultStringToTypeConverter} to match boolean false values.
     */
    public record RegistrySearchProperties(@DefaultValue({ "dd.MM.yyyy.", "dd.MM.yyyy.'T'HH:mm", "dd.MM.yyyy.'T'HH:mm'Z'" }) List<String> dateFormatList,
                                           @DefaultValue({ "#0.00", "#0,00" }) List<String> decimalNumberFormatList,
                                           @DefaultValue("^(?i)\\s*(true|yes|da)\\s*$") String booleanTrueRegexPattern, @DefaultValue("^(?i)\\s*(false|no|ne)\\s*$") String booleanFalseRegexPattern) {
    }
}
