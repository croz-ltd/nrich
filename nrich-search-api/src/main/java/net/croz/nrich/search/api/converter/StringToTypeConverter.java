package net.croz.nrich.search.api.converter;

/**
 * Converts string to required type.
 *
 * @param <T> type for conversion
 */
public interface StringToTypeConverter<T> {

    /**
     * Converts string value to required type.
     *
     * @param value        value to convert
     * @param requiredType type to convert to
     * @return converted value or null if conversion failed
     */
    T convert(String value, Class<?> requiredType);

    /**
     * Whether this converter supports conversion.
     *
     * @param requiredType type to convert to
     * @return whether this converter supports conversion
     */
    boolean supports(Class<?> requiredType);

}
