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

package net.croz.nrich.jackson.deserializer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.jackson.module.JacksonModuleUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ConvertEmptyStringToNullDeserializerTest {

    private static final TypeReference<Map<String, String>> MAP_TYPE_REFERENCE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(JacksonModuleUtil.convertEmptyStringToNullModule());

    @Test
    void shouldConvertEmptyStringsToNull() throws Exception {
        // given
        String emptyKey = "emptyKey";
        String nonEmptyKey = "nonEmptyKey";
        Map<String, String> testMap = Map.of(emptyKey, "", nonEmptyKey, "non empty");

        // when
        Map<String, String> deserialized = objectMapper.readValue(objectMapper.writeValueAsString(testMap), MAP_TYPE_REFERENCE);

        // then
        assertThat(deserialized.get(emptyKey)).isNull();
        assertThat(deserialized.get(nonEmptyKey)).isNotEmpty();
    }
}
