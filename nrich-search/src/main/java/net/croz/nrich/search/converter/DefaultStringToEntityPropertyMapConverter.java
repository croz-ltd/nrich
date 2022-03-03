package net.croz.nrich.search.converter;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.converter.StringToTypeConverter;
import net.croz.nrich.search.model.AttributeHolder;
import net.croz.nrich.search.support.JpaEntityAttributeResolver;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.persistence.metamodel.ManagedType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class DefaultStringToEntityPropertyMapConverter implements StringToEntityPropertyMapConverter {

    private final List<StringToTypeConverter<?>> converterList;

    @Override
    public Map<String, Object> convert(String value, List<String> propertyToSearchList, ManagedType<?> managedType) {
        if (value == null || CollectionUtils.isEmpty(propertyToSearchList)) {
            return Collections.emptyMap();
        }

        Assert.notNull(managedType, "Managed type cannot be null!");

        JpaEntityAttributeResolver attributeResolver = new JpaEntityAttributeResolver(managedType);

        Map<String, Object> resultMap = new HashMap<>();

        propertyToSearchList.forEach(property -> {
            AttributeHolder attributeHolder = attributeResolver.resolveAttributeByPath(property);

            if (attributeHolder.getAttribute() == null) {
                return;
            }

            Object convertedValue = doConversion(value, attributeHolder.getAttribute().getJavaType());

            resultMap.put(property, convertedValue);

        });

        return resultMap;
    }

    private Object doConversion(String searchTerm, Class<?> attributeType) {
        if (String.class.isAssignableFrom(attributeType)) {
            return searchTerm;
        }

        StringToTypeConverter<?> converter = converterList.stream()
            .filter(value -> value.supports(attributeType))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("No converter found for attribute type %s", attributeType.getName())));

        return converter.convert(searchTerm, attributeType);
    }
}
