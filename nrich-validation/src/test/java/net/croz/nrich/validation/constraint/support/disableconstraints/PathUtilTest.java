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

package net.croz.nrich.validation.constraint.support.disableconstraints;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PathUtilTest {

    @Test
    void shouldReturnPropertyNameWhenTypeIsNull() {
        // given
        String propertyName = "propertyName";

        // when
        String result = PathUtil.getPath((Class<?>) null, propertyName);

        // then
        assertThat(result).isEqualTo(propertyName);
    }

    @Test
    void shouldReturnTypeNameWhenPropertyNameIsNull() {
        // given
        Class<?> type = Object.class;

        // when
        String result = PathUtil.getPath(type, null);

        // then
        assertThat(result).isEqualTo(type.getName());
    }

    @Test
    void shouldReturnPath() {
        // given
        Class<?> type = Object.class;
        String propertyName = "propertyName";

        // when
        String result = PathUtil.getPath(type, propertyName);

        // then
        assertThat(result).isEqualTo("java.lang.Object.propertyName");
    }
}
