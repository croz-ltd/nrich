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

package net.croz.nrich.search.starter.configuration;

import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.converter.StringToTypeConverter;
import net.croz.nrich.search.api.factory.RepositoryFactorySupportFactory;
import net.croz.nrich.search.converter.DefaultStringToEntityPropertyMapConverter;
import net.croz.nrich.search.converter.DefaultStringToTypeConverter;
import net.croz.nrich.search.factory.SearchRepositoryFactorySupportFactory;
import net.croz.nrich.search.starter.properties.NrichSearchProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@AutoConfigureAfter(HibernateJpaAutoConfiguration.class)
@EnableConfigurationProperties(NrichSearchProperties.class)
@Configuration(proxyBeanMethods = false)
public class NrichSearchAutoConfiguration {

    private static final String SEARCH_CONVERTER = "search";

    @ConditionalOnProperty(name = "nrich.search.default-converter-enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(name = "searchDefaultStringToTypeConverter")
    @Bean
    public StringToTypeConverter<Object> searchDefaultStringToTypeConverter(NrichSearchProperties searchProperties) {
        List<String> dateFormatList = searchProperties.stringSearch().dateFormatList();
        List<String> decimalFormatList = searchProperties.stringSearch().decimalNumberFormatList();
        String booleanTrueRegexPattern = searchProperties.stringSearch().booleanTrueRegexPattern();
        String booleanFalseRegexPattern = searchProperties.stringSearch().booleanFalseRegexPattern();

        return new DefaultStringToTypeConverter(dateFormatList, decimalFormatList, booleanTrueRegexPattern, booleanFalseRegexPattern);
    }

    @ConditionalOnMissingBean(name = "searchStringToEntityPropertyMapConverter")
    @Bean
    public StringToEntityPropertyMapConverter searchStringToEntityPropertyMapConverter(@Lazy @Autowired(required = false) Map<String, StringToTypeConverter<?>> stringToTypeConverterList) {
        @SuppressWarnings("java:S6204")
        List<StringToTypeConverter<?>> searchConverters = stringToTypeConverterList.entrySet().stream()
            .filter(entry -> entry.getKey().toLowerCase(Locale.ROOT).contains(SEARCH_CONVERTER))
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());

        return new DefaultStringToEntityPropertyMapConverter(searchConverters);
    }

    @ConditionalOnMissingBean
    @Bean
    public RepositoryFactorySupportFactory searchRepositoryFactorySupportFactory(StringToEntityPropertyMapConverter searchStringToEntityPropertyMapConverter) {
        return new SearchRepositoryFactorySupportFactory(searchStringToEntityPropertyMapConverter);
    }
}
