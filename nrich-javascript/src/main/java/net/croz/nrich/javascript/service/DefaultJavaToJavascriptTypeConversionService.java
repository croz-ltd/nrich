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

package net.croz.nrich.javascript.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.croz.nrich.javascript.api.converter.JavaToJavascriptTypeConverter;
import net.croz.nrich.javascript.api.model.JavascriptType;
import net.croz.nrich.javascript.api.service.JavaToJavascriptTypeConversionService;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class DefaultJavaToJavascriptTypeConversionService implements JavaToJavascriptTypeConversionService {

    private static final String DEFAULT_TYPE = JavascriptType.OBJECT.name().toLowerCase(Locale.ROOT);

    private final List<JavaToJavascriptTypeConverter> javaToJavascriptTypeConverterList;

    @Override
    public String convert(Class<?> type) {
        if (CollectionUtils.isEmpty(javaToJavascriptTypeConverterList)) {
            log.warn("No converts registered for converting between Java to Javascript type, consider defining a bean of {} type", JavaToJavascriptTypeConverter.class.getName());

            return DEFAULT_TYPE;
        }

        return javaToJavascriptTypeConverterList.stream()
            .filter(converter -> converter.supports(type))
            .findFirst()
            .map(converter -> converter.convert(type))
            .orElse(DEFAULT_TYPE);
    }
}
