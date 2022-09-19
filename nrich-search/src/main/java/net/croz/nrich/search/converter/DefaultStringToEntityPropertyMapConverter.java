/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.search.converter;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.converter.StringToTypeConverter;
import net.croz.nrich.search.api.model.property.SearchPropertyConfiguration;
import net.croz.nrich.search.model.AttributeHolder;
import net.croz.nrich.search.support.JpaEntityAttributeResolver;
import net.croz.nrich.search.util.PropertyNameUtil;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import jakarta.persistence.metamodel.ManagedType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class DefaultStringToEntityPropertyMapConverter implements StringToEntityPropertyMapConverter {

    private final List<StringToTypeConverter<?>> converterList;

    @Override
    public Map<String, Object> convert(String value, List<String> propertyToSearchList, ManagedType<?> managedType, SearchPropertyConfiguration searchPropertyConfiguration) {
        if (value == null || CollectionUtils.isEmpty(propertyToSearchList)) {
            return Collections.emptyMap();
        }

        Assert.notNull(managedType, "Managed type cannot be null!");

        JpaEntityAttributeResolver attributeResolver = new JpaEntityAttributeResolver(managedType);

        Map<String, Object> resultMap = new HashMap<>();

        propertyToSearchList.forEach(property -> {
            AttributeHolder attributeHolder = attributeResolver.resolveAttributeByPath(property);

            if (!attributeHolder.isFound()) {
                String propertyWithoutSuffix = PropertyNameUtil.propertyNameWithoutSuffix(property, searchPropertyConfiguration);

                attributeHolder = attributeResolver.resolveAttributeByPath(propertyWithoutSuffix);
            }

            if (!attributeHolder.isFound()) {
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
