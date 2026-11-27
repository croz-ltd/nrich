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

package net.croz.nrich.jackson.serializer;

import net.croz.nrich.jackson.module.JacksonModuleUtil;
import net.croz.nrich.jackson.serializer.stub.EntityClassSerializerModifierTestEntity;
import net.croz.nrich.jackson.serializer.stub.EntityClassSerializerModifierTestEntityWithoutAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class EntityClassSerializerModifierTest {

    private static final TypeReference<Map<String, String>> MAP_TYPE_REFERENCE = new TypeReference<>() {
    };

    @Test
    void shouldSerializeClassNameForEntityClassWhenEnabled() {
        // given
        JsonMapper jsonMapper = JsonMapper.builder().addModule(JacksonModuleUtil.classNameSerializerModule(true, null)).build();

        // when
        Map<String, String> deserialized = jsonMapper.readValue(jsonMapper.writeValueAsString(new EntityClassSerializerModifierTestEntity()), MAP_TYPE_REFERENCE);

        // then
        assertThat(deserialized).containsEntry("class", EntityClassSerializerModifierTestEntity.class.getName());
    }

    @MethodSource("shouldNotSerializeWhenSerializationIsNotEnabledMethodSource")
    @ParameterizedTest
    void shouldNotSerializeWhenSerializationIsNotEnabled(boolean serializeEntityAnnotatedClasses, List<String> packageList, Object entity) {
        // given
        JsonMapper jsonMapper = JsonMapper.builder().addModule(JacksonModuleUtil.classNameSerializerModule(serializeEntityAnnotatedClasses, packageList)).build();

        // when
        Map<String, String> deserialized = jsonMapper.readValue(jsonMapper.writeValueAsString(entity), MAP_TYPE_REFERENCE);

        // then
        assertThat(deserialized.get("class")).isNull();
    }

    private static Stream<Arguments> shouldNotSerializeWhenSerializationIsNotEnabledMethodSource() {
        return Stream.of(
            arguments(false, null, new EntityClassSerializerModifierTestEntity()),
            arguments(false, List.of("java.lang"), new EntityClassSerializerModifierTestEntity()),
            arguments(true, null, new EntityClassSerializerModifierTestEntityWithoutAnnotation())
        );
    }

    @Test
    void shouldSerializeClassNameForPackage() {
        // given
        JacksonModule module = JacksonModuleUtil.classNameSerializerModule(false, List.of(EntityClassSerializerModifierTestEntity.class.getPackage().getName()));
        JsonMapper jsonMapper = JsonMapper.builder().addModule(module).build();


        // when
        Map<String, String> deserialized = jsonMapper.readValue(jsonMapper.writeValueAsString(new EntityClassSerializerModifierTestEntity()), MAP_TYPE_REFERENCE);

        // then
        assertThat(deserialized).containsEntry("class", EntityClassSerializerModifierTestEntity.class.getName());
    }
}
