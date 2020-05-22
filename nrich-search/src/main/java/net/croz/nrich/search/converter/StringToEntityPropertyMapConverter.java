package net.croz.nrich.search.converter;

import java.util.List;
import java.util.Map;

public interface StringToEntityPropertyMapConverter {

    Map<String, Object> convert(String value, List<String> propertyToSearchList, Class<?> entityType);

}
