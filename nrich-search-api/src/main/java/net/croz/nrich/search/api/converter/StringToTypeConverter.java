package net.croz.nrich.search.api.converter;

public interface StringToTypeConverter<T> {

    T convert(String value, Class<?> requiredType);

    boolean supports(Class<?> requiredType);

}
