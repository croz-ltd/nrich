package net.croz.nrich.search.converter.impl;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.search.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.converter.StringToTypeConverter;
import net.croz.nrich.search.support.JpaEntityAttributeResolver;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.ManagedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class StringToEntityPropertyMapConverterImpl implements StringToEntityPropertyMapConverter {

    private final EntityManager entityManager;

    private final List<StringToTypeConverter<?>> converterList;

    @Override
    public Map<String, Object> convert(final String value, final List<String> propertyToSearchList, final Class<?> entityType) {
        final ManagedType<?> managedType = entityManager.getMetamodel().managedType(entityType);

        Assert.notNull(managedType, "Managed type not found for type!");
        Assert.notEmpty(propertyToSearchList, "Property to search cannot be empty!");

        final JpaEntityAttributeResolver attributeResolver = new JpaEntityAttributeResolver(managedType);

        final Map<String, Object> resultMap = new HashMap<>();

        propertyToSearchList.forEach(property -> {
            final JpaEntityAttributeResolver.AttributeHolder attributeHolder = attributeResolver.resolveAttributeByPath(property);

            if (attributeHolder.getAttribute() == null) {
                return;
            }

            final Object convertedValue = doConversion(value, attributeHolder.getAttribute().getJavaType());

            resultMap.put(property, convertedValue);

        });

        return resultMap;
    }

    private Object doConversion(final String searchTerm, final Class<?> attributeType) {
        if (String.class.isAssignableFrom(attributeType)) {
            return searchTerm;
        }

        final StringToTypeConverter<?> converter = converterList.stream()
                .filter(value -> value.supports(attributeType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No converter found for attribute type %s", attributeType.getName())));

        return converter.convert(searchTerm, attributeType);
    }
}
