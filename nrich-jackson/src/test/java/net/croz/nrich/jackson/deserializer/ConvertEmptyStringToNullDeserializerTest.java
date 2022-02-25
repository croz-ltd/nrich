package net.croz.nrich.jackson.deserializer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.jackson.module.JacksonModuleUtil;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ConvertEmptyStringToNullDeserializerTest {

    private static final TypeReference<Map<String, String>> MAP_TYPE_REFERENCE = new TypeReference<Map<String, String>>() {
    };

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(JacksonModuleUtil.convertEmptyStringToNullModule());

    @Test
    void shouldConvertEmptyStringsToNull() throws Exception {
        // given
        String emptyKey = "emptyKey";
        String nonEmptyKey = "nonEmptyKey";
        Map<String, String> testMap = new HashMap<>();

        testMap.put(emptyKey, "");
        testMap.put(nonEmptyKey, "non empty");

        // when
        Map<String, String> deserialized = objectMapper.readValue(objectMapper.writeValueAsString(testMap), MAP_TYPE_REFERENCE);

        // then
        assertThat(deserialized.get(emptyKey)).isNull();
        assertThat(deserialized.get(nonEmptyKey)).isNotEmpty();
    }
}
