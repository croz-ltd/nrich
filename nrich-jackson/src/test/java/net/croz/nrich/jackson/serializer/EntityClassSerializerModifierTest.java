package net.croz.nrich.jackson.serializer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.jackson.module.JacksonModuleUtil;
import net.croz.nrich.jackson.serializer.stub.EntityClassSerializerModifierTestEntity;
import net.croz.nrich.jackson.serializer.stub.EntityClassSerializerModifierTestEntityWithoutAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class EntityClassSerializerModifierTest {

    private static final TypeReference<Map<String, String>> MAP_TYPE_REFERENCE = new TypeReference<Map<String, String>>() {
    };

    @Test
    void shouldSerializeClassNameForEntityClassWhenEnabled() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(JacksonModuleUtil.classNameSerializerModule(true, null));

        // when
        Map<String, String> deserialized = objectMapper.readValue(objectMapper.writeValueAsString(new EntityClassSerializerModifierTestEntity()), MAP_TYPE_REFERENCE);

        // then
        assertThat(deserialized).containsEntry("class", EntityClassSerializerModifierTestEntity.class.getName());
    }

    @MethodSource("shouldNotSerializeWhenSerializationIsNotEnabledMethodSource")
    @ParameterizedTest
    void shouldNotSerializeWhenSerializationIsNotEnabled(boolean serializeEntityAnnotatedClasses, List<String> packageList, Object entity) throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(JacksonModuleUtil.classNameSerializerModule(serializeEntityAnnotatedClasses, packageList));

        // when
        Map<String, String> deserialized = objectMapper.readValue(objectMapper.writeValueAsString(entity), MAP_TYPE_REFERENCE);

        // then
        assertThat(deserialized.get("class")).isNull();
    }

    private static Stream<Arguments> shouldNotSerializeWhenSerializationIsNotEnabledMethodSource() {
        return Stream.of(
            arguments(false, null, new EntityClassSerializerModifierTestEntity()),
            arguments(false, Collections.singletonList("java.lang"), new EntityClassSerializerModifierTestEntity()),
            arguments(true, null, new EntityClassSerializerModifierTestEntityWithoutAnnotation())
        );
    }

    @Test
    void shouldSerializeClassNameForPackage() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(JacksonModuleUtil.classNameSerializerModule(false, Collections.singletonList(EntityClassSerializerModifierTestEntity.class.getPackage().getName())));

        // when
        Map<String, String> deserialized = objectMapper.readValue(objectMapper.writeValueAsString(new EntityClassSerializerModifierTestEntity()), MAP_TYPE_REFERENCE);

        // then
        assertThat(deserialized).containsEntry("class", EntityClassSerializerModifierTestEntity.class.getName());
    }
}
