package net.croz.nrich.jackson.serializer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.jackson.module.JacksonModuleUtil;
import net.croz.nrich.jackson.serializer.stub.EntityClassSerializerModifierTestEntity;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EntityClassSerializerModifierTest {

    @Test
    void shouldSerializeClassNameForEntityClassWhenEnabled() throws Exception {
        // given
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(JacksonModuleUtil.classNameSerializerModule(true, null));

        // when
        final Map<String, String> deserialized = objectMapper.readValue(objectMapper.writeValueAsString(new EntityClassSerializerModifierTestEntity()), new TypeReference<Map<String, String>>() {
        });

        // then
        assertThat(deserialized).containsEntry("class", EntityClassSerializerModifierTestEntity.class.getName());
    }

    @Test
    void shouldSerializeClassNameForEntityClassWhenDisabled() throws Exception {
        // given
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(JacksonModuleUtil.classNameSerializerModule(false, null));

        // when
        final Map<String, String> deserialized = objectMapper.readValue(objectMapper.writeValueAsString(new EntityClassSerializerModifierTestEntity()), new TypeReference<Map<String, String>>() {
        });

        // then
        assertThat(deserialized.get("class")).isNull();
    }

    @Test
    void shouldSerializeClassNameForPackage() throws Exception {
        // given
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(JacksonModuleUtil.classNameSerializerModule(false, Collections.singletonList(EntityClassSerializerModifierTestEntity.class.getPackage().getName())));

        // when
        final Map<String, String> deserialized = objectMapper.readValue(objectMapper.writeValueAsString(new EntityClassSerializerModifierTestEntity()), new TypeReference<Map<String, String>>() {
        });

        // then
        assertThat(deserialized).containsEntry("class", EntityClassSerializerModifierTestEntity.class.getName());
    }
}
