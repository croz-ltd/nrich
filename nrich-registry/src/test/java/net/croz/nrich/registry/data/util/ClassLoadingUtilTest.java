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

package net.croz.nrich.registry.data.util;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClassLoadingUtilTest {

    @Test
    void shouldLoadClass() {
        // given
        List<String> classNameList = Collections.singletonList(String.class.getName());

        // when
        Class<?> result = ClassLoadingUtil.loadClassFromList(classNameList);

        // then
        assertThat(result).isEqualTo(String.class);
    }

    @Test
    void shouldReturnNullForNonExistingClass() {
        // given
        List<String> classNameList = Collections.singletonList("non.existing.Class");

        // when
        Class<?> result = ClassLoadingUtil.loadClassFromList(classNameList);

        // then
        assertThat(result).isNull();
    }
}
