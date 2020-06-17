package net.croz.nrich.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class ConvertEmptyStringToNullDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final JsonNode node = jsonParser.readValueAsTree();

        if (node.asText().isEmpty()) {
            return null;
        }

        return node.asText();
    }
}
