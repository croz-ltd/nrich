package net.croz.nrich.registry.api.configuration.model.property;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

/**
 * Enum representing Javascript type, even though date is not a type it is added for easier handling on client.
 */
public enum JavascriptType {

    STRING, BOOLEAN, NUMBER, DATE, OBJECT;

    @JsonValue
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
