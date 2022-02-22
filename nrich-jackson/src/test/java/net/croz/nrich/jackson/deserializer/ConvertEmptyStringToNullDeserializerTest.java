package net.croz.nrich.jackson.deserializer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.jackson.module.JacksonModuleUtil;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ConvertEmptyStringToNullDeserializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(JacksonModuleUtil.convertEmptyStringToNullModule());

    @Test
    void shouldConvertEmptyStringsToNull() throws Exception {
        // given
        String key = "key";
        Map<String, String> testMap = new HashMap<>();
        testMap.put(key, "");

        // when
        Map<String, String> deserialized = objectMapper.readValue(objectMapper.writeValueAsString(testMap), new TypeReference<Map<String, String>>() {
        });

        // then
        assertThat(deserialized.get(key)).isNull();
    }
}
