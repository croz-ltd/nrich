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

package net.croz.nrich.validation.aot;

import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationRuntimeHintsRegistrarTest {

    @Test
    void shouldRegisterHints() {
        // given
        RuntimeHints hints = new RuntimeHints();
        ClassLoader classLoader = getClass().getClassLoader();

        // when
        new ValidationRuntimeHintsRegistrar().registerHints(hints, classLoader);

        // then
        assertThat(RuntimeHintsPredicates.resource().forBundle(ValidationRuntimeHintsRegistrar.RESOURCE_BUNDLE)).accepts(hints);

        ValidationRuntimeHintsRegistrar.RESOURCE_PATTERN_LIST.forEach(resource -> assertThat(RuntimeHintsPredicates.resource().forResource(resource)).accepts(hints));

        ValidationRuntimeHintsRegistrar.TYPE_REFERENCE_LIST.forEach(type -> assertThat(RuntimeHintsPredicates.reflection().onType(type)).accepts(hints));
    }
}
