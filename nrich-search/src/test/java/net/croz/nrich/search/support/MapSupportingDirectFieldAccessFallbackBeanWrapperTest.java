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

package net.croz.nrich.search.support;

import net.croz.nrich.search.bean.MapSupportingDirectFieldAccessFallbackBeanWrapper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static net.croz.nrich.search.support.testutil.MapSupportingDirectFieldAccessFallbackBeanWrapperGeneratingUtil.entityWithPropertyValue;
import static net.croz.nrich.search.support.testutil.MapSupportingDirectFieldAccessFallbackBeanWrapperGeneratingUtil.mapOf;
import static org.assertj.core.api.Assertions.assertThat;

class MapSupportingDirectFieldAccessFallbackBeanWrapperTest {

    @Test
    void shouldGetPropertyValueFromMap() {
        // given
        MapSupportingDirectFieldAccessFallbackBeanWrapper wrapper = new MapSupportingDirectFieldAccessFallbackBeanWrapper(mapOf("key", "value"));

        // when
        Object value = wrapper.getPropertyValue("key");

        // then
        assertThat(value).isEqualTo("value");
    }

    @Test
    void shouldSetPropertyValueToMap() {
        // given
        Map<String, Object> map = new HashMap<>();
        MapSupportingDirectFieldAccessFallbackBeanWrapper wrapper = new MapSupportingDirectFieldAccessFallbackBeanWrapper(map);

        // when
        wrapper.setPropertyValue("key", "value");

        // then
        assertThat(map).containsEntry("key", "value");
    }

    @Test
    void shouldGetPropertyFromObject() {
        // given
        MapSupportingDirectFieldAccessFallbackBeanWrapper wrapper = new MapSupportingDirectFieldAccessFallbackBeanWrapper(entityWithPropertyValue("value"));

        // when
        Object value = wrapper.getPropertyValue("name");

        // then
        assertThat(value).isEqualTo("value");
    }

    @Test
    void shouldSetPropertyToObject() {
        // given
        MapSupportingDirectFieldAccessFallbackBeanWrapper wrapper = new MapSupportingDirectFieldAccessFallbackBeanWrapper(entityWithPropertyValue("value"));

        // when
        wrapper.setPropertyValue("name", "new value");

        // then
        assertThat(wrapper.getPropertyValue("name")).isEqualTo("new value");
    }
}
