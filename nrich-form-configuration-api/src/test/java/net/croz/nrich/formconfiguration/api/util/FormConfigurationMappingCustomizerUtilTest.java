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

package net.croz.nrich.formconfiguration.api.util;

import net.croz.nrich.formconfiguration.api.customizer.FormConfigurationMappingCustomizer;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class FormConfigurationMappingCustomizerUtilTest {

    @Test
    void shouldNotFailOnNullFormConfigurationMappingCustomizerList() {
        // when
        Throwable thrown = catchThrowable(() -> FormConfigurationMappingCustomizerUtil.applyCustomizerList(Collections.emptyMap(), null));

        // then
        assertThat(thrown).isNull();
    }

    @Test
    void shouldNotFailOnNullFormConfigurationMapping() {
        // when
        Throwable thrown = catchThrowable(() -> FormConfigurationMappingCustomizerUtil.applyCustomizerList(null, Collections.emptyList()));

        // then
        assertThat(thrown).isNull();
    }

    @Test
    void shouldApplyFormConfigurationCustomizer() {
        // given
        String key = "key";
        Class<?> value = Integer.class;
        Map<String, Class<?>> formConfigurationMapping = new HashMap<>();
        FormConfigurationMappingCustomizer customizer = mapping -> mapping.put(key, value);

        // when
        Map<String, Class<?>> result = FormConfigurationMappingCustomizerUtil.applyCustomizerList(formConfigurationMapping, Collections.singletonList(customizer));

        // then
        assertThat(result).containsEntry(key, value);
    }
}
