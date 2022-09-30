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

package net.croz.nrich.javascript.service;

import net.croz.nrich.javascript.api.converter.JavaToJavascriptTypeConverter;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class DefaultJavaToJavascriptTypeConversionServiceTest {

    @Test
    void shouldReturnObjectWhenNoConvertersHaveBeenRegistered() {
        // given
        DefaultJavaToJavascriptTypeConversionService service = new DefaultJavaToJavascriptTypeConversionService(null);

        // when
        String result = service.convert(Integer.class);

        // then
        assertThat(result).isEqualTo("object");
    }

    @Test
    void shouldConvertUsingRegisteredConverter() {
        // given
        Class<?> type = Integer.class;
        JavaToJavascriptTypeConverter converter = mock(JavaToJavascriptTypeConverter.class);

        doReturn(true).when(converter).supports(type);
        doReturn("number").when(converter).convert(type);

        DefaultJavaToJavascriptTypeConversionService service = new DefaultJavaToJavascriptTypeConversionService(Collections.singletonList(converter));

        // when
        String result = service.convert(type);

        // then
        assertThat(result).isEqualTo("number");

        // and when
        String resultWithoutConverter = service.convert(Object.class);

        // then
        assertThat(resultWithoutConverter).isEqualTo("object");
    }
}
