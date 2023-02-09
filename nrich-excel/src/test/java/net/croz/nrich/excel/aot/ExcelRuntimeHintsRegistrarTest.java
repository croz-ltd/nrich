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

package net.croz.nrich.excel.aot;

import lombok.SneakyThrows;
import org.apache.xmlbeans.SchemaType;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

class ExcelRuntimeHintsRegistrarTest {

    @Test
    void shouldRegisterHints() {
        // given
        RuntimeHints hints = new RuntimeHints();
        ClassLoader classLoader = getClass().getClassLoader();

        // when
        new ExcelRuntimeHintsRegistrar().registerHints(hints, classLoader);

        // then
        assertThat(RuntimeHintsPredicates.resource().forBundle(ExcelRuntimeHintsRegistrar.RESOURCE_BUNDLE)).accepts(hints);
        assertThat(RuntimeHintsPredicates.resource().forResource(ExcelRuntimeHintsRegistrar.RESOURCE_PATTERN)).accepts(hints);

        ExcelRuntimeHintsRegistrar.CLASS_LIST.forEach(type -> assertThat(RuntimeHintsPredicates.reflection().onType(type)).accepts(hints));
        ExcelRuntimeHintsRegistrar.CONSTRUCTOR_CLASS_LIST.forEach(type -> assertThat(RuntimeHintsPredicates.reflection().onConstructor(resolveConstructor(type))).accepts(hints));
    }

    @SneakyThrows
    private Constructor<?> resolveConstructor(Class<?> type) {
        return type.getDeclaredConstructor(SchemaType.class);
    }
}
