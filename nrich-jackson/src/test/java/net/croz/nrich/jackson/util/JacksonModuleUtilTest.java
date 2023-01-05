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

package net.croz.nrich.jackson.util;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.croz.nrich.jackson.module.JacksonModuleUtil;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class JacksonModuleUtilTest {

    @Test
    void shouldCreateConvertEmptyStringToNullModule() {
        // when
        Module module = JacksonModuleUtil.convertEmptyStringToNullModule();

        // then
        assertThat(module.getModuleName()).isEqualTo(JacksonModuleUtil.CONVERT_EMPTY_STRING_TO_NULL_MODULE_NAME);
        assertThat(module).isInstanceOf(SimpleModule.class);
    }

    @Test
    void shouldCreateClassNameSerializerModule() {
        // when
        Module module = JacksonModuleUtil.classNameSerializerModule(true, Collections.singletonList("net.croz.nrich"));

        // then
        assertThat(module.getModuleName()).isEqualTo(JacksonModuleUtil.CLASS_NAME_SERIALIZER_MODULE);
        assertThat(module).isInstanceOf(SimpleModule.class);
    }
}
