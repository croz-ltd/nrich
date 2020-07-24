package net.croz.nrich.registry.api.configuration.model.property;

/**
 * Enum representing Javascript type, even though date is not a type it is added for easier handling on client.
 */
public enum JavascriptType {

    STRING, BOOLEAN, NUMBER, DATE, OBJECT;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
