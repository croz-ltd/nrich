package net.croz.nrich.search.converter;

import javax.persistence.metamodel.ManagedType;
import java.util.List;
import java.util.Map;

public interface StringToEntityPropertyMapConverter {

    Map<String, Object> convert(String value, List<String> propertyToSearchList, ManagedType<?> managedType);

}
