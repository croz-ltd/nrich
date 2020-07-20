package net.croz.nrich.registry.api.configuration.model;

// date is not a type but for easier handling on client making it one
public enum JavascriptType {

    STRING, BOOLEAN, NUMBER, DATE, OBJECT;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
