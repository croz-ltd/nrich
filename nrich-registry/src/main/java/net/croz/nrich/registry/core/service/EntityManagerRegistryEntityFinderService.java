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

package net.croz.nrich.registry.core.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.api.core.service.RegistryEntityFinderService;
import net.croz.nrich.registry.core.constants.RegistryCoreConstants;
import net.croz.nrich.registry.core.constants.RegistryQueryConstants;
import net.croz.nrich.registry.core.support.ManagedTypeWrapper;
import org.modelmapper.ModelMapper;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EntityManagerRegistryEntityFinderService implements RegistryEntityFinderService {

    private final EntityManager entityManager;

    private final ModelMapper modelMapper;

    private final Map<String, ManagedTypeWrapper> classNameManagedTypeWrapperMap;

    @Override
    public <T> T findEntityInstance(Class<T> type, Object id) {
        QueryCondition queryCondition = queryWherePartWithParameterMap(type, id, true);

        String joinFetchQueryPart = classNameManagedTypeWrapperMap.get(type.getName()).getSingularAssociationList().stream()
            .map(attribute -> String.format(RegistryQueryConstants.FIND_QUERY_JOIN_FETCH, attribute.getPath()))
            .collect(Collectors.joining(RegistryCoreConstants.SPACE));

        String entityWithAlias = String.format(RegistryQueryConstants.PROPERTY_SPACE_FORMAT, type.getName(), RegistryQueryConstants.ENTITY_ALIAS);
        String querySelectPart = String.format(RegistryQueryConstants.PROPERTY_SPACE_FORMAT, entityWithAlias, joinFetchQueryPart.trim());
        String fullQuery = String.format(RegistryQueryConstants.FIND_QUERY, querySelectPart, queryCondition.wherePart);

        @SuppressWarnings("unchecked")
        TypedQuery<T> query = (TypedQuery<T>) entityManager.createQuery(fullQuery);

        queryCondition.parameterMap.forEach(query::setParameter);

        return query.getSingleResult();
    }

    @Override
    public <T> Map<String, Object> resolveIdParameterMap(Class<T> type, Object id) {
        return queryWherePartWithParameterMap(type, id, false).parameterMap;
    }

    private <T> QueryCondition queryWherePartWithParameterMap(Class<T> type, Object id, boolean convertParameterToQueryFormat) {
        ManagedTypeWrapper managedTypeWrapper = classNameManagedTypeWrapperMap.get(type.getName());

        List<String> wherePartList = new ArrayList<>();
        Map<String, Object> parameterMap = new HashMap<>();

        if (managedTypeWrapper.isIdClassIdentifier()) {
            Assert.isTrue(id instanceof Map, "Id should be instance of Map for @IdClass identifier");

            @SuppressWarnings("unchecked")
            Map<String, Object> idMap = ((Map<Object, Object>) id).entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue));

            Map<String, Class<?>> idClassPropertyMap = managedTypeWrapper.getIdClassPropertyMap();

            idClassPropertyMap.forEach((key, value) -> {
                Object convertedIdValue = modelMapper.map(idMap.get(key), value);

                wherePartList.add(toParameterExpression(key, convertParameterToQueryFormat));

                parameterMap.put(toParameterVariable(key, convertParameterToQueryFormat), convertedIdValue);
            });
        }
        else {
            Object convertedIdValue;
            if (managedTypeWrapper.isEmbeddedIdentifier()) {
                boolean isMapOrEmbeddedId = id instanceof Map || managedTypeWrapper.getEmbeddableIdType().getJavaType().equals(id.getClass());

                Assert.isTrue(isMapOrEmbeddedId, "Id should be instance of Map or EmbeddedId for @EmbeddedId identifier");

                convertedIdValue = modelMapper.map(id, managedTypeWrapper.getEmbeddableIdType().getJavaType());
            }
            else {
                convertedIdValue = modelMapper.map(id, managedTypeWrapper.getIdentifiableType().getIdType().getJavaType());
            }

            String idAttributeName = managedTypeWrapper.getIdAttributeName();

            wherePartList.add(toParameterExpression(idAttributeName, convertParameterToQueryFormat));

            parameterMap.put(toParameterVariable(idAttributeName, convertParameterToQueryFormat), convertedIdValue);
        }

        return new QueryCondition(String.join(RegistryQueryConstants.FIND_QUERY_SEPARATOR, wherePartList), parameterMap);
    }

    private String toParameterExpression(String key, boolean convertParameterToQueryFormat) {
        return String.format(RegistryQueryConstants.QUERY_PARAMETER_FORMAT, key, toParameterVariable(key, convertParameterToQueryFormat));
    }

    private String toParameterVariable(String key, boolean convertParameterToQueryFormat) {
        if (!convertParameterToQueryFormat) {
            return key;
        }

        String[] keyList = key.split(RegistryQueryConstants.PATH_SEPARATOR_REGEX);

        return Arrays.stream(keyList)
            .map(StringUtils::capitalize)
            .collect(Collectors.joining());
    }

    @RequiredArgsConstructor
    @Getter
    private static class QueryCondition {

        private final String wherePart;

        private final Map<String, Object> parameterMap;

    }
}
