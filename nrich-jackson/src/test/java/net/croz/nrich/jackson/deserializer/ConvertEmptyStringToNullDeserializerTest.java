package net.croz.nrich.jackson.deserializer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.jackson.module.ConvertEmptyStringToNullModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ConvertEmptyStringToNullDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(ConvertEmptyStringToNullModule.convertEmptyStringToNullModule());
    }

    @Test
    void shouldConvertEmptyStringsToNull() throws Exception {
        // given
        final String key = "key";
        final Map<String, String> testMap = new HashMap<>();
        testMap.put(key, "");

        // when
        final Map<String, String> deserialized = objectMapper.readValue(objectMapper.writeValueAsString(testMap), new TypeReference<Map<String, String>>() {});

        // then
        assertThat(deserialized.get(key)).isNull();

    }
}
