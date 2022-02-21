package net.croz.nrich.search.api.converter;

import javax.persistence.metamodel.ManagedType;
import java.util.List;
import java.util.Map;

/**
 * Converts string value to a map that contains property name and property value. List of properties to search is used
 * to find properties on a managed type, conversion is attempted to property type and if conversion succeeds property is added to resulting map.
 */
public interface StringToEntityPropertyMapConverter {

    /**
     * Returns a map containing property name and property value. Resolved from propertyToSearchList found on {@link ManagedType} that can be converted from passed in string.
     *
     * @param value                value to convert
     * @param propertyToSearchList list of properties to convert to
     * @param managedType          entity managed type
     * @return map with all properties for which conversion succeeded
     */
    Map<String, Object> convert(String value, List<String> propertyToSearchList, ManagedType<?> managedType);

}
